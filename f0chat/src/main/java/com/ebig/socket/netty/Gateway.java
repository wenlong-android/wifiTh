package com.ebig.socket.netty;

import android.os.Build;

import com.ebig.service.ClientInfo;
import com.ebig.socket.utils.OkChatLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.socket.SocketChannel;

public class Gateway {
    private static  ConcurrentHashMap<String, SocketChannel> hashMap = new ConcurrentHashMap<>();
    public static class L{
        private static Gateway gateway=new Gateway();
    }
    public static Gateway get(){
        return L.gateway;
    }


    public  void addGatewayChannel(String id, SocketChannel gateway_channel) {
        OkChatLog.print("addGatewayChannel:"+id);
        if (hashMap.containsKey(id)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                hashMap.replace(id,gateway_channel);
            }
        }else {
            hashMap.put(id, gateway_channel);
        }
        checkCache();
    }

    public   SocketChannel getGatewayChannel(String host) {
        if (hashMap.containsKey(host)){
            return  hashMap.get(host);
        }
        return null;
    }

    public static void removeGatewayChannel(String id) {
        OkChatLog.print("removeGatewayChannel:"+id);
        hashMap.remove(id);
        checkCache();
    }



    /**
     * 获取所有连接客户端的SocketChannel
     * @return
     */

    public static List<ClientInfo> getClient() {
        if (hashMap.size() != 0) {
            List<ClientInfo> channelList = new ArrayList<>();
            for (String host : hashMap.keySet()) {
                channelList.add(new ClientInfo(host,hashMap.get(host)));
            }
            return channelList;
        }
        return null;
    }
    private static void checkCache(){
        for (String host : hashMap.keySet()) {
            OkChatLog.print("现有频道："+host);
        }
    }
}
