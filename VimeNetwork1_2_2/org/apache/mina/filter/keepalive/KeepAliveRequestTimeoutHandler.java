/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.keepalive;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface KeepAliveRequestTimeoutHandler {
    public static final KeepAliveRequestTimeoutHandler NOOP = new KeepAliveRequestTimeoutHandler(){

        @Override
        public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
        }
    };
    public static final KeepAliveRequestTimeoutHandler LOG = new KeepAliveRequestTimeoutHandler(){
        private final Logger LOGGER = LoggerFactory.getLogger(KeepAliveFilter.class);

        @Override
        public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
            this.LOGGER.warn("A keep-alive response message was not received within {} second(s).", (Object)filter.getRequestTimeout());
        }
    };
    public static final KeepAliveRequestTimeoutHandler EXCEPTION = new KeepAliveRequestTimeoutHandler(){

        @Override
        public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
            throw new KeepAliveRequestTimeoutException("A keep-alive response message was not received within " + filter.getRequestTimeout() + " second(s).");
        }
    };
    public static final KeepAliveRequestTimeoutHandler CLOSE = new KeepAliveRequestTimeoutHandler(){
        private final Logger LOGGER = LoggerFactory.getLogger(KeepAliveFilter.class);

        @Override
        public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
            this.LOGGER.warn("Closing the session because a keep-alive response message was not received within {} second(s).", (Object)filter.getRequestTimeout());
            session.closeNow();
        }
    };
    public static final KeepAliveRequestTimeoutHandler DEAF_SPEAKER = new KeepAliveRequestTimeoutHandler(){

        @Override
        public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
            throw new Error("Shouldn't be invoked.  Please file a bug report.");
        }
    };

    public void keepAliveRequestTimedOut(KeepAliveFilter var1, IoSession var2) throws Exception;
}

