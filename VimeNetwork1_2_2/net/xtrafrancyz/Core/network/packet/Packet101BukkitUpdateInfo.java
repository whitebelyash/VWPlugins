/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet101BukkitUpdateInfo
extends Packet {
    public String[] menuText = null;
    public int state;
    public int max;

    private Packet101BukkitUpdateInfo() {
    }

    public Packet101BukkitUpdateInfo(String[] menuText, int max, int state) {
        this.menuText = menuText;
        this.max = max;
        this.state = state;
    }

    @Override
    public void write0(Buf buf) throws Exception {
        buf.write((byte)this.menuText.length);
        for (String line : this.menuText) {
            buf.writeString(line);
        }
        buf.writeInt(this.max);
        buf.writeInt(this.state);
    }

    @Override
    public void read0(Buf buf) throws Exception {
        this.menuText = new String[buf.read()];
        for (int i = 0; i < this.menuText.length; ++i) {
            this.menuText[i] = buf.readString();
        }
        this.max = buf.readInt();
        this.state = buf.readInt();
    }

    @Override
    public void process0(PacketHandler handler) {
        handler.handle101BukkitUpdateInfo(this);
    }
}

