package org.apache.mina.transport.socket.nio;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.service.AbstractIoAcceptor;
import org.apache.mina.core.service.AbstractIoService;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.ExpiringSessionRecycler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.session.IoSessionInitializer;
import org.apache.mina.core.session.IoSessionRecycler;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestQueue;
import org.apache.mina.transport.socket.DatagramAcceptor;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.DefaultDatagramSessionConfig;
import org.apache.mina.util.ExceptionMonitor;

public final class NioDatagramAcceptor extends AbstractIoAcceptor implements DatagramAcceptor, IoProcessor {
   private static final IoSessionRecycler DEFAULT_RECYCLER = new ExpiringSessionRecycler();
   private static final long SELECT_TIMEOUT = 1000L;
   private final Semaphore lock;
   private final Queue registerQueue;
   private final Queue cancelQueue;
   private final Queue flushingSessions;
   private final Map boundHandles;
   private IoSessionRecycler sessionRecycler;
   private final AbstractIoService.ServiceOperationFuture disposalFuture;
   private volatile boolean selectable;
   private Acceptor acceptor;
   private long lastIdleCheckTime;
   private volatile Selector selector;

   public NioDatagramAcceptor() {
      this(new DefaultDatagramSessionConfig(), (Executor)null);
   }

   public NioDatagramAcceptor(Executor executor) {
      this(new DefaultDatagramSessionConfig(), executor);
   }

   private NioDatagramAcceptor(IoSessionConfig sessionConfig, Executor executor) {
      super(sessionConfig, executor);
      this.lock = new Semaphore(1);
      this.registerQueue = new ConcurrentLinkedQueue();
      this.cancelQueue = new ConcurrentLinkedQueue();
      this.flushingSessions = new ConcurrentLinkedQueue();
      this.boundHandles = Collections.synchronizedMap(new HashMap());
      this.sessionRecycler = DEFAULT_RECYCLER;
      this.disposalFuture = new AbstractIoService.ServiceOperationFuture();

      try {
         this.init();
         this.selectable = true;
      } catch (RuntimeException e) {
         throw e;
      } catch (Exception e) {
         throw new RuntimeIoException("Failed to initialize.", e);
      } finally {
         if (!this.selectable) {
            try {
               this.destroy();
            } catch (Exception e) {
               ExceptionMonitor.getInstance().exceptionCaught(e);
            }
         }

      }

   }

   private int registerHandles() {
      while(true) {
         AbstractIoAcceptor.AcceptorOperationFuture req = (AbstractIoAcceptor.AcceptorOperationFuture)this.registerQueue.poll();
         if (req == null) {
            return 0;
         }

         Map<SocketAddress, DatagramChannel> newHandles = new HashMap();
         List<SocketAddress> localAddresses = req.getLocalAddresses();

         int var20;
         try {
            for(SocketAddress socketAddress : localAddresses) {
               DatagramChannel handle = this.open(socketAddress);
               newHandles.put(this.localAddress(handle), handle);
            }

            this.boundHandles.putAll(newHandles);
            this.getListeners().fireServiceActivated();
            req.setDone();
            var20 = newHandles.size();
         } catch (Exception e) {
            req.setException(e);
            continue;
         } finally {
            if (req.getException() != null) {
               for(DatagramChannel handle : newHandles.values()) {
                  try {
                     this.close(handle);
                  } catch (Exception e) {
                     ExceptionMonitor.getInstance().exceptionCaught(e);
                  }
               }

               this.wakeup();
            }

         }

         return var20;
      }
   }

   private void processReadySessions(Set handles) {
      Iterator<SelectionKey> iterator = handles.iterator();

      while(iterator.hasNext()) {
         SelectionKey key = (SelectionKey)iterator.next();
         DatagramChannel handle = (DatagramChannel)key.channel();
         iterator.remove();

         try {
            if (key.isValid() && key.isReadable()) {
               this.readHandle(handle);
            }

            if (key.isValid() && key.isWritable()) {
               for(IoSession session : this.getManagedSessions().values()) {
                  this.scheduleFlush((NioSession)session);
               }
            }
         } catch (Exception e) {
            ExceptionMonitor.getInstance().exceptionCaught(e);
         }
      }

   }

