package com.ebig.client;

import com.ebig.socket.utils.GsonUtils;
import com.ebig.socket.utils.OkChatLog;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ClientHandler extends SimpleChannelInboundHandler<String> {
    private OkChatClient nettyClient;
    private String tenantId;
    private int attempts = 0;
    private ChannelPromise promise;
    private ChannelHandlerContext context;
    private String ip;
    private ChatReadListenner listenner;

    public ClientHandler(OkChatClient nettyClient, ChatReadListenner listenner) {
        this.nettyClient = nettyClient;
        this.listenner = listenner;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String message) throws Exception {
        OkChatLog.print("OkChatClient service send read" + message);
        if (GsonUtils.isJson(message)){
            ChatMsg msg=GsonUtils.fromJson(message,ChatMsg.class);
            if (listenner != null) {
                listenner.onRead(msg);
            }
        }


    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        OkChatLog.print("OkChatClient service channelReadComplete");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        OkChatLog.print("OkChatClient output connected!");
        nettyClient.setChannelMap(ctx.channel());
        listenner.onConnectStatus(true);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx)  {
        OkChatLog.print("OkChatClient channelInactive 使用过程中断线重连 "  );
        //使用过程中断线重连
        try {
            super.handlerRemoved(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
        nettyClient.start();
        listenner.onConnectStatus(false);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        OkChatLog.print("OkChatClient exceptionCaught " + cause.getMessage());
        ctx.close();
        nettyClient.setChannelMap(null);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        nettyClient.setChannelMap(null);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                OkChatLog.print("OkChatClient READER_IDLE");
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                //发送心跳，保持长连接
                String s = "NettyClient" + System.getProperty("line.separator");
                ctx.channel().writeAndFlush(s);  //发送心跳成功
                OkChatLog.print("OkChatClient WRITER_IDLE");
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                OkChatLog.print("OkChatClient ALL_IDLE");
            }
        }
        super.userEventTriggered(ctx, evt);
    }

}
