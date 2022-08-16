package com.ebig.socket.netty;

import com.ebig.socket.utils.OkChatHexUtils;
import com.ebig.socket.utils.OkChatStrUtils;

import java.util.List;

public class BaseMatchFactoryHelper {
    private static final int STANDRE_EXTRALEN=4;

    protected static CmdResultInfo makeCommon(String uuid, String host, String cmd){
        CmdResultInfo originInfo= CmdResultInfo.load(uuid,host,cmd);
        return originInfo;
    }

    public static List<String> getCmdArray(String cmd) {
        if (OkChatStrUtils.empty(cmd)){
            return null;
        }
       return OkChatStrUtils.spiltHex(cmd);
    }
    public static String getCmdData(List<String> cmdArray) {
        //获取数据长度
        int len= OkChatHexUtils.hex2int(cmdArray.get(1));
        //除了数据区外其他标准必备字段长度为10
        if (cmdArray.size()>STANDRE_EXTRALEN) {
            int cmdArrayLen=cmdArray.size();
            StringBuilder sb=new StringBuilder();
            for (int i=7;i<cmdArrayLen-3;i++){
                sb.append(cmdArray.get(i));
            }
            return sb.toString();
        }
        return "";
    }
}