   private boolean scheduleFlush(NioSession session) {
      if (session.setScheduledForFlush(true)) {
         this.flushingSessions.add(session);
         return true;
      } else {
         return false;
      }
   }

   private void readHandle(DatagramChannel handle) throws Exception {
      IoBuffer readBuf = IoBuffer.allocate(this.getSessionConfig().getReadBufferSize());
      SocketAddress remoteAddress = this.receive(handle, readBuf);
      if (remoteAddress != null) {
         IoSession session = this.newSessionWithoutLock(remoteAddress, this.localAddress(handle));
         readBuf.flip();
         session.getFilterChain().fireMessageReceived(readBuf);
      }

   }

   private IoSession newSessionWithoutLock(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
      DatagramChannel handle = (DatagramChannel)this.boundHandles.get(localAddress);
      if (handle == null) {
         throw new IllegalArgumentException("Unknown local address: " + localAddress);
      } else {
         IoSession session;
         synchronized(this.sessionRecycler) {
            session = this.sessionRecycler.recycle(remoteAddress);
            if (session != null) {
               return session;
            }

            NioSession newSession = this.newSession(this, handle, remoteAddress);
            this.getSessionRecycler().put(newSession);
            session = newSession;
         }

         this.initSession(session, (IoFuture)null, (IoSessionInitializer)null);

         try {
            this.getFilterChainBuilder().buildFilterChain(session.getFilterChain());
            this.getListeners().fireSessionCreated(session);
         } catch (Exception e) {
            ExceptionMonitor.getInstance().exceptionCaught(e);
         }

         return session;
      }
   }

   private void flushSessions(long currentTime) {
      while(true) {
         NioSession session = (NioSession)this.flushingSessions.poll();
         if (session == null) {
            return;
         }

         session.unscheduledForFlush();

         try {
            boolean flushedAll = this.flush(session, currentTime);
            if (flushedAll && !session.getWriteRequestQueue().isEmpty(session) && !session.isScheduledForFlush()) {
               this.scheduleFlush(session);
            }
         } catch (Exception e) {
            session.getFilterChain().fireExceptionCaught(e);
         }
      }
   }

   private boolean flush(NioSession session, long currentTime) throws Exception {
      WriteRequestQueue writeRequestQueue = session.getWriteRequestQueue();
      int maxWrittenBytes = session.getConfig().getMaxReadBufferSize() + (session.getConfig().getMaxReadBufferSize() >>> 1);
      int writtenBytes = 0;

      try {
         while(true) {
            WriteRequest req;
            IoBuffer buf;
            SocketAddress destination;
            while(true) {
               req = session.getCurrentWriteRequest();
               if (req == null) {
                  req = writeRequestQueue.poll(session);
                  if (req == null) {
                     this.setInterestedInWrite(session, false);
                     return true;
                  }

                  session.setCurrentWriteRequest(req);
               }

               buf = (IoBuffer)req.getMessage();
               if (buf.remaining() != 0) {
                  destination = req.getDestination();
                  if (destination == null) {
                     destination = session.getRemoteAddress();
                  }
                  break;
               }

               session.setCurrentWriteRequest((WriteRequest)null);
               buf.reset();
               session.getFilterChain().fireMessageSent(req);
            }

            int localWrittenBytes = this.send(session, buf, destination);
            if (localWrittenBytes == 0 || writtenBytes >= maxWrittenBytes) {
               this.setInterestedInWrite(session, true);
               boolean var11 = false;
               return var11;
            }

            this.setInterestedInWrite(session, false);
            session.setCurrentWriteRequest((WriteRequest)null);
            writtenBytes += localWrittenBytes;
            buf.reset();
            session.getFilterChain().fireMessageSent(req);
         }
      } finally {
         session.increaseWrittenBytes(writtenBytes, currentTime);
      }
   }

   private int unregisterHandles() {
      int nHandles = 0;

      while(true) {
         AbstractIoAcceptor.AcceptorOperationFuture request = (AbstractIoAcceptor.AcceptorOperationFuture)this.cancelQueue.poll();
         if (request == null) {
            return nHandles;
         }

         for(SocketAddress socketAddress : request.getLocalAddresses()) {
            DatagramChannel handle = (DatagramChannel)this.boundHandles.remove(socketAddress);
            if (handle != null) {
               try {
                  this.close(handle);
                  this.wakeup();
               } catch (Exception e) {
                  ExceptionMonitor.getInstance().exceptionCaught(e);
               } finally {
                  ++nHandles;
               }
            }
         }

         request.setDone();
      }
   }

