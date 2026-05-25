package org.apache.mina.core.service;

import java.lang.reflect.Constructor;
import java.nio.channels.spi.SelectorProvider;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.write.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleIoProcessorPool implements IoProcessor {
   private static final Logger LOGGER = LoggerFactory.getLogger(SimpleIoProcessorPool.class);
   private static final int DEFAULT_SIZE = Runtime.getRuntime().availableProcessors() + 1;
   private static final AttributeKey PROCESSOR = new AttributeKey(SimpleIoProcessorPool.class, "processor");
   private final IoProcessor[] pool;
   private final Executor executor;
   private final boolean createdExecutor;
   private final Object disposalLock;
   private volatile boolean disposing;
   private volatile boolean disposed;

   public SimpleIoProcessorPool(Class processorType) {
      this(processorType, (Executor)null, DEFAULT_SIZE, (SelectorProvider)null);
   }

   public SimpleIoProcessorPool(Class processorType, int size) {
      this(processorType, (Executor)null, size, (SelectorProvider)null);
   }

   public SimpleIoProcessorPool(Class processorType, int size, SelectorProvider selectorProvider) {
      this(processorType, (Executor)null, size, selectorProvider);
   }

   public SimpleIoProcessorPool(Class processorType, Executor executor) {
      this(processorType, executor, DEFAULT_SIZE, (SelectorProvider)null);
   }

   public SimpleIoProcessorPool(Class processorType, Executor executor, int size, SelectorProvider selectorProvider) {
      this.disposalLock = new Object();
      if (processorType == null) {
         throw new IllegalArgumentException("processorType");
      } else if (size <= 0) {
         throw new IllegalArgumentException("size: " + size + " (expected: positive integer)");
      } else {
         this.createdExecutor = executor == null;
         if (this.createdExecutor) {
            this.executor = Executors.newCachedThreadPool();
            ((ThreadPoolExecutor)this.executor).setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
         } else {
            this.executor = executor;
         }

         this.pool = new IoProcessor[size];
         boolean success = false;
         Constructor<? extends IoProcessor<S>> processorConstructor = null;
         boolean usesExecutorArg = true;

         try {
            try {
               try {
                  processorConstructor = processorType.getConstructor(ExecutorService.class);
                  this.pool[0] = (IoProcessor)processorConstructor.newInstance(this.executor);
               } catch (NoSuchMethodException var22) {
                  try {
                     if (selectorProvider == null) {
                        processorConstructor = processorType.getConstructor(Executor.class);
                        this.pool[0] = (IoProcessor)processorConstructor.newInstance(this.executor);
                     } else {
                        processorConstructor = processorType.getConstructor(Executor.class, SelectorProvider.class);
                        this.pool[0] = (IoProcessor)processorConstructor.newInstance(this.executor, selectorProvider);
                     }
                  } catch (NoSuchMethodException var21) {
                     try {
                        processorConstructor = processorType.getConstructor();
                        usesExecutorArg = false;
                        this.pool[0] = (IoProcessor)processorConstructor.newInstance();
                     } catch (NoSuchMethodException var20) {
                     }
                  }
               }
            } catch (RuntimeException re) {
               LOGGER.error((String)"Cannot create an IoProcessor :{}", (Object)re.getMessage());
               throw re;
            } catch (Exception e) {
               String msg = "Failed to create a new instance of " + processorType.getName() + ":" + e.getMessage();
               LOGGER.error((String)msg, (Throwable)e);
               throw new RuntimeIoException(msg, e);
            }

            if (processorConstructor == null) {
               String msg = processorType + " must have a public constructor with one " + ExecutorService.class.getSimpleName() + " parameter, a public constructor with one " + Executor.class.getSimpleName() + " parameter or a public default constructor.";
               LOGGER.error(msg);
               throw new IllegalArgumentException(msg);
            }

            for(int i = 1; i < this.pool.length; ++i) {
               try {
                  if (usesExecutorArg) {
                     if (selectorProvider == null) {
                        this.pool[i] = (IoProcessor)processorConstructor.newInstance(this.executor);
                     } else {
                        this.pool[i] = (IoProcessor)processorConstructor.newInstance(this.executor, selectorProvider);
                     }
                  } else {
                     this.pool[i] = (IoProcessor)processorConstructor.newInstance();
                  }
               } catch (Exception var19) {
               }
            }

            success = true;
         } finally {
            if (!success) {
               this.dispose();
            }

         }

      }
   }

   public final void add(AbstractIoSession session) {
      this.getProcessor(session).add(session);
   }

   public final void flush(AbstractIoSession session) {
      this.getProcessor(session).flush(session);
   }

   public final void write(AbstractIoSession session, WriteRequest writeRequest) {
      this.getProcessor(session).write(session, writeRequest);
   }

   public final void remove(AbstractIoSession session) {
      this.getProcessor(session).remove(session);
   }

   public final void updateTrafficControl(AbstractIoSession session) {
      this.getProcessor(session).updateTrafficControl(session);
   }

   public boolean isDisposed() {
      return this.disposed;
   }

   public boolean isDisposing() {
      return this.disposing;
   }

   public final void dispose() {
      if (!this.disposed) {
         synchronized(this.disposalLock) {
            if (!this.disposing) {
               this.disposing = true;

               for(IoProcessor ioProcessor : this.pool) {
                  if (ioProcessor != null && !ioProcessor.isDisposing()) {
                     try {
                        ioProcessor.dispose();
                     } catch (Exception e) {
                        LOGGER.warn((String)"Failed to dispose the {} IoProcessor.", (Object)ioProcessor.getClass().getSimpleName(), (Object)e);
                     }
                  }
               }

               if (this.createdExecutor) {
                  ((ExecutorService)this.executor).shutdown();
               }
            }

            Arrays.fill(this.pool, (Object)null);
            this.disposed = true;
         }
      }
   }

   private IoProcessor getProcessor(AbstractIoSession session) {
      IoProcessor<S> processor = (IoProcessor)session.getAttribute(PROCESSOR);
      if (processor == null) {
         if (this.disposed || this.disposing) {
            throw new IllegalStateException("A disposed processor cannot be accessed.");
         }

         processor = this.pool[Math.abs((int)session.getId()) % this.pool.length];
         if (processor == null) {
            throw new IllegalStateException("A disposed processor cannot be accessed.");
         }

         session.setAttributeIfAbsent(PROCESSOR, processor);
      }

      return processor;
   }
}
