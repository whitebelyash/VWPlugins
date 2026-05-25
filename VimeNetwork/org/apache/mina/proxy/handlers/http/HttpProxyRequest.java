package org.apache.mina.proxy.handlers.http;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.apache.mina.proxy.ProxyAuthException;
import org.apache.mina.proxy.handlers.ProxyRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProxyRequest extends ProxyRequest {
   private static final Logger logger = LoggerFactory.getLogger(HttpProxyRequest.class);
   private final String httpVerb;
   private final String httpURI;
   private String httpVersion;
   private String host;
   private Map headers;
   private transient Map properties;

   public HttpProxyRequest(InetSocketAddress endpointAddress) {
      this((InetSocketAddress)endpointAddress, "HTTP/1.0", (Map)null);
   }

   public HttpProxyRequest(InetSocketAddress endpointAddress, String httpVersion) {
      this((InetSocketAddress)endpointAddress, httpVersion, (Map)null);
   }

   public HttpProxyRequest(InetSocketAddress endpointAddress, String httpVersion, Map headers) {
      this.httpVerb = "CONNECT";
      if (!endpointAddress.isUnresolved()) {
         this.httpURI = endpointAddress.getHostName() + ":" + endpointAddress.getPort();
      } else {
         this.httpURI = endpointAddress.getAddress().getHostAddress() + ":" + endpointAddress.getPort();
      }

      this.httpVersion = httpVersion;
      this.headers = headers;
   }

   public HttpProxyRequest(String httpURI) {
      this("GET", httpURI, "HTTP/1.0", (Map)null);
   }

   public HttpProxyRequest(String httpURI, String httpVersion) {
      this("GET", httpURI, httpVersion, (Map)null);
   }

   public HttpProxyRequest(String httpVerb, String httpURI, String httpVersion) {
      this(httpVerb, httpURI, httpVersion, (Map)null);
   }

   public HttpProxyRequest(String httpVerb, String httpURI, String httpVersion, Map headers) {
      this.httpVerb = httpVerb;
      this.httpURI = httpURI;
      this.httpVersion = httpVersion;
      this.headers = headers;
   }

   public final String getHttpVerb() {
      return this.httpVerb;
   }

   public String getHttpVersion() {
      return this.httpVersion;
   }

   public void setHttpVersion(String httpVersion) {
      this.httpVersion = httpVersion;
   }

   public final synchronized String getHost() {
      if (this.host == null) {
         if (this.getEndpointAddress() != null && !this.getEndpointAddress().isUnresolved()) {
            this.host = this.getEndpointAddress().getHostName();
         }

         if (this.host == null && this.httpURI != null) {
            try {
               this.host = (new URL(this.httpURI)).getHost();
            } catch (MalformedURLException e) {
               logger.debug((String)"Malformed URL", (Throwable)e);
            }
         }
      }

      return this.host;
   }

   public final String getHttpURI() {
      return this.httpURI;
   }

   public final Map getHeaders() {
      return this.headers;
   }

   public final void setHeaders(Map headers) {
      this.headers = headers;
   }

   public Map getProperties() {
      return this.properties;
   }

   public void setProperties(Map properties) {
      this.properties = properties;
   }

   public void checkRequiredProperties(String... propNames) throws ProxyAuthException {
      StringBuilder sb = new StringBuilder();

      for(String propertyName : propNames) {
         if (this.properties.get(propertyName) == null) {
            sb.append(propertyName).append(' ');
         }
      }

      if (sb.length() > 0) {
         sb.append("property(ies) missing in request");
         throw new ProxyAuthException(sb.toString());
      }
   }

   public String toHttpString() {
      StringBuilder sb = new StringBuilder();
      sb.append(this.getHttpVerb()).append(' ').append(this.getHttpURI()).append(' ').append(this.getHttpVersion()).append("\r\n");
      boolean hostHeaderFound = false;
      if (this.getHeaders() != null) {
         for(Map.Entry header : this.getHeaders().entrySet()) {
            if (!hostHeaderFound) {
               hostHeaderFound = ((String)header.getKey()).equalsIgnoreCase("host");
            }

            for(String value : (List)header.getValue()) {
               sb.append((String)header.getKey()).append(": ").append(value).append("\r\n");
            }
         }

         if (!hostHeaderFound && this.getHttpVersion() == "HTTP/1.1") {
            sb.append("Host: ").append(this.getHost()).append("\r\n");
         }
      }

      sb.append("\r\n");
      return sb.toString();
   }
}
