package org.apache.mina.filter.keepalive;

import org.apache.mina.core.session.IoSession;

public interface KeepAliveMessageFactory {
   boolean isRequest(IoSession var1, Object var2);

   boolean isResponse(IoSession var1, Object var2);

   Object getRequest(IoSession var1);

   Object getResponse(IoSession var1, Object var2);
}
