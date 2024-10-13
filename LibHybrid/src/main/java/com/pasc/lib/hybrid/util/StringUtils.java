/*
 * $RCSfile$
 * $Revision$
 * $Date$
 *
 * Copyright 2003-2007 Jive Software.
 *
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pasc.lib.hybrid.util;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A collection of utility methods for String objects.
 */
public class StringUtils {

    /**
     * Date format as defined in XEP-0082 - XMPP Date and Time Profiles. The time zone is set to
     * UTC.
     * <p>
     * Date formats are not synchronized. Since multiple threads access the format concurrently, it
     * must be synchronized externally or you can use the convenience methods
     * {@link #parseXEP0082Date(String)} and {@link #formatXEP0082Date(Date)}.
     */
    public static final DateFormat XEP_0082_UTC_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.CHINESE);

    static {
        XEP_0082_UTC_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private static final char[] QUOTE_ENCODE = "&quot;".toCharArray();
    private static final char[] APOS_ENCODE = "&apos;".toCharArray();
    private static final char[] AMP_ENCODE = "&amp;".toCharArray();
    private static final char[] LT_ENCODE = "&lt;".toCharArray();
    private static final char[] GT_ENCODE = "&gt;".toCharArray();

    /**
     * Parses the given date string in the XEP-0082 - XMPP Date and Time Profiles format.
     *
     * @param dateString the date string to parse
     * @return the parsed Date
     * @throws ParseException if the specified string cannot be parsed
     */
    public static Date parseXEP0082Date(String dateString) throws ParseException {
        synchronized (XEP_0082_UTC_FORMAT) {
            return XEP_0082_UTC_FORMAT.parse(dateString);
        }
    }

    /**
     * Formats a Date into a XEP-0082 - XMPP Date and Time Profiles string.
     *
     * @param date the time value to be formatted into a time string
     * @return the formatted time string in XEP-0082 format
     */
    public static String formatXEP0082Date(Date date) {
        synchronized (XEP_0082_UTC_FORMAT) {
            return XEP_0082_UTC_FORMAT.format(date);
        }
    }

    /**
     * Returns the name portion of a XMPP address. For example, for the
     * address "matt@jivesoftware.com/Smack", "matt" would be returned. If no
     * username is present in the address, the empty string will be returned.
     *
     * @param XMPPAddress the XMPP address.
     * @return the name portion of the XMPP address.
     */
    public static String parseName(String XMPPAddress) {
        if (XMPPAddress == null) {
            return null;
        }
        int atIndex = XMPPAddress.lastIndexOf("@");
        if (atIndex <= 0) {
            return "";
        } else {
            return XMPPAddress.substring(0, atIndex);
        }
    }

    /**
     * Returns the server portion of a XMPP address. For example, for the
     * address "matt@jivesoftware.com/Smack", "jivesoftware.com" would be returned.
     * If no server is present in the address, the empty string will be returned.
     *
     * @param XMPPAddress the XMPP address.
     * @return the server portion of the XMPP address.
     */
    public static String parseServer(String XMPPAddress) {
        if (XMPPAddress == null) {
            return null;
        }
        int atIndex = XMPPAddress.lastIndexOf("@");
        // If the String ends with '@', return the empty string.
        if (atIndex + 1 > XMPPAddress.length()) {
            return "";
        }
        int slashIndex = XMPPAddress.indexOf("/");
        if (slashIndex > 0 && slashIndex > atIndex) {
            return XMPPAddress.substring(atIndex + 1, slashIndex);
        } else {
            return XMPPAddress.substring(atIndex + 1);
        }
    }

    /**
     * Returns the resource portion of a XMPP address. For example, for the
     * address "matt@jivesoftware.com/Smack", "Smack" would be returned. If no
     * resource is present in the address, the empty string will be returned.
     *
     * @param XMPPAddress the XMPP address.
     * @return the resource portion of the XMPP address.
     */
    public static String parseResource(String XMPPAddress) {
        if (XMPPAddress == null) {
            return null;
        }
        int slashIndex = XMPPAddress.indexOf("/");
        if (slashIndex + 1 > XMPPAddress.length() || slashIndex < 0) {
            return "";
        } else {
            return XMPPAddress.substring(slashIndex + 1);
        }
    }

    /**
     * Returns the XMPP address with any resource information removed. For example,
     * for the address "matt@jivesoftware.com/Smack", "matt@jivesoftware.com" would
     * be returned.
     *
     * @param XMPPAddress the XMPP address.
     * @return the bare XMPP address without resource information.
     */
    public static String parseBareAddress(String XMPPAddress) {
        if (XMPPAddress == null) {
            return null;
        }
        int slashIndex = XMPPAddress.indexOf("/");
        if (slashIndex < 0) {
            return XMPPAddress;
        } else if (slashIndex == 0) {
            return "";
        } else {
            return XMPPAddress.substring(0, slashIndex);
        }
    }

    /**
     * Escapes the node portion of a JID according to "JID Escaping" (JEP-0106).
     * Escaping replaces characters prohibited by node-prep with escape sequences,
     * as follows:<p>
     * <p>
     * <table border="1">
     * <tr><td><b>Unescaped Character</b></td><td><b>Encoded Sequence</b></td></tr>
     * <tr><td>&lt;space&gt;</td><td>\20</td></tr>
     * <tr><td>"</td><td>\22</td></tr>
     * <tr><td>&</td><td>\26</td></tr>
     * <tr><td>'</td><td>\27</td></tr>
     * <tr><td>/</td><td>\2f</td></tr>
     * <tr><td>:</td><td>\3a</td></tr>
     * <tr><td>&lt;</td><td>\3c</td></tr>
     * <tr><td>&gt;</td><td>\3e</td></tr>
     * <tr><td>@</td><td>\40</td></tr>
     * <tr><td>\</td><td>\5c</td></tr>
     * </table><p>
     * <p>
     * This process is useful when the node comes from an external source that doesn't
     * conform to nodeprep. For example, a username in LDAP may be "Joe Smith". Because
     * the &lt;space&gt; character isn't a valid part of a node, the username should
     * be escaped to "Joe\20Smith" before being made into a JID (e.g. "joe\20smith@example.com"
     * after case-folding, etc. has been applied).<p>
     * <p>
     * All node escaping and un-escaping must be performed manually at the appropriate
     * time; the JID class will not escape or un-escape automatically.
     *
     * @param node the node.
     * @return the escaped version of the node.
     */
    public static String escapeNode(String node) {
        if (node == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder(node.length() + 8);
        for (int i = 0, n = node.length(); i < n; i++) {
            char c = node.charAt(i);
            switch (c) {
                case '"':
                    buf.append("\\22");
                    break;
                case '&':
                    buf.append("\\26");
                    break;
                case '\'':
                    buf.append("\\27");
                    break;
                case '/':
                    buf.append("\\2f");
                    break;
                case ':':
                    buf.append("\\3a");
                    break;
                case '<':
                    buf.append("\\3c");
                    break;
                case '>':
                    buf.append("\\3e");
                    break;
                case '@':
                    buf.append("\\40");
                    break;
                case '\\':
                    buf.append("\\5c");
                    break;
                default: {
                    if (Character.isWhitespace(c)) {
                        buf.append("\\20");
                    } else {
                        buf.append(c);
                    }
                }
            }
        }
        return buf.toString();
    }

    /**
     * Un-escapes the node portion of a JID according to "JID Escaping" (JEP-0106).<p>
     * Escaping replaces characters prohibited by node-prep with escape sequences,
     * as follows:<p>
     * <p>
     * <table border="1">
     * <tr><td><b>Unescaped Character</b></td><td><b>Encoded Sequence</b></td></tr>
     * <tr><td>&lt;space&gt;</td><td>\20</td></tr>
     * <tr><td>"</td><td>\22</td></tr>
     * <tr><td>&</td><td>\26</td></tr>
     * <tr><td>'</td><td>\27</td></tr>
     * <tr><td>/</td><td>\2f</td></tr>
     * <tr><td>:</td><td>\3a</td></tr>
     * <tr><td>&lt;</td><td>\3c</td></tr>
     * <tr><td>&gt;</td><td>\3e</td></tr>
     * <tr><td>@</td><td>\40</td></tr>
     * <tr><td>\</td><td>\5c</td></tr>
     * </table><p>
     * <p>
     * This process is useful when the node comes from an external source that doesn't
     * conform to nodeprep. For example, a username in LDAP may be "Joe Smith". Because
     * the &lt;space&gt; character isn't a valid part of a node, the username should
     * be escaped to "Joe\20Smith" before being made into a JID (e.g. "joe\20smith@example.com"
     * after case-folding, etc. has been applied).<p>
     * <p>
     * All node escaping and un-escaping must be performed manually at the appropriate
     * time; the JID class will not escape or un-escape automatically.
     *
     * @param node the escaped version of the node.
     * @return the un-escaped version of the node.
     */
    public static String unescapeNode(String node) {
        if (node == null) {
            return null;
        }
        char[] nodeChars = node.toCharArray();
        StringBuilder buf = new StringBuilder(nodeChars.length);
        for (int i = 0, n = nodeChars.length; i < n; i++) {
            compare:
            {
                char c = node.charAt(i);
                if (c == '\\' && i + 2 < n) {
                    char c2 = nodeChars[i + 1];
                    char c3 = nodeChars[i + 2];
                    if (c2 == '2') {
                        switch (c3) {
                            case '0':
                                buf.append(' ');
                                i += 2;
                                break compare;
                            case '2':
                                buf.append('"');
                                i += 2;
                                break compare;
                            case '6':
                                buf.append('&');
                                i += 2;
                                break compare;
                            case '7':
                                buf.append('\'');
                                i += 2;
                                break compare;
                            case 'f':
                                buf.append('/');
                                i += 2;
                                break compare;
                        }
                    } else if (c2 == '3') {
                        switch (c3) {
                            case 'a':
                                buf.append(':');
                                i += 2;
                                break compare;
                            case 'c':
                                buf.append('<');
                                i += 2;
                                break compare;
                            case 'e':
                                buf.append('>');
                                i += 2;
                                break compare;
                        }
                    } else if (c2 == '4') {
                        if (c3 == '0') {
                            buf.append("@");
                            i += 2;
                            break compare;
                        }
                    } else if (c2 == '5') {
                        if (c3 == 'c') {
                            buf.append("\\");
                            i += 2;
                            break compare;
                        }
                    }
                }
                buf.append(c);
            }
        }
        return buf.toString();
    }

    /**
     * Escapes all necessary characters in the String so that it can be used
     * in an XML doc.
     *
     * @param string the string to escape.
     * @return the string with appropriate characters escaped.
     */
    public static String escapeForXML(String string) {
        if (string == null) {
            return null;
        }
        char ch;
        int i = 0;
        int last = 0;
        char[] input = string.toCharArray();
        int len = input.length;
        StringBuilder out = new StringBuilder((int) (len * 1.3));
        for (; i < len; i++) {
            ch = input[i];
            if (ch > '>') {
            } else if (ch == '<') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(LT_ENCODE);
            } else if (ch == '>') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(GT_ENCODE);
            } else if (ch == '&') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                // Do nothing if the string is of the form &#235; (unicode value)
                if (!(len > i + 5
                        && input[i + 1] == '#'
                        && Character.isDigit(input[i + 2])
                        && Character.isDigit(input[i + 3])
                        && Character.isDigit(input[i + 4])
                        && input[i + 5] == ';')) {
                    last = i + 1;
                    out.append(AMP_ENCODE);
                }
            } else if (ch == '"') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(QUOTE_ENCODE);
            } else if (ch == '\'') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(APOS_ENCODE);
            }
        }
        if (last == 0) {
            return string;
        }
        if (i > last) {
            out.append(input, last, i - last);
        }
        return out.toString();
    }

    /**
     * Used by the hash method.
     */
    private static MessageDigest digest = null;

    /**
     * Hashes a String using the SHA-1 algorithm and returns the result as a
     * String of hexadecimal numbers. This method is synchronized to avoid
     * excessive MessageDigest object creation. If calling this method becomes
     * a bottleneck in your code, you may wish to maintain a pool of
     * MessageDigest objects instead of using this method.
     * <p>
     * A hash is a one-way function -- that is, given an
     * input, an output is easily computed. However, given the output, the
     * input is almost impossible to compute. This is useful for passwords
     * since we can store the hash and a hacker will then have a very hard time
     * determining the original password.
     *
     * @param data the String to compute the hash of.
     * @return a hashed version of the passed-in String
     */
    public synchronized static String hash(String data) {
        if (digest == null) {
            try {
                digest = MessageDigest.getInstance("SHA-1");
            } catch (NoSuchAlgorithmException nsae) {
                System.err.println("Failed to load the SHA-1 MessageDigest. " +
                        "Jive will be unable to function normally.");
            }
        }
        // Now, compute hash.
        try {
            digest.update(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.err.println(e);
        }
        return encodeHex(digest.digest());
    }

    /**
     * Encodes an array of bytes as String representation of hexadecimal.
     *
     * @param bytes an array of bytes to convert to a hex string.
     * @return generated hex string.
     */
    public static String encodeHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder(bytes.length * 2);

        for (byte aByte : bytes) {
            if (((int) aByte & 0xff) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toString((int) aByte & 0xff, 16));
        }

        return hex.toString();
    }


    /**
     * Array of numbers and letters of mixed case. Numbers appear in the list
     * twice so that there is a more equal chance that a number will be picked.
     * We can use the array to get a random number or letter by picking a random
     * array index.
     */
    private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" +
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();

    private static char[] id_numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();


    /**
     * 随机字符串
     *
     * @param t_id t_id
     * @return String
     */
    public static String randomString(long t_id) {

        t_id *= 314;
        StringBuffer strNumber = new StringBuffer();
        // Create a char buffer to put random letters and numbers in.
        do {
            long index = t_id % 62;
            strNumber.append(id_numbersAndLetters[(int) index]);
            if (t_id < 62)
                break;
            t_id /= 62;

        } while (true);
        return strNumber.toString();
    }

    private StringUtils() {
        // Not instantiable.
    }

    /**
     * 检查字符是包含非法字符
     *
     * @param str str
     * @return boolean
     */
    public static boolean checkIllegalChar(String str) {
        if (str == null) {
            return true;
        }
        boolean ret = false;
        char ch;
        char[] cs = str.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            ch = cs[i];
            if (ch < 0x20 && ch != 0x9 && ch != 0xA && ch != 0xD && ch != 0x0) {
                ret = true;
                return ret;
            }
        }
        return ret;
    }

    /**
     * 检查是否是数值字符串
     *
     * @param s s
     * @return boolean
     */
    public static boolean isNum(String s) {
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher isNum = pattern.matcher(s);
        return isNum.matches();
    }

    /**
     * 判断多个字符串是否为空，只要有一个为空即返回false
     *
     * @param strings strings
     * @return boolean
     */
    public static boolean StringsHasEmpty(String... strings) {
        boolean flag = false;
        for (String str : strings) {
            if (TextUtils.isEmpty(str)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 字符串过滤
     *
     * @param str str
     * @return String
     */
    public static String stringFilter(String str) {
        String result;
        try {
            str = str.replaceAll("\\\\", "");
            String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";  //+号表示空格
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            result = m.replaceAll("").trim();
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
        return result;
    }

    /**
     * 判断字符串是否为空
     *
     * @param str str
     * @return boolean
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim()) || str.length() == 0 || str.equalsIgnoreCase("null");
    }

    /**
     * 判断字符串是否为非空
     *
     * @param str str
     * @return boolean
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * @param str str
     * @return String
     */
    public static String trim(String str) {
        return str == null ? "" : str.trim();
    }

    /**
     * 截取字符串
     *
     * @param search       待搜索的字符串
     * @param start        起始字符串 例如：<title>
     * @param end          结束字符串 例如：</title>
     * @param defaultValue defaultValue
     * @return String
     */
    public static String substring(String search, String start, String end,
                                   String defaultValue) {
        int start_len = start.length();
        int start_pos = StringUtils.isEmpty(start) ? 0 : search.indexOf(start);
        if (start_pos > -1) {
            int end_pos = StringUtils.isEmpty(end) ? -1 : search.indexOf(end,
                    start_pos + start_len);
            if (end_pos > -1)
                return search.substring(start_pos + start.length(), end_pos);
            else
                return search.substring(start_pos + start.length());
        }
        return defaultValue;
    }

    /**
     * 截取字符串
     *
     * @param search 待搜索的字符串
     * @param start  起始字符串 例如：<title>
     * @param end    结束字符串 例如：</title>
     * @return String
     */
    public static String substring(String search, String start, String end) {
        return substring(search, start, end, "");
    }

    /**
     * 得到key
     *
     * @param keys keys
     * @return String
     */
    public static String getKey(String... keys) {
        if (keys == null) return "";
        StringBuffer sbKey = new StringBuffer();

        for (int i = 0; i < keys.length; i++) {
            sbKey.append(keys[i]);
            if (i == keys.length - 1) {
                break;
            }
            sbKey.append(File.separator);
        }
        return sbKey.toString();
    }

    /**
     * inputStream 转 String
     *
     * @param is is
     * @return String
     */
    public static String inputStream2String(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();
        String line = null;
        try {

            while ((line = reader.readLine()) != null) {

                sb.append(line);

            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * inputStream 转 ByteArrary
     *
     * @param is is
     * @return byte
     */
    public static byte[] inputStream2ByteArrary(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int length = 0;
        while ((length = is.read(bytes)) > 0) {
            baos.write(bytes, 0, length);
        }
        return baos.toByteArray();
    }

    /**
     * 判断两个对象是否相同，兼容空异常
     *
     * @param ob1 1
     * @param ob2 2
     * @return boolean
     */
    public static boolean equals(Object ob1, Object ob2) {
        if (ob1 == null) {
            return false;
        } else {
            if (ob1.equals(ob2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 循环移位
     *
     * @param value 需要移位的值
     * @param n     移动方向和大小，负数为左移，正数为右移
     * @return
     */
    public static int cycleShift(int value, int n) {
        int i;
        if (n > 0) {//右移
            for (i = 0; i < n; i++) {
                value = value >>> 1 | (value & 1) << 31;
            }
        } else {
            n = -n;
            for (i = 0; i < n; i++) {
                value = value << 1 | value >>> 31 & 1;
            }
        }
        return value;
    }

    /**
     * 反转 hash
     *
     * @param key   key
     * @param prime prime
     * @return int
     */
    public static int rotatingHash(String key, int prime) {
        int hash, i;
        for (hash = key.length(), i = 0; i < key.length(); ++i) {
            hash = (hash << 4) ^ (hash >> 28) ^ key.charAt(i);
        }
        return (hash % prime);
    }

    /**
     * 判断字符串是否为 null 或长度为 0
     *
     * @param s 待校验字符串
     * @return {@code true}: 空<br> {@code false}: 不为空
     */
    public static boolean isEmpty(final CharSequence s) {
        return s == null || s.length() == 0;
    }

    /**
     * 判断字符串是否为 null 或全为空格
     *
     * @param s 待校验字符串
     * @return {@code true}: null 或全空格<br> {@code false}: 不为 null 且不全空格
     */
    public static boolean isTrimEmpty(final String s) {
        return (s == null || s.trim().length() == 0);
    }

    /**
     * 判断字符串是否为 null 或全为空白字符
     *
     * @param s 待校验字符串
     * @return {@code true}: null 或全空白字符<br> {@code false}: 不为 null 且不全空白字符
     */
    public static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断两字符串是否相等
     *
     * @param a 待校验字符串 a
     * @param b 待校验字符串 b
     * @return {@code true}: 相等<br>{@code false}: 不相等
     */
    public static boolean equals(final CharSequence a, final CharSequence b) {
        if (a == b) return true;
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 判断两字符串忽略大小写是否相等
     *
     * @param a 待校验字符串 a
     * @param b 待校验字符串 b
     * @return {@code true}: 相等<br>{@code false}: 不相等
     */
    public static boolean equalsIgnoreCase(final String a, final String b) {
        return a == null ? b == null : a.equalsIgnoreCase(b);
    }

    /**
     * null 转为长度为 0 的字符串
     *
     * @param s 待转字符串
     * @return s 为 null 转为长度为 0 字符串，否则不改变
     */
    public static String null2Length0(final String s) {
        return s == null ? "" : s;
    }

    /**
     * 返回字符串长度
     *
     * @param s 字符串
     * @return null 返回 0，其他返回自身长度
     */
    public static int length(final CharSequence s) {
        return s == null ? 0 : s.length();
    }

    /**
     * 首字母大写
     *
     * @param s 待转字符串
     * @return 首字母大写字符串
     */
    public static String upperFirstLetter(final String s) {
        if (isEmpty(s) || !Character.isLowerCase(s.charAt(0))) return s;
        return String.valueOf((char) (s.charAt(0) - 32)) + s.substring(1);
    }

    /**
     * 首字母小写
     *
     * @param s 待转字符串
     * @return 首字母小写字符串
     */
    public static String lowerFirstLetter(final String s) {
        if (isEmpty(s) || !Character.isUpperCase(s.charAt(0))) return s;
        return String.valueOf((char) (s.charAt(0) + 32)) + s.substring(1);
    }

    /**
     * 反转字符串
     *
     * @param s 待反转字符串
     * @return 反转字符串
     */
    public static String reverse(final String s) {
        int len = length(s);
        if (len <= 1) return s;
        int mid = len >> 1;
        char[] chars = s.toCharArray();
        char c;
        for (int i = 0; i < mid; ++i) {
            c = chars[i];
            chars[i] = chars[len - i - 1];
            chars[len - i - 1] = c;
        }
        return new String(chars);
    }

    /**
     * 转化为半角字符
     *
     * @param s 待转字符串
     * @return 半角字符串
     */
    public static String toDBC(final String s) {
        if (isEmpty(s)) return s;
        char[] chars = s.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == 12288) {
                chars[i] = ' ';
            } else if (65281 <= chars[i] && chars[i] <= 65374) {
                chars[i] = (char) (chars[i] - 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    /**
     * 转化为全角字符
     *
     * @param s 待转字符串
     * @return 全角字符串
     */
    public static String toSBC(final String s) {
        if (isEmpty(s)) return s;
        char[] chars = s.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == ' ') {
                chars[i] = (char) 12288;
            } else if (33 <= chars[i] && chars[i] <= 126) {
                chars[i] = (char) (chars[i] + 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }


    /**
     * 字体匹配
     *
     * @param query query
     * @param data  data
     * @return SpannableStringBuilder
     */
    public static SpannableStringBuilder getSpannableString(String query, String data) {
        Pattern mF;
        Matcher matcher = null;
        SpannableStringBuilder sb = null;
        mF = Pattern.compile(query, Pattern.CASE_INSENSITIVE | Pattern.LITERAL);
        if (!TextUtils.isEmpty(data)) {
            matcher = mF.matcher(data);
            while (matcher.find()) {
                return sb = createFindText(matcher, sb, data);
            }
        }
        return sb;
    }

    /**
     * 字体匹配
     *
     * @param matcher matcher
     * @param sb      sb
     * @param text    text
     * @return SpannableStringBuilder
     */
    private static SpannableStringBuilder createFindText(Matcher matcher, SpannableStringBuilder sb, String text) {
        if (sb == null) {
            sb = new SpannableStringBuilder(text);
        }
        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#ff6b37"));
        sb.setSpan(span, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return sb;
    }

    /**
     * 字体匹配
     *
     * @param query query
     * @param data  data
     * @param color color
     * @return SpannableStringBuilder
     */
    public static SpannableStringBuilder getSpannableString(String query, String data, int color) {
        Pattern mF;
        Matcher matcher = null;
        SpannableStringBuilder sb = null;
        mF = Pattern.compile(query, Pattern.CASE_INSENSITIVE | Pattern.LITERAL);
        if (!TextUtils.isEmpty(data)) {
            matcher = mF.matcher(data);
            while (matcher.find()) {
                return sb = createFindText(matcher, sb, data, color);
            }
        }
        return sb;
    }

    /**
     * 字体匹配
     *
     * @param matcher matcher
     * @param sb      sb
     * @param text    text
     * @param color   color
     * @return SpannableStringBuilder
     */
    private static SpannableStringBuilder createFindText(Matcher matcher, SpannableStringBuilder sb, String text, int color) {
        if (sb == null) {
            sb = new SpannableStringBuilder(text);
        }
        ForegroundColorSpan span = new ForegroundColorSpan(color);
        sb.setSpan(span, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return sb;
    }


    //######### 来自于原ComStringUtils 后续需要对比是否可以合并或修改############

    /**
     * 获取正常文本消息
     * <p/>
     * 过滤非正常字符
     *
     * @param content
     * @return String
     * @author 陈大龙
     * @time 2015-7-24 下午6:48:41
     */
    public static String getNormContentString(String content) {

        // 过滤空格
        content = content.trim().replaceAll("&nbsp;", "");

        Pattern pattern = Pattern.compile("[^0-9a-zA-Z\u4e00-\u9fa5.，,。？“”]+");
        Matcher matcher;
        StringBuilder stringBuilder = new StringBuilder();
        int length = content.length();
        for (int i = 0; i < length; i++) {
            char c = content.charAt(i);
            matcher = pattern.matcher(String.valueOf(c));
            if (matcher.find()) {
                continue;
            }
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }


    public static boolean hasUnKnow(String content, int max) {
        // 过滤空格
        content = content.trim().replaceAll("&nbsp;", "");

        Pattern pattern = Pattern.compile("[^0-9a-zA-Z\u4e00-\u9fa5.，,。？“”]+");
        Matcher matcher;

        int n = max < content.length() ? max : content.length();
        for (int i = 0; i < n; i++) {
            char c = content.charAt(i);
            matcher = pattern.matcher(String.valueOf(c));
            if (matcher.find()) {
                return true;
            }

        }

        return false;
    }

    /**
     * 链接变图文前50个字符 没有一个符号，认为当前标题不可取
     *
     * @param content
     * @return boolean
     * @author 陈大龙
     * @time 2015-7-24 下午7:00:54
     */
    public static boolean isLegitimateString(String content) {
        Pattern pattern = Pattern.compile("[.,:;'\"。，：“?？]");
        Matcher matcher = pattern.matcher(content);
        return !matcher.find() && content.length() <= 50;
    }

}
