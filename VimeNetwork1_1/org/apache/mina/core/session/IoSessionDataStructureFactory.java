package org.apache.mina.core.session;

import org.apache.mina.core.write.WriteRequestQueue;

public interface IoSessionDataStructureFactory {
   IoSessionAttributeMap getAttributeMap(IoSession var1) throws Exception;

   WriteRequestQueue getWriteRequestQueue(IoSession var1) throws Exception;
}
