package com.ebig.socket.netty;

import com.ebig.client.ChatMsg;

public interface SocketMonitorCall {
    void deviceConnect(String uuid,String ipHost);
    void deviceDisConnect(String uuid,String ipHost);
    void messageRead(String host,String msg);
    void writeOutTime(String uuid,String ipHost);
    void readOutTime(String uuid,String ipHost);
    void outTime(String uuid,String ipHost);
    void offline();


}
