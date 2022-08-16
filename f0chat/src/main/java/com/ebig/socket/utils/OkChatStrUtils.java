package com.ebig.socket.utils;

import java.util.ArrayList;
import java.util.List;

public class OkChatStrUtils {
    public static boolean notEmpty(String obj){
        return obj!=null&&!obj.trim().equals("");
    }

    public static boolean empty(String obj){
        return obj==null||obj.trim().equals("");
    }

    public static boolean objNull(Object obj){
        return obj==null;
    }
    public static boolean objNotNull(Object obj){
        return obj!=null;
    }

    public static String format(String wholeCmd) {
        if (OkChatStrUtils.empty(wholeCmd))return "";
        StringBuilder stringBuilder=new StringBuilder();
        int len=wholeCmd.length();
        for (int i = 0; i < len; i++) {
            if (i%2==0){
                stringBuilder.append("[ "+wholeCmd.charAt(i));
            }else if (i%2==1){
                stringBuilder.append(wholeCmd.charAt(i)+" ]");
            }
        }
        return stringBuilder.toString();
    }

    public static List<String> spiltHex(String cmd) {
        if (OkChatStrUtils.empty(cmd)){
            return null;
        }
        int len=cmd.length()/2;
        List<String> list=new ArrayList<>();
        for (int i = 0; i < len; i++) {
            int index=i*2;
            list.add(cmd.substring(index,index+2));
        }
        // EbLogUtils.i("得到数组："+ list.toString());
        return list;
    }

    public static List<String> spiltHex6(String cmd) {
        if (OkChatStrUtils.empty(cmd)){
            return null;
        }
        int len=cmd.length()/6;
        List<String> list=new ArrayList<>();
        for (int i = 0; i < len; i++) {
            int index=i*6;
            list.add(cmd.substring(index,index+6));
        }
        // EbLogUtils.i("得到数组："+ list.toString());
        return list;
    }




    public static boolean equel(String type, String cmd) {
        return type.equals(cmd);
    }


    public static List<String> removeGroupId(List<String> realArr) {
        List<String> list=new ArrayList<>();
        for (String str:realArr){
            list.add(str.substring(0,4));
        }
        return list;
    }
}
