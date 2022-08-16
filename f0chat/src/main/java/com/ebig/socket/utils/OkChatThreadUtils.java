package com.ebig.socket.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/*
 * 编写：wenlong on 2018/3/12 16:49
 * 企业QQ： 2853239883
 * 钉钉：13430330686
 */
public class OkChatThreadUtils {
    public static OkChatThreadUtils ins;
    public static Handler handler;

    public static OkChatThreadUtils getIns() {
        if (ins == null) {
            synchronized (OkChatThreadUtils.class) {
                if (ins == null) {
                    ins = new OkChatThreadUtils();
                }
            }
        }
        return ins;
    }

    //切换到主线程，只是切换到主线程，不做耗时操作
    public void runOnMainThread(Runnable runnable) {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        handler.post(runnable);
    }

    //切换到主线程,只是切换到主线程，不做耗时操作
    public void runOnMainThread(Runnable runnable, long delay) {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        handler.postDelayed(runnable, delay);
    }

    private static ExecutorService executorService = null;

    //创建单个线程时候使用，尽可能避免使用野线程
    public void runSingleThread(Runnable runnable) {
        int NUMBAR_OF_CORES = Runtime.getRuntime().availableProcessors();
         //MjLog.print("NUMBAR_OF_CORES:"+NUMBAR_OF_CORES);
        int KEEP_ALIVE_TIME = 1;//设置存活时间，确保空闲时线程能被释放
        TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.DAYS.SECONDS;
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(NUMBAR_OF_CORES,
                    NUMBAR_OF_CORES * 2, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, taskQueue);
        }
        executorService.execute(runnable);
    }
    private static ExecutorService executorService2 = null;
    //创建单个线程时候使用，尽可能避免使用野线程
    public void runThread(Runnable runnable) {
        if (executorService2 == null) {
            executorService2=Executors.newSingleThreadExecutor();
        }
        executorService2.execute(runnable);
    }
    private void sdfd() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        FutureTask<String> future = new FutureTask<String>(new Callable<String>() {
            //使用Callable接口作为构造参数
            public String call() {
                //真正的任务在这里执行，这里的返回值类型为String，可以为任意类型
                return "";
            }
        });
        executor.execute(future);
//在这里可以做别的任何事情
        try {
            String result = future.get(5000, TimeUnit.MILLISECONDS); //取得结果，同时设置超时执行时间为5秒。同样可以用future.get()，不设置执行超时时间取得结果
        } catch (InterruptedException e) {
            future.cancel(true);
        } catch (ExecutionException e) {
            future.cancel(true);
        } catch (TimeoutException e) {
            future.cancel(true);
        } finally {
            executor.shutdown();
        }
    }

}
