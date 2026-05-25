package org.apache.mina.core.write;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import org.apache.mina.util.MapBackedSet;

public class WriteException extends IOException {
   private static final long serialVersionUID = -4174407422754524197L;
   private final List requests;

   public WriteException(WriteRequest request) {
      this.requests = asRequestList(request);
   }

   public WriteException(WriteRequest request, String message) {
      super(message);
      this.requests = asRequestList(request);
   }

   public WriteException(WriteRequest request, String message, Throwable cause) {
      super(message);
      this.initCause(cause);
      this.requests = asRequestList(request);
   }

   public WriteException(WriteRequest request, Throwable cause) {
      this.initCause(cause);
      this.requests = asRequestList(request);
   }

   public WriteException(Collection requests) {
      this.requests = asRequestList(requests);
   }

   public WriteException(Collection requests, String message) {
      super(message);
      this.requests = asRequestList(requests);
   }

   public WriteException(Collection requests, String message, Throwable cause) {
      super(message);
      this.initCause(cause);
      this.requests = asRequestList(requests);
   }

   public WriteException(Collection requests, Throwable cause) {
      this.initCause(cause);
      this.requests = asRequestList(requests);
   }

   public List getRequests() {
      return this.requests;
   }

   public WriteRequest getRequest() {
      return (WriteRequest)this.requests.get(0);
   }

   private static List asRequestList(Collection requests) {
      if (requests == null) {
         throw new IllegalArgumentException("requests");
      } else if (requests.isEmpty()) {
         throw new IllegalArgumentException("requests is empty.");
      } else {
         Set<WriteRequest> newRequests = new MapBackedSet(new LinkedHashMap());

         for(WriteRequest r : requests) {
            newRequests.add(r.getOriginalRequest());
         }

         return Collections.unmodifiableList(new ArrayList(newRequests));
      }
   }

   private static List asRequestList(WriteRequest request) {
      if (request == null) {
         throw new IllegalArgumentException("request");
      } else {
         List<WriteRequest> requests = new ArrayList(1);
         requests.add(request.getOriginalRequest());
         return Collections.unmodifiableList(requests);
      }
   }
}
