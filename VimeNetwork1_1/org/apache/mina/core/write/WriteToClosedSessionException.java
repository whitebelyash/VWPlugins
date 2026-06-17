package org.apache.mina.core.write;

import java.util.Collection;

public class WriteToClosedSessionException extends WriteException {
   private static final long serialVersionUID = 5550204573739301393L;

   public WriteToClosedSessionException(Collection requests, String message, Throwable cause) {
      super(requests, message, cause);
   }

   public WriteToClosedSessionException(Collection requests, String s) {
      super(requests, s);
   }

   public WriteToClosedSessionException(Collection requests, Throwable cause) {
      super(requests, cause);
   }

   public WriteToClosedSessionException(Collection requests) {
      super(requests);
   }

   public WriteToClosedSessionException(WriteRequest request, String message, Throwable cause) {
      super(request, message, cause);
   }

   public WriteToClosedSessionException(WriteRequest request, String s) {
      super(request, s);
   }

   public WriteToClosedSessionException(WriteRequest request, Throwable cause) {
      super(request, cause);
   }

   public WriteToClosedSessionException(WriteRequest request) {
      super(request);
   }
}
