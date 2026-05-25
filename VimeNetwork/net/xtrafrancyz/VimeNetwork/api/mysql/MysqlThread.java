package net.xtrafrancyz.VimeNetwork.api.mysql;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.Plugin;

public abstract class MysqlThread extends Thread {
   private static final int TICK_INTERVAL = 1000;
   private static final String UNICODE_PARAMS = "useUnicode=true&characterEncoding=utf-8";
   private MysqlConfig config;
   private boolean useUnicode;
   private final Object lock;
   private final Queue queries;
   private volatile boolean running;
   private volatile boolean connected;
   protected Connection db;
   protected final Logger logger;

   public MysqlThread(Plugin plugin, String url, String user, String pass) {
      this(plugin, new MysqlConfigString(url, user, pass));
   }

   public MysqlThread(Plugin plugin, Supplier url, Supplier user, Supplier pass) {
      this(plugin, new MysqlConfigSupplier(url, user, pass));
   }

   public MysqlThread(Plugin plugin, MysqlConfig config) {
      this.useUnicode = false;
      this.lock = new Object();
      this.running = false;
      this.connected = false;
      this.setName(plugin.getName() + " - Mysql");
      this.setDaemon(true);
      this.config = config;
      this.logger = plugin.getLogger();
      this.queries = new ConcurrentLinkedQueue();
      SafeRunnable.class.getName();
   }

   public void query(String query) {
      this.update(query, (UpdateCallback)null);
   }

   public void select(String query, SelectCallback callback) {
      this.queries.add(new Query(query, callback));
      synchronized(this.lock) {
         this.lock.notify();
      }
   }

   public void update(String query, UpdateCallback callback) {
      this.queries.add(new Query(query, callback));
      synchronized(this.lock) {
         this.lock.notify();
      }
   }

   public void execute(File file) {
      this.safe(() -> this.execute((InputStream)(new FileInputStream(file))));
   }

