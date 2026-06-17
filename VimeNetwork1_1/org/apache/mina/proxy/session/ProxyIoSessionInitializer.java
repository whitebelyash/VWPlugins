package org.apache.mina.proxy.session;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionInitializer;

public class ProxyIoSessionInitializer implements IoSessionInitializer {
   private final IoSessionInitializer wrappedSessionInitializer;
   private final ProxyIoSession proxyIoSession;

   public ProxyIoSessionInitializer(IoSessionInitializer wrappedSessionInitializer, ProxyIoSession proxyIoSession) {
      this.wrappedSessionInitializer = wrappedSessionInitializer;
      this.proxyIoSession = proxyIoSession;
   }

   public ProxyIoSession getProxySession() {
      return this.proxyIoSession;
   }

   public void initializeSession(IoSession session, ConnectFuture future) {
      if (this.wrappedSessionInitializer != null) {
         this.wrappedSessionInitializer.initializeSession(session, future);
      }

      if (this.proxyIoSession != null) {
         this.proxyIoSession.setSession(session);
         session.setAttribute(ProxyIoSession.PROXY_SESSION, this.proxyIoSession);
      }

   }
}
