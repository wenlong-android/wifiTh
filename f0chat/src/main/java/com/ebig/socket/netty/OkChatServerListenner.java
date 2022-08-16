package com.ebig.socket.netty;

import com.ebig.client.ChatMsg;

public interface OkChatServerListenner {
    void onConnectSuccess(String host);
    void onDisConnect(String host);
    void onRead(String host,double temperature,double humidity);

}
