package org.apache.mina.proxy.handlers.http;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionInitializer;
import org.apache.mina.proxy.AbstractProxyLogicHandler;
import org.apache.mina.proxy.ProxyAuthException;
import org.apache.mina.proxy.session.ProxyIoSession;
import org.apache.mina.proxy.utils.IoBufferDecoder;
import org.apache.mina.proxy.utils.StringUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHttpLogicHandler extends AbstractProxyLogicHandler {
   private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHttpLogicHandler.class);
   private static final String DECODER = AbstractHttpLogicHandler.class.getName() + ".Decoder";
   private static final byte[] HTTP_DELIMITER = new byte[]{13, 10, 13, 10};
   private static final byte[] CRLF_DELIMITER = new byte[]{13, 10};
   private IoBuffer responseData = null;
   private HttpProxyResponse parsedResponse = null;
   private int contentLength = -1;
   private boolean hasChunkedData;
   private boolean waitingChunkedData;
   private boolean waitingFooters;
   private int entityBodyStartPosition;
   private int entityBodyLimitPosition;

   public AbstractHttpLogicHandler(ProxyIoSession proxyIoSession) {
      super(proxyIoSession);
   }

   public synchronized void messageReceived(IoFilter.NextFilter nextFilter, IoBuffer buf) throws ProxyAuthException {
      LOGGER.debug(" messageReceived()");
      IoBufferDecoder decoder = (IoBufferDecoder)this.getSession().getAttribute(DECODER);
      if (decoder == null) {
         decoder = new IoBufferDecoder(HTTP_DELIMITER);
         this.getSession().setAttribute(DECODER, decoder);
      }

      try {
         if (this.parsedResponse == null) {
            this.responseData = decoder.decodeFully(buf);
            if (this.responseData == null) {
               return;
            }

            String responseHeader = this.responseData.getString(this.getProxyIoSession().getCharset().newDecoder());
            this.entityBodyStartPosition = this.responseData.position();
            LOGGER.debug((String)"  response header received:\n{}", (Object)responseHeader.replace("\r", "\\r").replace("\n", "\\n\n"));
            this.parsedResponse = this.decodeResponse(responseHeader);
            if (this.parsedResponse.getStatusCode() == 200 || this.parsedResponse.getStatusCode() >= 300 && this.parsedResponse.getStatusCode() <= 307) {
               buf.position(0);
               this.setHandshakeComplete();
               return;
            }

            String contentLengthHeader = StringUtilities.getSingleValuedHeader(this.parsedResponse.getHeaders(), "Content-Length");
            if (contentLengthHeader == null) {
               this.contentLength = 0;
            } else {
               this.contentLength = Integer.parseInt(contentLengthHeader.trim());
               decoder.setContentLength(this.contentLength, true);
            }
         }

         if (!this.hasChunkedData) {
            if (this.contentLength > 0) {
               IoBuffer tmp = decoder.decodeFully(buf);
               if (tmp == null) {
                  return;
               }

               this.responseData.setAutoExpand(true);
               this.responseData.put(tmp);
               this.contentLength = 0;
            }

            if ("chunked".equalsIgnoreCase(StringUtilities.getSingleValuedHeader(this.parsedResponse.getHeaders(), "Transfer-Encoding"))) {
               LOGGER.debug("Retrieving additional http response chunks");
               this.hasChunkedData = true;
               this.waitingChunkedData = true;
            }
         }

         if (this.hasChunkedData) {
            while(this.waitingChunkedData) {
               if (this.contentLength == 0) {
                  decoder.setDelimiter(CRLF_DELIMITER, false);
                  IoBuffer tmp = decoder.decodeFully(buf);
                  if (tmp == null) {
                     return;
                  }

                  String chunkSize = tmp.getString(this.getProxyIoSession().getCharset().newDecoder());
                  int pos = chunkSize.indexOf(59);
                  if (pos >= 0) {
                     chunkSize = chunkSize.substring(0, pos);
                  } else {
                     chunkSize = chunkSize.substring(0, chunkSize.length() - 2);
                  }

                  this.contentLength = Integer.decode("0x" + chunkSize);
                  if (this.contentLength > 0) {
                     this.contentLength += 2;
                     decoder.setContentLength(this.contentLength, true);
                  }
               }

               if (this.contentLength == 0) {
                  this.waitingChunkedData = false;
                  this.waitingFooters = true;
                  this.entityBodyLimitPosition = this.responseData.position();
                  break;
               }

               IoBuffer tmp = decoder.decodeFully(buf);
               if (tmp == null) {
                  return;
               }

               this.contentLength = 0;
               this.responseData.put(tmp);
               buf.position(buf.position());
            }

            while(this.waitingFooters) {
               decoder.setDelimiter(CRLF_DELIMITER, false);
               IoBuffer tmp = decoder.decodeFully(buf);
               if (tmp == null) {
                  return;
               }

               if (tmp.remaining() == 2) {
                  this.waitingFooters = false;
                  break;
               }

               String footer = tmp.getString(this.getProxyIoSession().getCharset().newDecoder());
               String[] f = footer.split(":\\s?", 2);
               StringUtilities.addValueToHeader(this.parsedResponse.getHeaders(), f[0], f[1], false);
               this.responseData.put(tmp);
               this.responseData.put(CRLF_DELIMITER);
            }
         }

         this.responseData.flip();
         LOGGER.debug((String)"  end of response received:\n{}", (Object)this.responseData.getString(this.getProxyIoSession().getCharset().newDecoder()));
         this.responseData.position(this.entityBodyStartPosition);
         this.responseData.limit(this.entityBodyLimitPosition);
         this.parsedResponse.setBody(this.responseData.getString(this.getProxyIoSession().getCharset().newDecoder()));
         this.responseData.free();
         this.responseData = null;
         this.handleResponse(this.parsedResponse);
         this.parsedResponse = null;
         this.hasChunkedData = false;
         this.contentLength = -1;
         decoder.setDelimiter(HTTP_DELIMITER, true);
         if (!this.isHandshakeComplete()) {
            this.doHandshake(nextFilter);
         }

      } catch (Exception ex) {
         if (ex instanceof ProxyAuthException) {
            throw (ProxyAuthException)ex;
         } else {
            throw new ProxyAuthException("Handshake failed", ex);
         }
      }
   }

   public abstract void handleResponse(HttpProxyResponse var1) throws ProxyAuthException;

   public void writeRequest(IoFilter.NextFilter nextFilter, HttpProxyRequest request) {
      ProxyIoSession proxyIoSession = this.getProxyIoSession();
      if (proxyIoSession.isReconnectionNeeded()) {
         this.reconnect(nextFilter, request);
      } else {
         this.writeRequest0(nextFilter, request);
      }

   }

   private void writeRequest0(IoFilter.NextFilter nextFilter, HttpProxyRequest request) {
      try {
         String data = request.toHttpString();
         IoBuffer buf = IoBuffer.wrap(data.getBytes(this.getProxyIoSession().getCharsetName()));
         LOGGER.debug((String)"   write:\n{}", (Object)data.replace("\r", "\\r").replace("\n", "\\n\n"));
         this.writeData(nextFilter, buf);
      } catch (UnsupportedEncodingException ex) {
         this.closeSession("Unable to send HTTP request: ", ex);
      }

   }

   private void reconnect(final IoFilter.NextFilter nextFilter, final HttpProxyRequest request) {
      LOGGER.debug("Reconnecting to proxy ...");
      final ProxyIoSession proxyIoSession = this.getProxyIoSession();
      proxyIoSession.getConnector().connect(new IoSessionInitializer() {
         public void initializeSession(IoSession session, ConnectFuture future) {
            AbstractHttpLogicHandler.LOGGER.debug((String)"Initializing new session: {}", (Object)session);
            session.setAttribute(ProxyIoSession.PROXY_SESSION, proxyIoSession);
            proxyIoSession.setSession(session);
            AbstractHttpLogicHandler.LOGGER.debug((String)"  setting up proxyIoSession: {}", (Object)proxyIoSession);
            future.addListener(new IoFutureListener() {
               public void operationComplete(ConnectFuture future) {
                  proxyIoSession.setReconnectionNeeded(false);
                  AbstractHttpLogicHandler.this.writeRequest0(nextFilter, request);
               }
            });
         }
      });
   }

   protected HttpProxyResponse decodeResponse(String response) throws Exception {
      LOGGER.debug("  parseResponse()");
      String[] responseLines = response.split("\r\n");
      String[] statusLine = responseLines[0].trim().split(" ", 2);
      if (statusLine.length < 2) {
         throw new Exception("Invalid response status line (" + statusLine + "). Response: " + response);
      } else if (!statusLine[1].matches("^\\d\\d\\d")) {
         throw new Exception("Invalid response code (" + statusLine[1] + "). Response: " + response);
      } else {
         Map<String, List<String>> headers = new HashMap();

         for(int i = 1; i < responseLines.length; ++i) {
            String[] args = responseLines[i].split(":\\s?", 2);
            StringUtilities.addValueToHeader(headers, args[0], args[1], false);
         }

         return new HttpProxyResponse(statusLine[0], statusLine[1], headers);
      }
   }
}
