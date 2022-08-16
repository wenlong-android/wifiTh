package com.ebig.client;

public interface ChatReadListenner {
    void onConnectStatus(boolean isConnect);
    void onRead(ChatMsg chatMsg);
}
