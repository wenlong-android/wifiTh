package com.ebig.client;

public class ChatMsg {
    private int type;//0:超时应答 1：消息
    private String host;
    private String content;
    private long timeStamp;

    public ChatMsg(int type, String host, String content, long timeStamp) {
        this.type = type;
        this.host = host;
        this.content = content;
        this.timeStamp = timeStamp;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
