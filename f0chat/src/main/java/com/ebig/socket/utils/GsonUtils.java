package com.ebig.socket.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class GsonUtils {
    public static <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        return new Gson().fromJson(json, classOfT);
    }

    public static String toJson(Object obj) {
        return new Gson().toJson(obj);
    }


    public static String stringToJSON(String strJson) {
        // 计数tab的个数
        int tabNum = 0;
        StringBuilder jsonFormat = new StringBuilder();
        int length = strJson.length();

        char last = 0;
        for (int i = 0; i < length; i++) {
            char c = strJson.charAt(i);
            if (c == '{') {
                tabNum++;
                jsonFormat.append(c).append("\n");
                jsonFormat.append(getSpaceOrTab(tabNum));
            } else if (c == '}') {
                tabNum--;
                jsonFormat.append("\n");
                jsonFormat.append(getSpaceOrTab(tabNum));
                jsonFormat.append(c);
            } else if (c == ',') {
                jsonFormat.append(c).append("\n");
                jsonFormat.append(getSpaceOrTab(tabNum));
            } else if (c == ':') {
                jsonFormat.append(c).append(" ");
            } else if (c == '[') {
                tabNum++;
                char next = strJson.charAt(i + 1);
                if (next == ']') {
                    jsonFormat.append(c);
                } else {
                    jsonFormat.append(c).append("\n");
                    jsonFormat.append(getSpaceOrTab(tabNum));
                }
            } else if (c == ']') {
                tabNum--;
                if (last == '[') {
                    jsonFormat.append(c);
                } else {
                    jsonFormat.append("\n").append(getSpaceOrTab(tabNum)).append(c);
                }
            } else {
                jsonFormat.append(c);
            }
            last = c;
        }
        return jsonFormat.toString();
    }

    // 是空格还是tab
    private static String getSpaceOrTab(int tabNum) {
        StringBuilder sbTab = new StringBuilder();
        for (int i = 0; i < tabNum; i++) {
            sbTab.append('\t');
        }
        return sbTab.toString();
    }

    /*解析json为列表实体*/
    public static <T> List<T> fromJsonList(String json, Class<T> classOfT) throws JsonSyntaxException {
        if (json == null) {
            return null;
        } else {
            return new Gson().fromJson(json,
                    new TypeToken<List<T>>() {
                    }.getType());
        }
    }
    public static boolean isJson(String str) {

        boolean result = false;

        if (OkChatStrUtils.notEmpty(str)) {

            str = str.trim();

            if (str.startsWith("{") && str.endsWith("}")) {

                result = true;

            } else if (str.startsWith("[") && str.endsWith("]")) {

                result = true;

            }

        }

        return result;

    }


}
