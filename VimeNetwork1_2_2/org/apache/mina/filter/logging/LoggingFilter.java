/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.logging;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.filter.logging.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingFilter
extends IoFilterAdapter {
    private final String name;
    private final Logger logger;
    private LogLevel exceptionCaughtLevel = LogLevel.WARN;
    private LogLevel messageSentLevel = LogLevel.INFO;
    private LogLevel messageReceivedLevel = LogLevel.INFO;
    private LogLevel sessionCreatedLevel = LogLevel.INFO;
    private LogLevel sessionOpenedLevel = LogLevel.INFO;
    private LogLevel sessionIdleLevel = LogLevel.INFO;
    private LogLevel sessionClosedLevel = LogLevel.INFO;

    public LoggingFilter() {
        this(LoggingFilter.class.getName());
    }

    public LoggingFilter(Class<?> clazz) {
        this(clazz.getName());
    }

    public LoggingFilter(String name) {
        this.name = name == null ? LoggingFilter.class.getName() : name;
        this.logger = LoggerFactory.getLogger(this.name);
    }

    public String getName() {
        return this.name;
    }

    private void log(LogLevel eventLevel, String message, Throwable cause) {
        switch (eventLevel) {
            case TRACE: {
                this.logger.trace(message, cause);
                return;
            }
            case DEBUG: {
                this.logger.debug(message, cause);
                return;
            }
            case INFO: {
                this.logger.info(message, cause);
                return;
            }
            case WARN: {
                this.logger.warn(message, cause);
                return;
            }
            case ERROR: {
                this.logger.error(message, cause);
                return;
            }
        }
    }

    private void log(LogLevel eventLevel, String message, Object param) {
        switch (eventLevel) {
            case TRACE: {
                this.logger.trace(message, param);
                return;
            }
            case DEBUG: {
                this.logger.debug(message, param);
                return;
            }
            case INFO: {
                this.logger.info(message, param);
                return;
            }
            case WARN: {
                this.logger.warn(message, param);
                return;
            }
            case ERROR: {
                this.logger.error(message, param);
                return;
            }
        }
    }

    private void log(LogLevel eventLevel, String message) {
        switch (eventLevel) {
            case TRACE: {
                this.logger.trace(message);
                return;
            }
            case DEBUG: {
                this.logger.debug(message);
                return;
            }
            case INFO: {
                this.logger.info(message);
                return;
            }
            case WARN: {
                this.logger.warn(message);
                return;
            }
            case ERROR: {
                this.logger.error(message);
                return;
            }
        }
    }

    @Override
    public void exceptionCaught(IoFilter.NextFilter nextFilter, IoSession session, Throwable cause) throws Exception {
        this.log(this.exceptionCaughtLevel, "EXCEPTION :", cause);
        nextFilter.exceptionCaught(session, cause);
    }

    @Override
    public void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message) throws Exception {
        this.log(this.messageReceivedLevel, "RECEIVED: {}", message);
        nextFilter.messageReceived(session, message);
    }

    @Override
    public void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
        this.log(this.messageSentLevel, "SENT: {}", writeRequest.getOriginalRequest().getMessage());
        nextFilter.messageSent(session, writeRequest);
    }

    @Override
    public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
        this.log(this.sessionCreatedLevel, "CREATED");
        nextFilter.sessionCreated(session);
    }

    @Override
    public void sessionOpened(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
        this.log(this.sessionOpenedLevel, "OPENED");
        nextFilter.sessionOpened(session);
    }

    @Override
    public void sessionIdle(IoFilter.NextFilter nextFilter, IoSession session, IdleStatus status) throws Exception {
        this.log(this.sessionIdleLevel, "IDLE");
        nextFilter.sessionIdle(session, status);
    }

    @Override
    public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
        this.log(this.sessionClosedLevel, "CLOSED");
        nextFilter.sessionClosed(session);
    }

    public void setExceptionCaughtLogLevel(LogLevel level) {
        this.exceptionCaughtLevel = level;
    }

    public LogLevel getExceptionCaughtLogLevel() {
        return this.exceptionCaughtLevel;
    }

    public void setMessageReceivedLogLevel(LogLevel level) {
        this.messageReceivedLevel = level;
    }

    public LogLevel getMessageReceivedLogLevel() {
        return this.messageReceivedLevel;
    }

    public void setMessageSentLogLevel(LogLevel level) {
        this.messageSentLevel = level;
    }

    public LogLevel getMessageSentLogLevel() {
        return this.messageSentLevel;
    }

    public void setSessionCreatedLogLevel(LogLevel level) {
        this.sessionCreatedLevel = level;
    }

    public LogLevel getSessionCreatedLogLevel() {
        return this.sessionCreatedLevel;
    }

    public void setSessionOpenedLogLevel(LogLevel level) {
        this.sessionOpenedLevel = level;
    }

    public LogLevel getSessionOpenedLogLevel() {
        return this.sessionOpenedLevel;
    }

    public void setSessionIdleLogLevel(LogLevel level) {
        this.sessionIdleLevel = level;
    }

    public LogLevel getSessionIdleLogLevel() {
        return this.sessionIdleLevel;
    }

    public void setSessionClosedLogLevel(LogLevel level) {
        this.sessionClosedLevel = level;
    }

    public LogLevel getSessionClosedLogLevel() {
        return this.sessionClosedLevel;
    }
}

