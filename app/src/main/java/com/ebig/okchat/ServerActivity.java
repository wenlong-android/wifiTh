package com.ebig.okchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.ebig.socket.netty.WifiThServer;
import com.ebig.socket.netty.WifiThListenner;

public class ServerActivity extends AppCompatActivity implements WifiThListenner {
    private TextView btn_content;
    private long count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        btn_content = (TextView) findViewById(R.id.btn_content);
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiThServer.getInstance().addListenner(ServerActivity.this).start(8080);
            }

        });

    }

    @Override
    public void onConnectSuccess(String host) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ServerActivity.this, "客户端：" + host + " 加入连接", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDisConnect(String host) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ServerActivity.this, "客户端：" + host + " 断开连接", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onStartResult(boolean success) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Override
    public void onRead(String host, double temperature, double humidity) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (count>1000000){
                    count=1;
                }
                btn_content.setText(
                        "统计次数：" + count +
                                "\n设备：" + host +
                                "\n温度：" + temperature +
                                "\n湿度：" + humidity);
                count++;
            }
        });
    }
}