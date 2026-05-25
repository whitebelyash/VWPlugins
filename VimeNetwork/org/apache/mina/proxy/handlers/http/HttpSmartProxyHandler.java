package org.apache.mina.proxy.handlers.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.proxy.ProxyAuthException;
import org.apache.mina.proxy.session.ProxyIoSession;
import org.apache.mina.proxy.utils.StringUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpSmartProxyHandler extends AbstractHttpLogicHandler {
   private static final Logger logger = LoggerFactory.getLogger(HttpSmartProxyHandler.class);
   private boolean requestSent = false;
   private AbstractAuthLogicHandler authHandler;

   public HttpSmartProxyHandler(ProxyIoSession proxyIoSession) {
      super(proxyIoSession);
   }

   public void doHandshake(IoFilter.NextFilter nextFilter) throws ProxyAuthException {
      logger.debug(" doHandshake()");
      if (this.authHandler != null) {
         this.authHandler.doHandshake(nextFilter);
      } else {
         if (this.requestSent) {
            throw new ProxyAuthException("Authentication request already sent");
         }

         logger.debug("  sending HTTP request");
         HttpProxyRequest req = (HttpProxyRequest)this.getProxyIoSession().getRequest();
         Map<String, List<String>> headers = (Map<String, List<String>>)(req.getHeaders() != null ? req.getHeaders() : new HashMap());
         AbstractAuthLogicHandler.addKeepAliveHeaders(headers);
         req.setHeaders(headers);
         this.writeRequest(nextFilter, req);
         this.requestSent = true;
      }

   }

   private void autoSelectAuthHandler(HttpProxyResponse response) throws ProxyAuthException {
      List<String> values = (List)response.getHeaders().get("Proxy-Authenticate");
      ProxyIoSession proxyIoSession = this.getProxyIoSession();
      if (values != null && values.size() != 0) {
         if (this.getProxyIoSession().getPreferedOrder() == null) {
            int method = -1;

            for(String proxyAuthHeader : values) {
               proxyAuthHeader = proxyAuthHeader.toLowerCase();
               if (proxyAuthHeader.contains("ntlm")) {
                  method = HttpAuthenticationMethods.NTLM.getId();
                  break;
               }

               if (proxyAuthHeader.contains("digest") && method != HttpAuthenticationMethods.NTLM.getId()) {
                  method = HttpAuthenticationMethods.DIGEST.getId();
               } else if (proxyAuthHeader.contains("basic") && method == -1) {
                  method = HttpAuthenticationMethods.BASIC.getId();
               }
            }

            if (method != -1) {
               try {
                  this.authHandler = HttpAuthenticationMethods.getNewHandler(method, proxyIoSession);
               } catch (Exception ex) {
                  logger.debug((String)"Following exception occured:", (Throwable)ex);
               }
            }

            if (this.authHandler == null) {
               this.authHandler = HttpAuthenticationMethods.NO_AUTH.getNewHandler(proxyIoSession);
            }
         } else {
            for(HttpAuthenticationMethods method : proxyIoSession.getPreferedOrder()) {
               if (this.authHandler != null) {
                  break;
               }

               if (method == HttpAuthenticationMethods.NO_AUTH) {
                  this.authHandler = HttpAuthenticationMethods.NO_AUTH.getNewHandler(proxyIoSession);
                  break;
               }

               for(String proxyAuthHeader : values) {
                  proxyAuthHeader = proxyAuthHeader.toLowerCase();

                  try {
                     if (proxyAuthHeader.contains("basic") && method == HttpAuthenticationMethods.BASIC) {
                        this.authHandler = HttpAuthenticationMethods.BASIC.getNewHandler(proxyIoSession);
                        break;
                     }

                     if (proxyAuthHeader.contains("digest") && method == HttpAuthenticationMethods.DIGEST) {
                        this.authHandler = HttpAuthenticationMethods.DIGEST.getNewHandler(proxyIoSession);
                        break;
                     }

                     if (proxyAuthHeader.contains("ntlm") && method == HttpAuthenticationMethods.NTLM) {
                        this.authHandler = HttpAuthenticationMethods.NTLM.getNewHandler(proxyIoSession);
                        break;
                     }
                  } catch (Exception ex) {
                     logger.debug((String)"Following exception occured:", (Throwable)ex);
                  }
               }
            }
         }
      } else {
         this.authHandler = HttpAuthenticationMethods.NO_AUTH.getNewHandler(proxyIoSession);
      }

      if (this.authHandler == null) {
         throw new ProxyAuthException("Unknown authentication mechanism(s): " + values);
      }
   }

   public void handleResponse(HttpProxyResponse response) throws ProxyAuthException {
      if (!this.isHandshakeComplete() && ("close".equalsIgnoreCase(StringUtilities.getSingleValuedHeader(response.getHeaders(), "Proxy-Connection")) || "close".equalsIgnoreCase(StringUtilities.getSingleValuedHeader(response.getHeaders(), "Connection")))) {
         this.getProxyIoSession().setReconnectionNeeded(true);
      }

      if (response.getStatusCode() == 407) {
         if (this.authHandler == null) {
            this.autoSelectAuthHandler(response);
         }

         this.authHandler.handleResponse(response);
      } else {
         throw new ProxyAuthException("Error: unexpected response code " + response.getStatusLine() + " received from proxy.");
      }
   }
}
