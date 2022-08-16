package com.minjie.libcmd;

// Declare any non-default types here with import statements
import com.minjie.libcmd.IthListenner;
interface ITHServiceCall {
    void regist(IthListenner call);
    void sendJson(String json);
}