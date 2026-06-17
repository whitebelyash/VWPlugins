/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec.textline;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

public class LineDelimiter {
    public static final LineDelimiter DEFAULT;
    public static final LineDelimiter AUTO;
    public static final LineDelimiter CRLF;
    public static final LineDelimiter UNIX;
    public static final LineDelimiter WINDOWS;
    public static final LineDelimiter MAC;
    public static final LineDelimiter NUL;
    private final String value;

    public LineDelimiter(String value) {
        if (value == null) {
            throw new IllegalArgumentException("delimiter");
        }
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LineDelimiter)) {
            return false;
        }
        LineDelimiter that = (LineDelimiter)o;
        return this.value.equals(that.value);
    }

    public String toString() {
        if (this.value.length() == 0) {
            return "delimiter: auto";
        }
        StringBuilder buf = new StringBuilder();
        buf.append("delimiter:");
        for (int i = 0; i < this.value.length(); ++i) {
            buf.append(" 0x");
            buf.append(Integer.toHexString(this.value.charAt(i)));
        }
        return buf.toString();
    }

    static {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(bout, true);
        out.println();
        DEFAULT = new LineDelimiter(new String(bout.toByteArray()));
        AUTO = new LineDelimiter("");
        CRLF = new LineDelimiter("\r\n");
        UNIX = new LineDelimiter("\n");
        WINDOWS = CRLF;
        MAC = new LineDelimiter("\r");
        NUL = new LineDelimiter("\u0000");
    }
}

