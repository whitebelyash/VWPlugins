package org.apache.mina.core.session;

public interface IoSessionConfig {
   int getReadBufferSize();

   void setReadBufferSize(int var1);

   int getMinReadBufferSize();

   void setMinReadBufferSize(int var1);

   int getMaxReadBufferSize();

   void setMaxReadBufferSize(int var1);

   int getThroughputCalculationInterval();

   long getThroughputCalculationIntervalInMillis();

   void setThroughputCalculationInterval(int var1);

   int getIdleTime(IdleStatus var1);

   long getIdleTimeInMillis(IdleStatus var1);

   void setIdleTime(IdleStatus var1, int var2);

   int getReaderIdleTime();

   long getReaderIdleTimeInMillis();

   void setReaderIdleTime(int var1);

   int getWriterIdleTime();

   long getWriterIdleTimeInMillis();

   void setWriterIdleTime(int var1);

   int getBothIdleTime();

   long getBothIdleTimeInMillis();

   void setBothIdleTime(int var1);

   int getWriteTimeout();

   long getWriteTimeoutInMillis();

   void setWriteTimeout(int var1);

   boolean isUseReadOperation();

   void setUseReadOperation(boolean var1);

   void setAll(IoSessionConfig var1);
}
