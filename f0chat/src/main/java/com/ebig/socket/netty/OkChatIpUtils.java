package com.ebig.socket.netty;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import io.netty.channel.ChannelHandlerContext;

public class OkChatIpUtils {
    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager)context.
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                if (ipAddress == 0) return "未连接wifi";
                return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                        + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
            }
        } else {
            //当前无网络连接,请在设置中打开网络
            return "当前无网络连接,请在设置中打开网络";
        }
        return "IP获取失败";
    }
    public static String getIPString(ChannelHandlerContext ctx) {
        String ipString = "";
        String socketString = ctx.channel().remoteAddress().toString();
        int colonAt = socketString.indexOf(":");
        ipString = socketString.substring(1, colonAt);
        return ipString;
    }


    public static String getRemoteAddress(ChannelHandlerContext ctx) {
        String socketString = "";
        socketString = ctx.channel().remoteAddress().toString();
        return socketString.replace("/","");
    }


    private String getKeyFromArray(byte[] addressDomain) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < 5; i++) {
            sBuffer.append(addressDomain[i]);
        }
        return sBuffer.toString();
    }

    protected String to8BitString(String binaryString) {
        int len = binaryString.length();
        for (int i = 0; i < 8 - len; i++) {
            binaryString = "0" + binaryString;
        }
        return binaryString;
    }

    protected static byte[] combine2Byte(byte[] bt1, byte[] bt2) {
        byte[] byteResult = new byte[bt1.length + bt2.length];
        System.arraycopy(bt1, 0, byteResult, 0, bt1.length);
        System.arraycopy(bt2, 0, byteResult, bt1.length, bt2.length);
        return byteResult;
    }

    public static String getUuid(ChannelHandlerContext ctx){
        return ctx.channel().id().asLongText();
    }
}
