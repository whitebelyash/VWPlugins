/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.filterchain;

import java.util.List;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

public interface IoFilterChain {
    public IoSession getSession();

    public Entry getEntry(String var1);

    public Entry getEntry(IoFilter var1);

    public Entry getEntry(Class<? extends IoFilter> var1);

    public IoFilter get(String var1);

    public IoFilter get(Class<? extends IoFilter> var1);

    public IoFilter.NextFilter getNextFilter(String var1);

    public IoFilter.NextFilter getNextFilter(IoFilter var1);

    public IoFilter.NextFilter getNextFilter(Class<? extends IoFilter> var1);

    public List<Entry> getAll();

    public List<Entry> getAllReversed();

    public boolean contains(String var1);

    public boolean contains(IoFilter var1);

    public boolean contains(Class<? extends IoFilter> var1);

    public void addFirst(String var1, IoFilter var2);

    public void addLast(String var1, IoFilter var2);

    public void addBefore(String var1, String var2, IoFilter var3);

    public void addAfter(String var1, String var2, IoFilter var3);

    public IoFilter replace(String var1, IoFilter var2);

    public void replace(IoFilter var1, IoFilter var2);

    public IoFilter replace(Class<? extends IoFilter> var1, IoFilter var2);

    public IoFilter remove(String var1);

    public void remove(IoFilter var1);

    public IoFilter remove(Class<? extends IoFilter> var1);

    public void clear() throws Exception;

    public void fireSessionCreated();

    public void fireSessionOpened();

    public void fireSessionClosed();

    public void fireSessionIdle(IdleStatus var1);

    public void fireMessageReceived(Object var1);

    public void fireMessageSent(WriteRequest var1);

    public void fireExceptionCaught(Throwable var1);

    public void fireInputClosed();

    public void fireFilterWrite(WriteRequest var1);

    public void fireFilterClose();

    public static interface Entry {
        public String getName();

        public IoFilter getFilter();

        public IoFilter.NextFilter getNextFilter();

        public void addBefore(String var1, IoFilter var2);

        public void addAfter(String var1, IoFilter var2);

        public void replace(IoFilter var1);

        public void remove();
    }
}

