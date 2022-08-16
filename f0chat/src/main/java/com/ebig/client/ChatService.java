package com.ebig.client;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.ebig.socket.utils.OkChatLog;
import com.ebig.socket.utils.OkChatStrUtils;
import com.ebig.socket.utils.OkCommonCall;
import com.minjie.libcmd.ITHServiceCall;
import com.minjie.libcmd.IthListenner;

public class ChatService extends Service implements  OkCommonCall<String> {
    private String host;
    private int port;
    private OkChatClient client;

    @Override
    public void onCreate() {
        super.onCreate();
        OkChatLog.print("OkChatClient onCreate:");
    }

    @Override
    public void onOkCommonCall(String json) {
        OkChatLog.print("OkChatClient onOkCommonCall:" + json);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        OkChatLog.print("OkChatClient onBind:");
        host = intent.getStringExtra("host");
        port = intent.getIntExtra("port", 0);
        if (OkChatStrUtils.notEmpty(host) && port != 0) {
            OkChatLog.print("OkChatClient ip:" + host + " port:" + port);
//            client = new OkChatClient.Config(host, port).addReadCall(this).build();
//            client.start();
        }

        return serviceCall.asBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }



    private ITHServiceCall serviceCall = new ITHServiceCall.Stub() {
        @Override
        public void regist(IthListenner call) throws RemoteException {

        }

        @Override
        public void sendJson(String json) {
            OkChatLog.print("OkChatClient sendJson:" + json+" client:"+client);
            if (client != null) {
                client.send(json);
            }
        }
    };
}
