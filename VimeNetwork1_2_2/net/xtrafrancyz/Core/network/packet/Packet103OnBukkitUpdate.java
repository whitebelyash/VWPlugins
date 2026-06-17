/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet103OnBukkitUpdate
extends Packet {
    public String id;
    public String[] menuText;

    private Packet103OnBukkitUpdate() {
    }

    public Packet103OnBukkitUpdate(String id, String[] menuText) {
        this.id = id;
        this.menuText = menuText;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeString(this.id);
        buf.write((byte)this.menuText.length);
        for (String line : this.menuText) {
            buf.writeString(line);
        }
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.id = buf.readString();
        this.menuText = new String[buf.read()];
        for (int k = 0; k < this.menuText.length; ++k) {
            this.menuText[k] = buf.readString();
        }
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle103OnBukkitUpdate(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{server=" + this.id + "}";
    }
}