   public void execute(InputStream is) {
      Scanner s = (new Scanner(is)).useDelimiter(";");
      Throwable var3 = null;

      try {
         while(s.hasNext()) {
            String query = s.next().trim();
            if (!query.isEmpty()) {
               this.query(query);
            }
         }
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (s != null) {
            if (var3 != null) {
               try {
                  s.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               s.close();
            }
         }

      }

   }

   public void start() {
      if (!this.running) {
         this.running = true;
         super.start();
      }
   }

   public void finish() {
      if (this.running) {
         this.running = false;
         this.safe(this::join);
         if (this.db != null) {
            this.safe(this::checkConnection);
            this.safe(this::executeQueries);
            Connection var10001 = this.db;
            this.safe(var10001::close);
         }

      }
   }

   public boolean isConnected() {
      return this.connected;
   }

   public boolean isRunning() {
      return this.running;
   }

   public void useUnicode() {
      this.useUnicode = true;
   }

   protected void onConnect() {
   }

   protected void onDisconnect() {
   }

   protected String onPreQuery(String query) {
      return query;
   }

   protected void onPostQuery(String query, boolean success) {
   }

   protected void safe(SafeRunnable r) {
      try {
         r.run();
      } catch (Exception var3) {
      }

   }

   public void run() {
      this.checkConnection();

      while(this.running) {
         if (!this.queries.isEmpty()) {
            if (this.checkConnection()) {
               this.executeQueries();
            } else {
               this.queries.clear();
            }
         }

         try {
            synchronized(this.lock) {
               this.lock.wait(1000L);
            }
         } catch (InterruptedException var4) {
            this.running = false;
         }
      }

   }

   private void executeQueries() {
      while(!this.queries.isEmpty()) {
         Query query = (Query)this.queries.poll();
         String q = this.onPreQuery(query.query);
         if (q != null) {
            try {
               Statement statement = this.db.createStatement();
               Throwable var4 = null;

               try {
                  boolean isSelect = statement.execute(q);

                  try {
                     if (isSelect) {
                        if (query.callback != null) {
                           ResultSet rs = statement.getResultSet();
                           ((SelectCallback)query.callback).done(rs);
                           rs.close();
                        }
                     } else if (query.callback != null) {
                        ((UpdateCallback)query.callback).done(statement.getUpdateCount());
                     }
                  } catch (Exception e) {
                     this.logger.log(Level.SEVERE, "Query " + q + " is failed!", e);
                  }

                  this.onPostQuery(q, true);
               } catch (Throwable var17) {
                  var4 = var17;
                  throw var17;
               } finally {
                  if (statement != null) {
                     if (var4 != null) {
                        try {
                           statement.close();
                        } catch (Throwable var15) {
                           var4.addSuppressed(var15);
                        }
                     } else {
                        statement.close();
                     }
                  }

               }
            } catch (Exception var19) {
               this.onPostQuery(q, false);
               if (var19.getMessage() != null && var19.getMessage().contains("try restarting transaction")) {
                  this.queries.add(query);
                  this.logger.warning(" Query " + q + " is failed! Restarting: " + var19.getMessage());
               } else {
                  this.logger.severe("Query " + q + " is failed! Message: " + var19.getMessage());
               }
            }
         }
      }

   }

   private boolean checkConnection() {
      boolean state = false;

      try {
         if (this.db != null && !this.isValid()) {
            Connection var10001 = this.db;
            this.safe(var10001::close);
            this.db = null;
         }

         if (this.db == null) {
            this.connect();
         }

         state = this.db != null && this.isValid();
      } catch (Exception e) {
         this.logger.log(Level.WARNING, "Error while connecting to database: {0}", e.getMessage());
      }

      if (this.connected != state) {
         this.connected = state;
         if (!this.connected) {
            this.onDisconnect();
         }
      }

      return state;
   }

   private void connect() {
      try {
         String url = this.config.getUrl();
         if (this.useUnicode) {
            url = this.addUnicodeParams(this.config.getUrl());
         }

         this.db = DriverManager.getConnection(url, this.config.getUser(), this.config.getPass());
         if (this.isValid()) {
            this.logger.info("Mysql connected.");
            this.onConnect();
         }
      } catch (SQLException ex) {
         this.logger.warning(ex.getMessage());
      }

   }

   private String addUnicodeParams(String url) {
      if (url.contains("?")) {
         if (url.contains("useUnicode=true&characterEncoding=utf-8")) {
            return url;
         }

         url = url + "&";
      } else {
         url = url + "?";
      }

      return url + "useUnicode=true&characterEncoding=utf-8";
   }

   private boolean isValid() throws SQLException {
      return this.db.isValid(40);
   }

   private static long limit(long min, long value, long max) {
      if (value < min) {
         return min;
      } else {
         return value > max ? max : value;
      }
   }

   public static class MysqlConfigString implements MysqlConfig {
      private final String url;
      private final String user;
      private final String pass;

      public MysqlConfigString(String url, String user, String pass) {
         this.url = url;
         this.user = user;
         this.pass = pass;
      }

      public String getUrl() {
         return this.url;
      }

      public String getUser() {
         return this.user;
      }

      public String getPass() {
         return this.pass;
      }
   }

   public static class MysqlConfigSupplier implements MysqlConfig {
      private final Supplier url;
      private final Supplier user;
      private final Supplier pass;

      public MysqlConfigSupplier(Supplier url, Supplier user, Supplier pass) {
         this.url = url;
         this.user = user;
         this.pass = pass;
      }

      public String getUrl() {
         return (String)this.url.get();
      }

      public String getUser() {
         return (String)this.user.get();
      }

      public String getPass() {
         return (String)this.pass.get();
      }
   }

   private static class Query {
      String query;
      Callback callback;

      public Query(String query, Callback callback) {
         this.query = query;
         this.callback = callback;
      }
   }

   public interface MysqlConfig {
      String getUrl();

      String getUser();

      String getPass();
   }

   protected interface SafeRunnable {
      void run() throws Exception;
   }
}
