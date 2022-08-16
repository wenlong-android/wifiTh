package com.ebig.socket.utils;

import java.io.UnsupportedEncodingException;
import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OkChatHexUtils {
    /**
     * 单个字节最高位取1
     *
     * @param order
     * @return
     */
    public static String makeReciveHex(String order) {
        int i = OkChatHexUtils.hex2int(order);
        return OkChatHexUtils.int2Hex(i | 0x80);
    }

    /**
     * Convert byte[] to hex string. 把字节数组转化为字符串
     * 这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     *
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv + " ");
        }
        return stringBuilder.toString();
    }

    public static String bytesToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    /**
     * Convert hex string to byte[]   把为字符串转化为字节数组
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


    /**
     * byte[]转int
     *
     * @param bytes 需要转换成int的数组
     * @return int值
     */
    public static int byteArr2Int(byte[] bytes) {
        int result = 0;
        if (bytes.length == 4) {
            int a = (bytes[0] & 0xff) << 24;//说明二
            int b = (bytes[1] & 0xff) << 16;
            int c = (bytes[2] & 0xff) << 8;
            int d = (bytes[3] & 0xff);
            result = a | b | c | d;
        }
        return result;
    }

    /**
     * 将一个整形化为十六进制，并以字符串的形式返回
     */
    private final static String[] hexArray = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    /**
     * int转16字符串
     */
    public static String int2Hex(int intger) {
        //return Integer.toHexString(intger);
        if (intger < 0) {
            intger = intger + 256;
        }
        int d1 = intger / 16;
        int d2 = intger % 16;
        return hexArray[d1] + hexArray[d2];

    }

    /**
     * 16字符串转int
     */
    public static int hex2int(String hex) {
        return Integer.valueOf(hex, 16);
    }


    /*将字符串(不限于中文)转换为十六进制Unicode编码字符串*/
    public static String str2Unicode(String str) {
        str = (str == null ? "" : str);
        String tmpStr = "";
        StringBuffer sb = new StringBuffer(1024);
        char c;
        int j = 0;

        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            sb.append("\\u");

            j = (c >>> 8); //取出高8位
            tmpStr = Integer.toHexString(j);
            if (tmpStr.length() == 1) {
                sb.append("0");
            }
            sb.append(tmpStr);

            j = (c & 0xFF); //取出低8位
            tmpStr = Integer.toHexString(j);
            if (tmpStr.length() == 1) {
                sb.append("0");
            }
            sb.append(tmpStr);
        }

        return (sb.toString().replace("\\u", ""));
    }

    /*
     *  把十六进制Unicode编码字符串转换为中文字符串
     */
    public static String unicode2Str(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);

            str = str.replace(matcher.group(1), ch + "");
        }
        return str;

    }

    public static String getChiniseLen(String str) {
        return int2Hex(str.length() * 2);
    }

    public static String reverseLH(String str) {
        String hex = str.substring(2, 4) + str.substring(0, 2);

        return hex;
    }

    public static String long2Hex(long along) {
        String hex = Long.toHexString(along);
        if (hex.length() % 2 == 1) {
            hex = "0" + hex;
        }
        if (hex.length() == 2) {
            hex = "00" + hex;
        }
        return hex;
    }

    public static String long2HexReverse(long along) {
        String hex = Long.toHexString(along);
        if (hex.length() % 2 == 1) {
            hex = "0" + hex;
        }
        if (hex.length() == 2) {
            hex = "00" + hex;
        }
        return reverseLH(hex);
    }



    /**
     * 将16进制字符串转换成汉字
     *
     * @param str
     */
    public static String deUnicode(String str) {
        byte[] bytes = new byte[str.length() / 2];
        byte tempByte = 0;
        byte tempHigh = 0;
        byte tempLow = 0;
        for (int i = 0, j = 0; i < str.length(); i += 2, j++) {
            tempByte = (byte) (((int) str.charAt(i)) & 0xff);
            if (tempByte >= 48 && tempByte <= 57) {
                tempHigh = (byte) ((tempByte - 48) << 4);
            } else if (tempByte >= 97 && tempByte <= 101) {
                tempHigh = (byte) ((tempByte - 97 + 10) << 4);
            }
            tempByte = (byte) (((int) str.charAt(i + 1)) & 0xff);
            if (tempByte >= 48 && tempByte <= 57) {
                tempLow = (byte) (tempByte - 48);
            } else if (tempByte >= 97 && tempByte <= 101) {
                tempLow = (byte) (tempByte - 97 + 10);
            }
            bytes[j] = (byte) (tempHigh | tempLow);
        }
        String result = null;
        try {
            result = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 将16进制字符串转换成汉字
     * @return
     */


    public static String deUnicode2(String s) {
        if (s == null || s.equals("")) {


            return null;

        }

        s = s.replace(" ", "");

        byte[] baKeyword = new byte[s.length() / 2];

        for (int i = 0; i < baKeyword.length; i++) {


            try {


                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));

            } catch (Exception e) {

                e.printStackTrace();

            }

        }

        try {

            s = new String(baKeyword, "UTF-8");

            new String();

        } catch (Exception e1) {


            e1.printStackTrace();

        }

        return s;

    }
}
