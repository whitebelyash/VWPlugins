/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Commons;

import java.util.Arrays;
import java.util.Iterator;

public class F {
    private static final char[] magnitudes = new char[]{'k', 'M', 'G', 'T', 'P', 'E'};

    private F() {
    }

    public static String formatBytes(long bytes) {
        if (bytes < 1024L) {
            return bytes + " B";
        }
        int exp = (int)(Math.log(bytes) / Math.log(1024.0));
        String pre = "KMGTPE".charAt(exp - 1) + "iB";
        return F.formatFloat((float)((double)bytes / Math.pow(1024.0, exp)), 1) + " " + pre;
    }

    public static String trimNumber(long number) {
        String ret;
        if (number >= 0L) {
            ret = "";
        } else {
            if (number <= -9200000000000000000L) {
                return "-9.2E";
            }
            ret = "-";
            number = -number;
        }
        if (number < 1000L) {
            return ret + number;
        }
        int i = 0;
        while (number >= 10000L || number % 1000L < 100L) {
            if ((number /= 1000L) < 1000L) {
                return ret + number + magnitudes[i];
            }
            ++i;
        }
        return ret + number / 1000L + '.' + number % 1000L / 100L + magnitudes[i];
    }

    public static String formatNumberDelimited(long number, char delimiter) {
        if (number <= 999L) {
            return String.valueOf(number);
        }
        char[] chars = String.valueOf(number).toCharArray();
        StringBuilder sb = new StringBuilder(chars.length + (chars.length - 1) / 3);
        int begin = chars.length % 3;
        sb.append(chars, 0, begin);
        for (int i = begin; i < chars.length; i += 3) {
            if (i != 0) {
                sb.append(delimiter);
            }
            sb.append(chars, i, chars.length - i >= 3 ? 3 : chars.length - i);
        }
        return sb.toString();
    }

    public static String formatFloat(float num, int pr) {
        StringBuilder sb = new StringBuilder();
        sb.append((int)num);
        sb.append('.');
        for (int i = 0; i < pr; ++i) {
            sb.append((int)(num *= 10.0f) % 10);
        }
        return sb.toString();
    }

    public static String formatSecondsShort(int seconds) {
        if (seconds < 0) {
            return "0 \u043c\u0438\u043d.";
        }
        if (seconds <= 3540) {
            return F.plurals((int)Math.ceil((float)seconds / 60.0f), "\u043c\u0438\u043d\u0443\u0442\u0430", "\u043c\u0438\u043d\u0443\u0442\u044b", "\u043c\u0438\u043d\u0443\u0442");
        }
        if (seconds <= 82800) {
            return F.plurals((int)Math.ceil((float)seconds / 3600.0f), "\u0447\u0430\u0441", "\u0447\u0430\u0441\u0430", "\u0447\u0430\u0441\u043e\u0432");
        }
        if (seconds <= 2505600) {
            return F.plurals((int)Math.ceil((float)seconds / 86400.0f), "\u0434\u0435\u043d\u044c", "\u0434\u043d\u044f", "\u0434\u043d\u0435\u0439");
        }
        if (seconds <= 28512000) {
            return F.plurals((int)Math.ceil((float)seconds / 2592000.0f), "\u043c\u0435\u0441\u044f\u0446", "\u043c\u0435\u0441\u044f\u0446\u0430", "\u043c\u0435\u0441\u044f\u0446\u0435\u0432");
        }
        return F.plurals((int)Math.ceil((float)seconds / 3.1104E7f), "\u0433\u043e\u0434", "\u0433\u043e\u0434\u0430", "\u043b\u0435\u0442");
    }

    public static String plurals(int n, String form1, String form2, String form3) {
        int orig = n;
        if (n == 0) {
            return orig + " " + form3;
        }
        if ((n = Math.abs(n) % 100) > 10 && n < 20) {
            return orig + " " + form3;
        }
        if ((n %= 10) > 1 && n < 5) {
            return orig + " " + form2;
        }
        if (n == 1) {
            return orig + " " + form1;
        }
        return orig + " " + form3;
    }

    public static String implode(String glue, String ... values) {
        return F.implode(glue, Arrays.asList(values));
    }

    public static String implode(String glue, Iterable<String> values) {
        Iterator<String> it = values.iterator();
        if (!it.hasNext()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(it.next());
        while (it.hasNext()) {
            sb.append(glue).append(it.next());
        }
        return sb.toString();
    }
}

