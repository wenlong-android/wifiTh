package com.ebig.client;

import androidx.annotation.IntDef;


@IntDef({
        ChatTimeOut.sec5,
        ChatTimeOut.sec8,
        ChatTimeOut.sec10,
        ChatTimeOut.sec20,
        ChatTimeOut.sec30,
})

public @interface ChatTimeOut {
    final static int sec5 = 5000;
    final static int sec8 = 8000;
    final static int sec10 = 10000;
    final static int sec20 = 20000;
    final static int sec30 = 30000;
}
