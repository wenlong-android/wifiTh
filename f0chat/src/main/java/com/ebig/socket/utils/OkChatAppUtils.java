package com.ebig.socket.utils;

import android.app.Application;
import android.content.Context;

import java.lang.reflect.InvocationTargetException;

public class OkChatAppUtils {
 public static Context sApplication=null;
 public static Context getContext(){
     if (sApplication==null){
         if (sApplication == null) {
             try {
                 sApplication = (Application) Class.forName("android.app.ActivityThread")
                         .getMethod("currentApplication")
                         .invoke(null, (Object[]) null);
             } catch (IllegalAccessException e) {
                 e.printStackTrace();
             } catch (InvocationTargetException e) {
                 e.printStackTrace();
             } catch (NoSuchMethodException e) {
                 e.printStackTrace();
             } catch (ClassNotFoundException e) {
                 e.printStackTrace();
             }
         }
     }
     return sApplication;
 }
}
