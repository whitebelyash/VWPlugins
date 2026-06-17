/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.util.byteaccess;

import org.apache.mina.util.byteaccess.ByteArray;

abstract class AbstractByteArray
implements ByteArray {
    AbstractByteArray() {
    }

    @Override
    public final int length() {
        return this.last() - this.first();
    }

    @Override
    public final boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ByteArray)) {
            return false;
        }
        ByteArray otherByteArray = (ByteArray)other;
        if (this.first() != otherByteArray.first() || this.last() != otherByteArray.last() || !this.order().equals(otherByteArray.order())) {
            return false;
        }
        ByteArray.Cursor cursor = this.cursor();
        ByteArray.Cursor otherCursor = otherByteArray.cursor();
        int remaining = cursor.getRemaining();
        while (remaining > 0) {
            byte otherB;
            byte b;
            int otherI;
            int i;
            if (!(remaining >= 4 ? (i = cursor.getInt()) != (otherI = otherCursor.getInt()) : (b = cursor.get()) != (otherB = otherCursor.get()))) continue;
            return false;
        }
        return true;
    }
}

