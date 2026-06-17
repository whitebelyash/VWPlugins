package org.apache.mina.handler.multiton;

import org.apache.mina.core.session.IoSession;

/** @deprecated */
@Deprecated
public interface SingleSessionIoHandlerFactory {
   SingleSessionIoHandler getHandler(IoSession var1) throws Exception;
}
