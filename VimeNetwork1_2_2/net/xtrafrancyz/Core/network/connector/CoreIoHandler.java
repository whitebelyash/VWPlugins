/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.connector;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import net.xtrafrancyz.Core.network.connector.CallbackData;
import net.xtrafrancyz.Core.network.connector.CoreConnector;
import net.xtrafrancyz.Core.network.packet.Packet;
import net.xtrafrancyz.Core.network.packet.Packet0KeepAlive;
import net.xtrafrancyz.Core.network.packet.Packet303ProtocolCheck;
import net.xtrafrancyz.Core.network.packet.Packet53Answer;
import net.xtrafrancyz.Core.network.packet.ResponsePacket;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

class CoreIoHandler
extends IoHandlerAdapter {
    private final CoreConnector connector;

    public CoreIoHandler(CoreConnector connector) {
        this.connector = connector;
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        this.connector.logger.info("[Core] Connected");
        this.connector.sendPacket(new Packet303ProtocolCheck(4), packet0 -> {
            Packet53Answer answer = (Packet53Answer)packet0;
            String error = null;
            switch (answer.status) {
                case "version.older": {
                    error = "[Core] Installed OLDER protocol version then the server! Update it!";
                    break;
                }
                case "version.newer": {
                    error = "[Core] Installed NEWER protocol version then the server! Update it!";
                }
            }
            if (error != null) {
                this.connector.logger.log(Level.SEVERE, error);
                Thread t = new Thread(this.connector::disconnect);
                t.setDaemon(true);
                t.setName("Core disconnecter");
                t.start();
            } else {
                for (Runnable r : this.connector.connectListeners) {
                    try {
                        r.run();
                    }
                    catch (Exception e) {
                        this.connector.logger.log(Level.WARNING, null, e);
                    }
                }
                while (!this.connector.sendQueue.isEmpty()) {
                    this.connector.sendPacket(this.connector.sendQueue.poll());
                }
            }
        }, 2000L, () -> {
            this.connector.logger.severe("[Core] Protocol check timeout");
            Thread t = new Thread(() -> {
                this.connector.disconnect();
                this.connector.connect();
            });
            t.setDaemon(true);
            t.setName("Core reconnecter");
            t.start();
        });
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        this.connector.session = null;
        for (Runnable r : this.connector.disconnectListeners) {
            try {
                r.run();
            }
            catch (Exception e) {
                this.connector.logger.log(Level.WARNING, null, e);
            }
        }
        this.connector.logger.info("[Core] Disconnected");
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            this.connector.logger.log(Level.WARNING, "[Core] Connection lost...");
        } else {
            this.connector.logger.log(Level.WARNING, "[Core] Session: " + session, cause);
        }
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        CallbackData data;
        Packet packet = (Packet)message;
        try {
            packet.process(this.connector.mainHandler);
        }
        catch (Exception ex) {
            this.connector.logger.log(Level.WARNING, "Packet: " + packet, ex);
        }
        List<Consumer<Packet>> consumers = this.connector.customHandlers.get(packet.getId());
        if (consumers != null) {
            for (Consumer<Packet> consumer : consumers) {
                try {
                    consumer.accept(packet);
                }
                catch (Exception ex) {
                    this.connector.logger.log(Level.WARNING, "Packet: " + packet, ex);
                }
            }
        }
        if (packet instanceof ResponsePacket && (data = this.connector.callbacks.remove(((ResponsePacket)packet).pResponseId)) != null) {
            data.callback.done(packet);
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        session.write(new Packet0KeepAlive());
    }
}

