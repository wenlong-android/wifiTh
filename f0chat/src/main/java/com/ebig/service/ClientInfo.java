package com.ebig.service;

import io.netty.channel.socket.SocketChannel;

public class ClientInfo {
    private String host;
    private SocketChannel socketChannel;

    public ClientInfo(String host, SocketChannel socketChannel) {
        this.host = host;
        this.socketChannel = socketChannel;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }
}
