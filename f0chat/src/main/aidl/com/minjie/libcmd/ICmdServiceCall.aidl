package com.minjie.libcmd;

// Declare any non-default types here with import statements
import com.minjie.libcmd.IResultListenner;
interface ICmdServiceCall {
    void regist(IResultListenner call);
    void sendCmd(String cmd);
}