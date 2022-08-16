package com.ebig.service;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class PackUtils {
    /**
     * 判断服务是否在运行
     * @param mContext
     * @param className　　Service.class.getName();
     * @return
     */
    public static boolean isServiceRunning(Context mContext, String className){
        boolean isRunning = false ;
        ActivityManager activityManager = (ActivityManager)   mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> seviceList = activityManager.getRunningServices(200);
        //200:是运行的service的集合大小，当设置太小时，我没有获取到正在运行的Serice
        if (seviceList.size() <= 0){
            return false;
        }
        for (int i=0 ;i < seviceList.size();i++){
            if (seviceList.get(i).service.getClassName().toString().equals(className)){
                isRunning = true;
                break;
            }
        }
        return  isRunning;
    }

    /**
     * 获取当前进程名
     *
     * @param context 上下文
     * @return
     */
    public static String getCurProcessName(Context context) {
        // 获取此进程的标识符
        int pid = android.os.Process.myPid();
        // 获取活动管理器
        ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        // 从应用程序进程列表找到当前进程，是：返回当前进程名
        for (ActivityManager.RunningAppProcessInfo appProcess :
                activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

}
