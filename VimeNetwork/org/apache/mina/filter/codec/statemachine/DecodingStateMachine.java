package org.apache.mina.filter.codec.statemachine;

import java.util.ArrayList;
import java.util.List;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DecodingStateMachine implements DecodingState {
   private final Logger log = LoggerFactory.getLogger(DecodingStateMachine.class);
   private final List childProducts = new ArrayList();
   private final ProtocolDecoderOutput childOutput = new ProtocolDecoderOutput() {
      public void flush(IoFilter.NextFilter nextFilter, IoSession session) {
      }

      public void write(Object message) {
         DecodingStateMachine.this.childProducts.add(message);
      }
   };
   private DecodingState currentState;
   private boolean initialized;

   protected abstract DecodingState init() throws Exception;

   protected abstract DecodingState finishDecode(List var1, ProtocolDecoderOutput var2) throws Exception;

   protected abstract void destroy() throws Exception;

   public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out) throws Exception {
      DecodingState state = this.getCurrentState();
      int limit = in.limit();
      int pos = in.position();

      try {
         while(true) {
            if (pos != limit) {
               DecodingState oldState = state;
               state = state.decode(in, this.childOutput);
               if (state == null) {
                  DecodingState var14 = this.finishDecode(this.childProducts, out);
                  return var14;
               }

               int newPos = in.position();
               if (newPos != pos || oldState != state) {
                  pos = newPos;
                  continue;
               }
            }

            DecodingStateMachine e = this;
            return e;
         }
      } catch (Exception e) {
         state = null;
         throw e;
      } finally {
         this.currentState = state;
         if (state == null) {
            this.cleanup();
         }

      }
   }

   public DecodingState finishDecode(ProtocolDecoderOutput out) throws Exception {
      DecodingState state = this.getCurrentState();

      DecodingState nextState;
      try {
         DecodingState oldState;
         try {
            do {
               oldState = state;
               state = state.finishDecode(this.childOutput);
            } while(state != null && oldState != state);
         } catch (Exception e) {
            state = null;
            this.log.debug((String)"Ignoring the exception caused by a closed session.", (Throwable)e);
         }
      } finally {
         this.currentState = state;
         nextState = this.finishDecode(this.childProducts, out);
         if (state == null) {
            this.cleanup();
         }

      }

      return nextState;
   }

   private void cleanup() {
      if (!this.initialized) {
         throw new IllegalStateException();
      } else {
         this.initialized = false;
         this.childProducts.clear();

         try {
            this.destroy();
         } catch (Exception e2) {
            this.log.warn((String)"Failed to destroy a decoding state machine.", (Throwable)e2);
         }

      }
   }

   private DecodingState getCurrentState() throws Exception {
      DecodingState state = this.currentState;
      if (state == null) {
         state = this.init();
         this.initialized = true;
      }

      return state;
   }
}
