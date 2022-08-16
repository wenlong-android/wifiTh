package com.ebig.socket.netty;


import com.ebig.socket.utils.OkChatLog;
import com.ebig.socket.utils.OkChatStrUtils;

import java.util.List;

public class CmdResultInfo {
    private String uuid;
    private String host;
    private long timeStamp;
    private String cmd;
    private String data;
    private String type;
    private String order;
    private List<String> cmdArray;
    private List<String> dataArray;

    public CmdResultInfo(String uuid, String host, String cmd, long timeStamp) {
        this.uuid = uuid;
        this.host = host;
        this.timeStamp = timeStamp;
        this.cmd = cmd;
    }

    public static CmdResultInfo load(String uuid, String host, String cmd) {
        CmdResultInfo info = new CmdResultInfo(uuid, host, cmd, System.currentTimeMillis());
        List<String> cmdArr= BaseMatchFactoryHelper.getCmdArray(cmd);
        if (OkChatStrUtils.objNotNull(cmdArr)){
            OkChatLog.print("获取到的指令分组："+cmd);
            info.setCmdArray(cmdArr);
            info.setOrder(cmdArr.get(3));
            String data=BaseMatchFactoryHelper.getCmdData(cmdArr);
            info.setData(data);
            info.setDataArray(BaseMatchFactoryHelper.getCmdArray(data));
        }
        return info;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getCmdArray() {
        return cmdArray;
    }

    public void setCmdArray(List<String> cmdArray) {
        this.cmdArray = cmdArray;
    }

    public List<String> getDataArray() {
        return dataArray;
    }

    public void setDataArray(List<String> dataArray) {
        this.dataArray = dataArray;
    }

    @Override
    public String toString() {
        return "CmdResultInfo{" +
                "uuid='" + uuid + '\'' +
                ", host='" + host + '\'' +
                ", timeStamp=" + timeStamp +
                ", cmd='" + cmd + '\'' +
                ", data='" + data + '\'' +
                ", type='" + type + '\'' +
                ", order='" + order + '\'' +
                ", cmdArray=" + cmdArray +
                ", dataArray=" + dataArray +
                '}';
    }
}
