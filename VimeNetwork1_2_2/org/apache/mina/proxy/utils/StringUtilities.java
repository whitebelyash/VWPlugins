/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.proxy.utils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.security.sasl.AuthenticationException;
import javax.security.sasl.SaslException;

public class StringUtilities {
    public static String getDirectiveValue(HashMap<String, String> directivesMap, String directive, boolean mandatory) throws AuthenticationException {
        String value = directivesMap.get(directive);
        if (value == null) {
            if (mandatory) {
                throw new AuthenticationException("\"" + directive + "\" mandatory directive is missing");
            }
            return "";
        }
        return value;
    }

    public static void copyDirective(HashMap<String, String> directives, StringBuilder sb, String directive) {
        String directiveValue = directives.get(directive);
        if (directiveValue != null) {
            sb.append(directive).append(" = \"").append(directiveValue).append("\", ");
        }
    }

    public static String copyDirective(HashMap<String, String> src, HashMap<String, String> dst, String directive) {
        String directiveValue = src.get(directive);
        if (directiveValue != null) {
            dst.put(directive, directiveValue);
        }
        return directiveValue;
    }

    public static HashMap<String, String> parseDirectives(byte[] buf) throws SaslException {
        HashMap<String, String> map = new HashMap<String, String>();
        boolean gettingKey = true;
        boolean gettingQuotedValue = false;
        boolean expectSeparator = false;
        ByteArrayOutputStream key = new ByteArrayOutputStream(10);
        ByteArrayOutputStream value = new ByteArrayOutputStream(10);
        int i = StringUtilities.skipLws(buf, 0);
        while (i < buf.length) {
            byte bch = buf[i];
            if (gettingKey) {
                if (bch == 44) {
                    if (key.size() != 0) {
                        throw new SaslException("Directive key contains a ',':" + key);
                    }
                    i = StringUtilities.skipLws(buf, i + 1);
                    continue;
                }
                if (bch == 61) {
                    if (key.size() == 0) {
                        throw new SaslException("Empty directive key");
                    }
                    gettingKey = false;
                    if ((i = StringUtilities.skipLws(buf, i + 1)) < buf.length) {
                        if (buf[i] != 34) continue;
                        gettingQuotedValue = true;
                        ++i;
                        continue;
                    }
                    throw new SaslException("Valueless directive found: " + key.toString());
                }
                if (StringUtilities.isLws(bch)) {
                    if ((i = StringUtilities.skipLws(buf, i + 1)) < buf.length) {
                        if (buf[i] == 61) continue;
                        throw new SaslException("'=' expected after key: " + key.toString());
                    }
                    throw new SaslException("'=' expected after key: " + key.toString());
                }
                key.write(bch);
                ++i;
                continue;
            }
            if (gettingQuotedValue) {
                if (bch == 92) {
                    if (++i < buf.length) {
                        value.write(buf[i]);
                        ++i;
                        continue;
                    }
                    throw new SaslException("Unmatched quote found for directive: " + key.toString() + " with value: " + value.toString());
                }
                if (bch == 34) {
                    ++i;
                    gettingQuotedValue = false;
                    expectSeparator = true;
                    continue;
                }
                value.write(bch);
                ++i;
                continue;
            }
            if (StringUtilities.isLws(bch) || bch == 44) {
                StringUtilities.extractDirective(map, key.toString(), value.toString());
                key.reset();
                value.reset();
                gettingKey = true;
                expectSeparator = false;
                gettingQuotedValue = false;
                i = StringUtilities.skipLws(buf, i + 1);
                continue;
            }
            if (expectSeparator) {
                throw new SaslException("Expecting comma or linear whitespace after quoted string: \"" + value.toString() + "\"");
            }
            value.write(bch);
            ++i;
        }
        if (gettingQuotedValue) {
            throw new SaslException("Unmatched quote found for directive: " + key.toString() + " with value: " + value.toString());
        }
        if (key.size() > 0) {
            StringUtilities.extractDirective(map, key.toString(), value.toString());
        }
        return map;
    }

    private static void extractDirective(HashMap<String, String> map, String key, String value) throws SaslException {
        if (map.get(key) != null) {
            throw new SaslException("Peer sent more than one " + key + " directive");
        }
        map.put(key, value);
    }

    public static boolean isLws(byte b) {
        switch (b) {
            case 9: 
            case 10: 
            case 13: 
            case 32: {
                return true;
            }
        }
        return false;
    }

    private static int skipLws(byte[] buf, int start) {
        int i;
        for (i = start; i < buf.length; ++i) {
            if (StringUtilities.isLws(buf[i])) continue;
            return i;
        }
        return i;
    }

    public static String stringTo8859_1(String str) throws UnsupportedEncodingException {
        if (str == null) {
            return "";
        }
        return new String(str.getBytes("UTF8"), "8859_1");
    }

    public static String getSingleValuedHeader(Map<String, List<String>> headers, String key) {
        List<String> values = headers.get(key);
        if (values == null) {
            return null;
        }
        if (values.size() > 1) {
            throw new IllegalArgumentException("Header with key [\"" + key + "\"] isn't single valued !");
        }
        return values.get(0);
    }

    public static void addValueToHeader(Map<String, List<String>> headers, String key, String value, boolean singleValued) {
        List<String> values = headers.get(key);
        if (values == null) {
            values = new ArrayList<String>(1);
            headers.put(key, values);
        }
        if (singleValued && values.size() == 1) {
            values.set(0, value);
        } else {
            values.add(value);
        }
    }
}

