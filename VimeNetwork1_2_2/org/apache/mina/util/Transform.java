/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.util;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

public class Transform {
    private static final String CDATA_START = "<![CDATA[";
    private static final String CDATA_END = "]]>";
    private static final String CDATA_PSEUDO_END = "]]&gt;";
    private static final String CDATA_EMBEDED_END = "]]>]]&gt;<![CDATA[";
    private static final int CDATA_END_LEN = "]]>".length();

    public static String escapeTags(String input) {
        if (input == null || input.length() == 0 || input.indexOf(34) == -1 && input.indexOf(38) == -1 && input.indexOf(60) == -1 && input.indexOf(62) == -1) {
            return input;
        }
        StringBuilder buf = new StringBuilder(input.length() + 6);
        int len = input.length();
        for (int i = 0; i < len; ++i) {
            char ch = input.charAt(i);
            if (ch > '>') {
                buf.append(ch);
                continue;
            }
            if (ch == '<') {
                buf.append("&lt;");
                continue;
            }
            if (ch == '>') {
                buf.append("&gt;");
                continue;
            }
            if (ch == '&') {
                buf.append("&amp;");
                continue;
            }
            if (ch == '\"') {
                buf.append("&quot;");
                continue;
            }
            buf.append(ch);
        }
        return buf.toString();
    }

    public static void appendEscapingCDATA(StringBuffer buf, String str) {
        if (str != null) {
            int end = str.indexOf(CDATA_END);
            if (end < 0) {
                buf.append(str);
            } else {
                int start = 0;
                while (end > -1) {
                    buf.append(str.substring(start, end));
                    buf.append(CDATA_EMBEDED_END);
                    start = end + CDATA_END_LEN;
                    if (start < str.length()) {
                        end = str.indexOf(CDATA_END, start);
                        continue;
                    }
                    return;
                }
                buf.append(str.substring(start));
            }
        }
    }

    public static String[] getThrowableStrRep(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        pw.flush();
        LineNumberReader reader = new LineNumberReader(new StringReader(sw.toString()));
        ArrayList<String> lines = new ArrayList<String>();
        try {
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        }
        catch (IOException ex) {
            lines.add(ex.toString());
        }
        String[] rep = new String[lines.size()];
        lines.toArray(rep);
        return rep;
    }
}

