package org.apache.mina.filter.stream;

import java.io.IOException;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.file.FileRegion;

public class FileRegionWriteFilter extends AbstractStreamWriteFilter {
   protected Class getMessageClass() {
      return FileRegion.class;
   }

   protected IoBuffer getNextBuffer(FileRegion fileRegion) throws IOException {
      if (fileRegion.getRemainingBytes() <= 0L) {
         return null;
      } else {
         int bufferSize = (int)Math.min((long)this.getWriteBufferSize(), fileRegion.getRemainingBytes());
         IoBuffer buffer = IoBuffer.allocate(bufferSize);
         int bytesRead = fileRegion.getFileChannel().read(buffer.buf(), fileRegion.getPosition());
         fileRegion.update((long)bytesRead);
         buffer.flip();
         return buffer;
      }
   }
}
