/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import java.util.HashMap;
import java.util.Map;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet7MiniDot
extends Packet {
    public int userid;
    public Map<String, Integer> items;
    public int item;
    public Action action;

    private Packet7MiniDot() {
    }

    public Packet7MiniDot(int userid, int item, Action action) {
        if (action != Action.UNLOCK) {
            throw new IllegalArgumentException();
        }
        this.userid = userid;
        this.item = item;
        this.action = action;
    }

    public Packet7MiniDot(int userid, Map<String, Integer> items, Action action) {
        if (action != Action.DRESS) {
            throw new IllegalArgumentException();
        }
        this.userid = userid;
        this.items = items;
        this.action = action;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeInt(this.userid);
        buf.write((byte)this.action.ordinal());
        if (this.action == Action.DRESS) {
            buf.writeInt(this.items.size());
            for (Map.Entry<String, Integer> item : this.items.entrySet()) {
                buf.writeString(item.getKey());
                buf.writeInt(item.getValue());
            }
        } else {
            buf.writeInt(this.item);
        }
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.userid = buf.readInt();
        this.action = Action.values()[buf.read()];
        if (this.action == Action.DRESS) {
            this.items = new HashMap<String, Integer>();
            int size = buf.readInt();
            for (int i = 0; i < size; ++i) {
                this.items.put(buf.readString(), buf.readInt());
            }
        } else {
            this.item = buf.readInt();
        }
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle7MiniDot(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{userid=" + this.userid + ", action=" + (Object)((Object)this.action) + "}";
    }

    public static enum Action {
        DRESS,
        UNLOCK;

    }
}

