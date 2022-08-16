package com.ebig.client;

import android.content.Context;

import com.ebig.socket.netty.OkChatIpUtils;
import com.ebig.socket.utils.OkChatAppUtils;
import com.ebig.socket.utils.GsonUtils;
import com.ebig.socket.utils.OkCommonCall;
import com.ebig.socket.utils.OkChatLog;


import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class OkChatClient {
    private static class L{
        private static OkChatClient chatClient=new OkChatClient();
    }
    public static OkChatClient getInstance(){
        return L.chatClient;
    }
    private String host = "192.168.10.88";
    private int port = 9413;
    private ChatReadListenner listenner;
    private static Bootstrap bootstrap = null;
    private static EventLoopGroup workerGroup = null;
    private static ClientHandler clientHandler;
    private static Channel channelMap = null;
    private static Context mContext;
    private SocketChannel socketChannel;
    private volatile Timer connectTimer;
    private @ChatTimeOut int timeOut=ChatTimeOut.sec10;
    private Timer timeOutTimer;

    public void connect(String host,int port){
        this.host=host;
        this.port=port;
        start();
    }

    public OkChatClient addListenner(ChatReadListenner l){
        this.listenner=l;
        return OkChatClient.this;
    }

    public OkChatClient timeout(@ChatTimeOut int timeout){
        this.timeOut=timeout;
        return OkChatClient.this;
    }



    public void start() {
        mContext = OkChatAppUtils.getContext();
        OkChatLog.print("OkChatClient start connect service ...");
        bootstrap = null;
        workerGroup = null;
        bootstrap = new Bootstrap();
        workerGroup = new NioEventLoopGroup();
        bootstrap.group(workerGroup)
                .option(ChannelOption.TCP_NODELAY, true) // 不延迟，直接发送
                .option(ChannelOption.SO_KEEPALIVE, true) // 保持长连接状态
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        //字符串编码解码
                        pipeline.addLast(new IdleStateHandler(0, 30, 0));
                        pipeline.addLast(new ObjectEncoder());
                        pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                        //pipeline.addLast(new OutBoundHandler(listenner));
                        clientHandler = new ClientHandler(OkChatClient.this, listenner);
                        pipeline.addLast(clientHandler);
                    }
                }).connect(new InetSocketAddress(host, port))
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        OkChatLog.print("OkChatClient 连接:" + (channelFuture.isSuccess() ? "成功" : "失败"));
                        if (!channelFuture.isSuccess()) {
                            if (connectTimer == null) {
                                connectTimer = new Timer();
                            }
                            connectTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    start();
                                }
                            }, 5000);
                            channelFuture.channel().close();
                            workerGroup.shutdownGracefully();
                        } else {
                            OkChatLog.print("OkChatClient 连接成功");
                            //socketChannel = (SocketChannel) channelFuture.channel();
                            timeOutTimer=new Timer();
                            timeOutTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {

                                }
                            },timeOut);
                        }
                    }
                });

    }


    /**
     * 客户端发送消息
     *
     * @param message
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private static long pauseTime = 0;

    public void send(String message, OkCommonCall<Boolean> commonCall) {
        if (channelMap != null) {
            OkChatLog.print("OkChatClient 发送内容：" + message);
            ChatMsg msg = new ChatMsg(1,OkChatIpUtils.getIPAddress(mContext), message, System.currentTimeMillis());
            channelMap.writeAndFlush(GsonUtils.toJson(msg)).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    OkChatLog.print("OkChatClient 发送：" + (channelFuture.isSuccess() ? "成功" : "失败"));
                    if (commonCall != null) {
                        commonCall.onOkCommonCall(channelFuture.isSuccess());
                    }
                }
            });

        } else {
            OkChatLog.print("OkChatClient 发送：channelMap != null");
        }
    }

    public void send(String message) {
        send(message, null);
    }


    public Channel getChannelMap() {
        return channelMap;
    }

    public void setChannelMap(Channel channelMap) {
        this.channelMap = channelMap;
    }


}
