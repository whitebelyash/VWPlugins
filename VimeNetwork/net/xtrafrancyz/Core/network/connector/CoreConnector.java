package net.xtrafrancyz.Core.network.connector;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.filter.PacketDecoder;
import net.xtrafrancyz.Core.network.filter.PacketEncoder;
import net.xtrafrancyz.Core.network.packet.Packet;
import net.xtrafrancyz.Core.network.packet.ResponsePacket;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.DefaultConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class CoreConnector {
   private AtomicInteger callbackCounter = new AtomicInteger(0);
   Map callbacks = new ConcurrentHashMap();
   private final String host;
   private final int port;
   final Logger logger;
   NioSocketConnector connector;
   volatile IoSession session;
   PacketHandler mainHandler;
   Queue sendQueue;
   volatile ConnectFuture connectFuture;
   Map customHandlers;
   List connectListeners;
   List disconnectListeners;
   private WatcherThread watcher;
   private boolean lastConnectIsSuccess = true;
   long lastConnectAttempt = 0L;

   public CoreConnector(Logger logger, String host, int port) {
      this.host = host;
      this.port = port;
      this.logger = logger;
      this.connectListeners = new ArrayList();
      this.disconnectListeners = new ArrayList();
      this.customHandlers = new HashMap();
      this.sendQueue = new ConcurrentLinkedQueue();
   }

   public void setMainHandler(PacketHandler handler) {
      this.mainHandler = handler;
   }

   public void addHandler(Class clazz, Consumer listener) {
      Integer id = (Integer)Packet.packetToId.get(clazz);
      if (id == null) {
         throw new IllegalArgumentException("Packet for class " + clazz.getName() + " is not registered");
      } else {
         ((List)this.customHandlers.computeIfAbsent(id, (k) -> new ArrayList())).add(listener);
      }
   }

   public void removeHandler(Class clazz, Consumer listener) {
      Integer id = (Integer)Packet.packetToId.get(clazz);
      if (id == null) {
         throw new IllegalArgumentException("Packet for class " + clazz.getName() + " is not registered");
      } else {
         List<Consumer<Packet>> consumers = (List)this.customHandlers.get(id);
         if (consumers != null) {
            consumers.remove(listener);
         }

      }
   }

   public void addConnectListener(Runnable listener) {
      this.connectListeners.add(listener);
      if (this.isConnected()) {
         listener.run();
      }

   }

   public void addDisconnectListener(Runnable listener) {
      this.disconnectListeners.add(listener);
   }

   public void connect() {
      if (this.connectFuture == null) {
         if (this.mainHandler == null) {
            throw new IllegalStateException("Main handler is not set");
         } else {
            if (this.connector == null) {
               this.connector = new NioSocketConnector();
               this.connector.setDefaultRemoteAddress(new InetSocketAddress(this.host, this.port));
               this.connector.setConnectTimeoutMillis(3000L);
               this.connector.getSessionConfig().setWriterIdleTime(10);
               this.connector.getSessionConfig().setWriteTimeout(3);
               this.connector.setHandler(new CoreIoHandler(this));
               this.connector.getFilterChain().addLast("protocol", new ProtocolCodecFilter(new PacketEncoder(), new PacketDecoder()));
            }

            this.lastConnectAttempt = System.currentTimeMillis();
            this.connectFuture = this.connector.connect();
            this.connectFuture.addListener((future) -> {
               Throwable ex = ((DefaultConnectFuture)future).getException();
               if (ex == null) {
                  this.session = future.getSession();
                  this.lastConnectIsSuccess = true;
               } else if (this.lastConnectIsSuccess) {
                  this.logger.log(Level.WARNING, "[Core] Connection error: " + ex.getClass().getName() + ": " + ex.getMessage());
                  this.lastConnectIsSuccess = false;
               }

               this.connectFuture = null;
            });
            if (this.watcher == null) {
               this.watcher = new WatcherThread(this);
               this.watcher.start();
            }

         }
      }
   }

   public void disconnect() {
      if (this.watcher != null) {
         this.watcher.interrupt();
         this.watcher = null;
      }

      if (this.connectFuture != null) {
         this.connectFuture.cancel();
         this.connectFuture = null;
      }

      if (this.isConnected()) {
         if (this.session.isConnected() && !this.session.isClosing() && !this.session.isWriteSuspended()) {
            this.session.closeOnFlush();
         } else {
            this.session.closeNow();
         }

         this.session = null;
      }

   }

   public void dispose() {
      if (this.isConnected()) {
         this.disconnect();
      }

      timeout(() -> this.connector.dispose(true), 1500L);
      this.connector = null;
   }

   public boolean isConnected() {
      return this.session != null;
   }

   public NioSocketConnector getConnector() {
      return this.connector;
   }

   public void sendPacket(Packet packet) {
      if (this.isConnected()) {
         this.session.write(packet);
      } else {
         this.sendQueue.add(packet);
      }

   }

   public void sendPacket(ResponsePacket packet, CoreCallback callback, long timeout) {
      this.sendPacket(packet, callback, timeout, (Runnable)null);
   }

   public void sendPacket(ResponsePacket packet, CoreCallback callback, long timeout, Runnable onTimeout) {
      if (timeout <= 0L) {
         throw new IllegalArgumentException("Callback timeout must be greater than zero");
      } else {
         CallbackData data = new CallbackData(this.callbackCounter.getAndIncrement(), callback, timeout, onTimeout);
         this.callbacks.put(data.id, data);
         packet.pResponseId = data.id;
         this.sendPacket(packet);
      }
   }

   private static void timeout(Runnable task, long timeoutMillis) {
      ExecutorService executor = Executors.newSingleThreadExecutor();
      Future future = executor.submit(task);

      try {
         future.get(timeoutMillis, TimeUnit.MILLISECONDS);
      } catch (Exception var6) {
         future.cancel(true);
      }

      executor.shutdownNow();
   }
}