   private void notifyIdleSessions(long currentTime) {
      if (currentTime - this.lastIdleCheckTime >= 1000L) {
         this.lastIdleCheckTime = currentTime;
         AbstractIoSession.notifyIdleness(this.getListeners().getManagedSessions().values().iterator(), currentTime);
      }

   }

   private void startupAcceptor() throws InterruptedException {
      if (!this.selectable) {
         this.registerQueue.clear();
         this.cancelQueue.clear();
         this.flushingSessions.clear();
      }

      this.lock.acquire();
      if (this.acceptor == null) {
         this.acceptor = new Acceptor();
         this.executeWorker(this.acceptor);
      } else {
         this.lock.release();
      }

   }

   protected void init() throws Exception {
      this.selector = Selector.open();
   }

   public void add(NioSession session) {
   }

   protected final Set bindInternal(List localAddresses) throws Exception {
      AbstractIoAcceptor.AcceptorOperationFuture request = new AbstractIoAcceptor.AcceptorOperationFuture(localAddresses);
      this.registerQueue.add(request);
      this.startupAcceptor();

      try {
         this.lock.acquire();
         Thread.sleep(10L);
         this.wakeup();
      } finally {
         this.lock.release();
      }

      request.awaitUninterruptibly();
      if (request.getException() != null) {
         throw request.getException();
      } else {
         Set<SocketAddress> newLocalAddresses = new HashSet();

         for(DatagramChannel handle : this.boundHandles.values()) {
            newLocalAddresses.add(this.localAddress(handle));
         }

         return newLocalAddresses;
      }
   }

   protected void close(DatagramChannel handle) throws Exception {
      SelectionKey key = handle.keyFor(this.selector);
      if (key != null) {
         key.cancel();
      }

      handle.disconnect();
      handle.close();
   }

   protected void destroy() throws Exception {
      if (this.selector != null) {
         this.selector.close();
      }

   }

   protected void dispose0() throws Exception {
      this.unbind();
      this.startupAcceptor();
      this.wakeup();
   }

   public void flush(NioSession session) {
      if (this.scheduleFlush(session)) {
         this.wakeup();
      }

   }

   public InetSocketAddress getDefaultLocalAddress() {
      return (InetSocketAddress)super.getDefaultLocalAddress();
   }

   public InetSocketAddress getLocalAddress() {
      return (InetSocketAddress)super.getLocalAddress();
   }

   public DatagramSessionConfig getSessionConfig() {
      return (DatagramSessionConfig)this.sessionConfig;
   }

   public final IoSessionRecycler getSessionRecycler() {
      return this.sessionRecycler;
   }

   public TransportMetadata getTransportMetadata() {
      return NioDatagramSession.METADATA;
   }

   protected boolean isReadable(DatagramChannel handle) {
      SelectionKey key = handle.keyFor(this.selector);
      return key != null && key.isValid() ? key.isReadable() : false;
   }

   protected boolean isWritable(DatagramChannel handle) {
      SelectionKey key = handle.keyFor(this.selector);
      return key != null && key.isValid() ? key.isWritable() : false;
   }

   protected SocketAddress localAddress(DatagramChannel handle) throws Exception {
      InetSocketAddress inetSocketAddress = (InetSocketAddress)handle.socket().getLocalSocketAddress();
      InetAddress inetAddress = inetSocketAddress.getAddress();
      if (inetAddress instanceof Inet6Address && ((Inet6Address)inetAddress).isIPv4CompatibleAddress()) {
         byte[] ipV6Address = ((Inet6Address)inetAddress).getAddress();
         byte[] ipV4Address = new byte[4];
         System.arraycopy(ipV6Address, 12, ipV4Address, 0, 4);
         InetAddress inet4Adress = Inet4Address.getByAddress(ipV4Address);
         return new InetSocketAddress(inet4Adress, inetSocketAddress.getPort());
      } else {
         return inetSocketAddress;
      }
   }

