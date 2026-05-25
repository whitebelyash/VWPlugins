package org.apache.mina.core.file;

import java.nio.channels.FileChannel;

public interface FileRegion {
   FileChannel getFileChannel();

   long getPosition();

   void update(long var1);

   long getRemainingBytes();

   long getWrittenBytes();

   String getFilename();
}
