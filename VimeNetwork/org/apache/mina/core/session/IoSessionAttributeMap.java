package org.apache.mina.core.session;

import java.util.Set;

public interface IoSessionAttributeMap {
   Object getAttribute(IoSession var1, Object var2, Object var3);

   Object setAttribute(IoSession var1, Object var2, Object var3);

   Object setAttributeIfAbsent(IoSession var1, Object var2, Object var3);

   Object removeAttribute(IoSession var1, Object var2);

   boolean removeAttribute(IoSession var1, Object var2, Object var3);

   boolean replaceAttribute(IoSession var1, Object var2, Object var3, Object var4);

   boolean containsAttribute(IoSession var1, Object var2);

   Set getAttributeKeys(IoSession var1);

   void dispose(IoSession var1) throws Exception;
}
