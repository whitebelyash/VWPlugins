/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
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
import net.xtrafrancyz.VimeNetwork.api.mysql.Callback;
import net.xtrafrancyz.VimeNetwork.api.mysql.SelectCallback;
import net.xtrafrancyz.VimeNetwork.api.mysql.UpdateCallback;
import org.bukkit.plugin.Plugin;

public abstract class MysqlThread
extends Thread {
    private static final int TICK_INTERVAL = 1000;
    private static final String UNICODE_PARAMS = "useUnicode=true&characterEncoding=utf-8";
    private MysqlConfig config;
    private boolean useUnicode = false;
    private final Object lock = new Object();
    private final Queue<Query> queries;
    private volatile boolean running = false;
    private volatile boolean connected = false;
    protected Connection db;
    protected final Logger logger;

    public MysqlThread(Plugin plugin, String url, String user, String pass) {
        this(plugin, new MysqlConfigString(url, user, pass));
    }

    public MysqlThread(Plugin plugin, Supplier<String> url, Supplier<String> user, Supplier<String> pass) {
        this(plugin, new MysqlConfigSupplier(url, user, pass));
    }

    public MysqlThread(Plugin plugin, MysqlConfig config) {
        this.setName(plugin.getName() + " - Mysql");
        this.setDaemon(true);
        this.config = config;
        this.logger = plugin.getLogger();
        this.queries = new ConcurrentLinkedQueue<Query>();
        SafeRunnable.class.getName();
    }

    public void query(String query) {
        this.update(query, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void select(String query, SelectCallback callback) {
        this.queries.add(new Query(query, callback));
        Object object = this.lock;
        synchronized (object) {
            this.lock.notify();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update(String query, UpdateCallback callback) {
        this.queries.add(new Query(query, callback));
        Object object = this.lock;
        synchronized (object) {
            this.lock.notify();
        }
    }

    public void execute(File file) {
        this.safe(() -> this.execute(new FileInputStream(file)));
    }

    public void execute(InputStream is) {
        try (Scanner s = new Scanner(is).useDelimiter(";");){
            while (s.hasNext()) {
                String query = s.next().trim();
                if (query.isEmpty()) continue;
                this.query(query);
            }
        }
    }

    @Override
    public void start() {
        if (this.running) {
            return;
        }
        this.running = true;
        super.start();
    }

    public void finish() {
        if (!this.running) {
            return;
        }
        this.running = false;
        this.safe(this::join);
        if (this.db != null) {
            this.safe(this::checkConnection);
            this.safe(this::executeQueries);
            this.safe(this.db::close);
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
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        this.checkConnection();
        while (this.running) {
            if (!this.queries.isEmpty()) {
                if (this.checkConnection()) {
                    this.executeQueries();
                } else {
                    this.queries.clear();
                }
            }
            try {
                Object object = this.lock;
                synchronized (object) {
                    this.lock.wait(1000L);
                }
            }
            catch (InterruptedException e) {
                this.running = false;
            }
        }
    }

    private void executeQueries() {
        while (!this.queries.isEmpty()) {
            Query query = this.queries.poll();
            String q = this.onPreQuery(query.query);
            if (q == null) continue;
            try {
                Statement statement = this.db.createStatement();
                Throwable throwable = null;
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
                    }
                    catch (Exception e) {
                        this.logger.log(Level.SEVERE, "Query " + q + " is failed!", e);
                    }
                    this.onPostQuery(q, true);
                }
                catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                }
                finally {
                    if (statement == null) continue;
                    if (throwable != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable throwable3) {
                            throwable.addSuppressed(throwable3);
                        }
                        continue;
                    }
                    statement.close();
                }
            }
            catch (Exception e) {
                this.onPostQuery(q, false);
                if (e.getMessage() != null && e.getMessage().contains("try restarting transaction")) {
                    this.queries.add(query);
                    this.logger.warning(" Query " + q + " is failed! Restarting: " + e.getMessage());
                    continue;
                }
                this.logger.severe("Query " + q + " is failed! Message: " + e.getMessage());
            }
        }
    }

    private boolean checkConnection() {
        boolean state = false;
        try {
            if (this.db != null && !this.isValid()) {
                this.safe(this.db::close);
                this.db = null;
            }
            if (this.db == null) {
                this.connect();
            }
            state = this.db != null && this.isValid();
        }
        catch (Exception e) {
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
        }
        catch (SQLException ex) {
            this.logger.warning(ex.getMessage());
        }
    }

    private String addUnicodeParams(String url) {
        if (url.contains("?")) {
            if (url.contains(UNICODE_PARAMS)) {
                return url;
            }
            url = url + "&";
        } else {
            url = url + "?";
        }
        return url + UNICODE_PARAMS;
    }

    private boolean isValid() throws SQLException {
        return this.db.isValid(40);
    }

    private static long limit(long min, long value, long max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    private static class Query {
        String query;
        Callback callback;

        public Query(String query, Callback callback) {
            this.query = query;
            this.callback = callback;
        }
    }

    public static class MysqlConfigSupplier
    implements MysqlConfig {
        private final Supplier<String> url;
        private final Supplier<String> user;
        private final Supplier<String> pass;

        public MysqlConfigSupplier(Supplier<String> url, Supplier<String> user, Supplier<String> pass) {
            this.url = url;
            this.user = user;
            this.pass = pass;
        }

        @Override
        public String getUrl() {
            return this.url.get();
        }

        @Override
        public String getUser() {
            return this.user.get();
        }

        @Override
        public String getPass() {
            return this.pass.get();
        }
    }

    public static class MysqlConfigString
    implements MysqlConfig {
        private final String url;
        private final String user;
        private final String pass;

        public MysqlConfigString(String url, String user, String pass) {
            this.url = url;
            this.user = user;
            this.pass = pass;
        }

        @Override
        public String getUrl() {
            return this.url;
        }

        @Override
        public String getUser() {
            return this.user;
        }

        @Override
        public String getPass() {
            return this.pass;
        }
    }

    protected static interface SafeRunnable {
        public void run() throws Exception;
    }

    public static interface MysqlConfig {
        public String getUrl();

        public String getUser();

        public String getPass();
    }
}

