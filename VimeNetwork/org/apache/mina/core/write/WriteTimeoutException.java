package org.apache.mina.core.write;

import java.util.Collection;

public class WriteTimeoutException extends WriteException {
   private static final long serialVersionUID = 3906931157944579121L;

   public WriteTimeoutException(Collection requests, String message, Throwable cause) {
      super(requests, message, cause);
   }

   public WriteTimeoutException(Collection requests, String s) {
      super(requests, s);
   }

   public WriteTimeoutException(Collection requests, Throwable cause) {
      super(requests, cause);
   }

   public WriteTimeoutException(Collection requests) {
      super(requests);
   }

   public WriteTimeoutException(WriteRequest request, String message, Throwable cause) {
      super(request, message, cause);
   }

   public WriteTimeoutException(WriteRequest request, String s) {
      super(request, s);
   }

   public WriteTimeoutException(WriteRequest request, Throwable cause) {
      super(request, cause);
   }

   public WriteTimeoutException(WriteRequest request) {
      super(request);
   }
}