   protected NioSession newSession(IoProcessor processor, DatagramChannel handle, SocketAddress remoteAddress) {
      SelectionKey key = handle.keyFor(this.selector);
      if (key != null && key.isValid()) {
         NioDatagramSession newSession = new NioDatagramSession(this, handle, processor, remoteAddress);
         newSession.setSelectionKey(key);
         return newSession;
      } else {
         return null;
      }
   }

   public final IoSession newSession(SocketAddress remoteAddress, SocketAddress localAddress) {
      if (this.isDisposing()) {
         throw new IllegalStateException("The Acceptor is being disposed.");
      } else if (remoteAddress == null) {
         throw new IllegalArgumentException("remoteAddress");
      } else {
         synchronized(this.bindLock) {
            if (!this.isActive()) {
               throw new IllegalStateException("Can't create a session from a unbound service.");
            } else {
               IoSession var10000;
               try {
                  var10000 = this.newSessionWithoutLock(remoteAddress, localAddress);
               } catch (RuntimeException e) {
                  throw e;
               } catch (Error e) {
                  throw e;
               } catch (Exception e) {
                  throw new RuntimeIoException("Failed to create a session.", e);
               }

               return var10000;
            }
         }
      }
   }

   protected DatagramChannel open(SocketAddress localAddress) throws Exception {
      DatagramChannel ch = DatagramChannel.open();
      boolean success = false;

      try {
         (new NioDatagramSessionConfig(ch)).setAll(this.getSessionConfig());
         ch.configureBlocking(false);

         try {
            ch.socket().bind(localAddress);
         } catch (IOException ioe) {
            String newMessage = "Error while binding on " + localAddress + "\n" + "original message : " + ioe.getMessage();
            Exception e = new IOException(newMessage);
            e.initCause(ioe.getCause());
            ch.close();
            throw e;
         }

         ch.register(this.selector, 1);
         success = true;
      } finally {
         if (!success) {
            this.close(ch);
         }

      }

      return ch;
   }

   protected SocketAddress receive(DatagramChannel handle, IoBuffer buffer) throws Exception {
      return handle.receive(buffer.buf());
   }

   public void remove(NioSession session) {
      this.getSessionRecycler().remove(session);
      this.getListeners().fireSessionDestroyed(session);
   }

   protected int select() throws Exception {
      return this.selector.select();
   }

   protected int select(long timeout) throws Exception {
      return this.selector.select(timeout);
   }

   protected Set selectedHandles() {
      return this.selector.selectedKeys();
   }

   protected int send(NioSession session, IoBuffer buffer, SocketAddress remoteAddress) throws Exception {
      return ((DatagramChannel)session.getChannel()).send(buffer.buf(), remoteAddress);
   }

   public void setDefaultLocalAddress(InetSocketAddress localAddress) {
      this.setDefaultLocalAddress(localAddress);
   }

   protected void setInterestedInWrite(NioSession session, boolean isInterested) throws Exception {
      SelectionKey key = session.getSelectionKey();
      if (key != null) {
         int newInterestOps = key.interestOps();
         if (isInterested) {
            newInterestOps |= 4;
         } else {
            newInterestOps &= -5;
         }

         key.interestOps(newInterestOps);
      }
   }

   public final void setSessionRecycler(IoSessionRecycler sessionRecycler) {
      synchronized(this.bindLock) {
         if (this.isActive()) {
            throw new IllegalStateException("sessionRecycler can't be set while the acceptor is bound.");
         } else {
            if (sessionRecycler == null) {
               sessionRecycler = DEFAULT_RECYCLER;
            }

            this.sessionRecycler = sessionRecycler;
         }
      }
   }

   protected final void unbind0(List localAddresses) throws Exception {
      AbstractIoAcceptor.AcceptorOperationFuture request = new AbstractIoAcceptor.AcceptorOperationFuture(localAddresses);
      this.cancelQueue.add(request);
      this.startupAcceptor();
      this.wakeup();
      request.awaitUninterruptibly();
      if (request.getException() != null) {
         throw request.getException();
      }
   }

   public void updateTrafficControl(NioSession session) {
      throw new UnsupportedOperationException();
   }

   protected void wakeup() {
      this.selector.wakeup();
   }

