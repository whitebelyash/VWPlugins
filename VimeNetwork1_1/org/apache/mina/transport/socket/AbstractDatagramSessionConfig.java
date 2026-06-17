package org.apache.mina.transport.socket;

import org.apache.mina.core.session.AbstractIoSessionConfig;
import org.apache.mina.core.session.IoSessionConfig;

public abstract class AbstractDatagramSessionConfig extends AbstractIoSessionConfig implements DatagramSessionConfig {
   private boolean closeOnPortUnreachable = true;

   public void setAll(IoSessionConfig config) {
      super.setAll(config);
      if (config instanceof DatagramSessionConfig) {
         if (config instanceof AbstractDatagramSessionConfig) {
            AbstractDatagramSessionConfig cfg = (AbstractDatagramSessionConfig)config;
            if (cfg.isBroadcastChanged()) {
               this.setBroadcast(cfg.isBroadcast());
            }

            if (cfg.isReceiveBufferSizeChanged()) {
               this.setReceiveBufferSize(cfg.getReceiveBufferSize());
            }

            if (cfg.isReuseAddressChanged()) {
               this.setReuseAddress(cfg.isReuseAddress());
            }

            if (cfg.isSendBufferSizeChanged()) {
               this.setSendBufferSize(cfg.getSendBufferSize());
            }

            if (cfg.isTrafficClassChanged() && this.getTrafficClass() != cfg.getTrafficClass()) {
               this.setTrafficClass(cfg.getTrafficClass());
            }
         } else {
            DatagramSessionConfig cfg = (DatagramSessionConfig)config;
            this.setBroadcast(cfg.isBroadcast());
            this.setReceiveBufferSize(cfg.getReceiveBufferSize());
            this.setReuseAddress(cfg.isReuseAddress());
            this.setSendBufferSize(cfg.getSendBufferSize());
            if (this.getTrafficClass() != cfg.getTrafficClass()) {
               this.setTrafficClass(cfg.getTrafficClass());
            }
         }

      }
   }

   protected boolean isBroadcastChanged() {
      return true;
   }

   protected boolean isReceiveBufferSizeChanged() {
      return true;
   }

   protected boolean isReuseAddressChanged() {
      return true;
   }

   protected boolean isSendBufferSizeChanged() {
      return true;
   }

   protected boolean isTrafficClassChanged() {
      return true;
   }

   public boolean isCloseOnPortUnreachable() {
      return this.closeOnPortUnreachable;
   }

   public void setCloseOnPortUnreachable(boolean closeOnPortUnreachable) {
      this.closeOnPortUnreachable = closeOnPortUnreachable;
   }
}