   public void write(NioSession session, WriteRequest writeRequest) {
      long currentTime = System.currentTimeMillis();
      WriteRequestQueue writeRequestQueue = session.getWriteRequestQueue();
      int maxWrittenBytes = session.getConfig().getMaxReadBufferSize() + (session.getConfig().getMaxReadBufferSize() >>> 1);
      int writtenBytes = 0;
      IoBuffer buf = (IoBuffer)writeRequest.getMessage();
      if (buf.remaining() == 0) {
         session.setCurrentWriteRequest((WriteRequest)null);
         buf.reset();
         session.getFilterChain().fireMessageSent(writeRequest);
      } else {
         try {
            while(true) {
               if (writeRequest == null) {
                  writeRequest = writeRequestQueue.poll(session);
                  if (writeRequest == null) {
                     this.setInterestedInWrite(session, false);
                     break;
                  }

                  session.setCurrentWriteRequest(writeRequest);
               }

               buf = (IoBuffer)writeRequest.getMessage();
               if (buf.remaining() == 0) {
                  session.setCurrentWriteRequest((WriteRequest)null);
                  buf.reset();
                  session.getFilterChain().fireMessageSent(writeRequest);
               } else {
                  SocketAddress destination = writeRequest.getDestination();
                  if (destination == null) {
                     destination = session.getRemoteAddress();
                  }

                  int localWrittenBytes = this.send(session, buf, destination);
                  if (localWrittenBytes != 0 && writtenBytes < maxWrittenBytes) {
                     this.setInterestedInWrite(session, false);
                     session.setCurrentWriteRequest((WriteRequest)null);
                     writtenBytes += localWrittenBytes;
                     buf.reset();
                     session.getFilterChain().fireMessageSent(writeRequest);
                     break;
                  }

                  this.setInterestedInWrite(session, true);
                  session.getWriteRequestQueue().offer(session, writeRequest);
                  this.scheduleFlush(session);
               }
            }
         } catch (Exception e) {
            session.getFilterChain().fireExceptionCaught(e);
         } finally {
            session.increaseWrittenBytes(writtenBytes, currentTime);
         }

      }
   }

   private class Acceptor implements Runnable {
      private Acceptor() {
      }

      public void run() {
         int nHandles = 0;
         NioDatagramAcceptor.this.lastIdleCheckTime = System.currentTimeMillis();
         NioDatagramAcceptor.this.lock.release();

         while(NioDatagramAcceptor.this.selectable) {
            try {
               int selected = NioDatagramAcceptor.this.select(1000L);
               nHandles += NioDatagramAcceptor.this.registerHandles();
               if (nHandles == 0) {
                  try {
                     NioDatagramAcceptor.this.lock.acquire();
                     if (NioDatagramAcceptor.this.registerQueue.isEmpty() && NioDatagramAcceptor.this.cancelQueue.isEmpty()) {
                        NioDatagramAcceptor.this.acceptor = null;
                        break;
                     }
                  } finally {
                     NioDatagramAcceptor.this.lock.release();
                  }
               }

               if (selected > 0) {
                  NioDatagramAcceptor.this.processReadySessions(NioDatagramAcceptor.this.selectedHandles());
               }

               long currentTime = System.currentTimeMillis();
               NioDatagramAcceptor.this.flushSessions(currentTime);
               nHandles -= NioDatagramAcceptor.this.unregisterHandles();
               NioDatagramAcceptor.this.notifyIdleSessions(currentTime);
            } catch (ClosedSelectorException cse) {
               ExceptionMonitor.getInstance().exceptionCaught(cse);
               break;
            } catch (Exception e) {
               ExceptionMonitor.getInstance().exceptionCaught(e);

               try {
                  Thread.sleep(1000L);
               } catch (InterruptedException var20) {
               }
            }
         }

         if (NioDatagramAcceptor.this.selectable && NioDatagramAcceptor.this.isDisposing()) {
            NioDatagramAcceptor.this.selectable = false;

            try {
               NioDatagramAcceptor.this.destroy();
            } catch (Exception e) {
               ExceptionMonitor.getInstance().exceptionCaught(e);
            } finally {
               NioDatagramAcceptor.this.disposalFuture.setValue(true);
            }
         }

      }
   }
}
