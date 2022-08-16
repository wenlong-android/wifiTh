package com.ebig.socket.netty;

import android.content.Context;

import com.ebig.socket.utils.OkChatAppUtils;
import com.ebig.socket.utils.OkChatLog;
import com.ebig.socket.utils.OkCommonCall;
import com.ebig.socket.utils.OkChatThreadUtils;


import java.util.Timer;
import java.util.TimerTask;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class WifiThServer implements SocketMonitorCall {
    private ChannelFuture channelFuture = null;
    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;
    private static Channel serverChannel;
    private static int mPort = 0;
    private static ServerBootstrap bootstrap;
    private static OkChatServerListenner okChatServerListenner;
    private static Context mContext;
    public static WifiThServer getInstance() {
        if (mContext==null){
            mContext= OkChatAppUtils.getContext();
        }
        return new WifiThServer();
    }

    private static Timer reboot;

    public WifiThServer start(int port) {
        this.mPort = port;
        OkChatThreadUtils.getIns().runSingleThread(new Runnable() {
            @Override
            public void run() {
                realStart(port);
            }
        });
        return WifiThServer.this;
    }

    public WifiThServer addListenner(OkChatServerListenner l) {
        this.okChatServerListenner = l;
        return WifiThServer.this;
    }

    public void realStart(int port) {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        try {
            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 保持长连接状态
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            //长度协议的解码器
//                            ch.pipeline().addLast(new ObjectEncoder());
//                            ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                            ch.pipeline().addLast("bytesDecoder", new ByteArrayDecoder());
                            ch.pipeline().addLast("bytesEncoder", new ByteArrayEncoder());
                            //ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(104857600,0,2));
                            //ch.pipeline().addLast(new OutBoundHandler());
                            ch.pipeline().addLast(
                                    new IdleStateHandler(
                                            0,
                                            0,
                                            60),
                                    InBoundHandler.getInstance(WifiThServer.this));
                        }
                    });
            connectAsync(port, bootstrap);
        } catch (Exception e) {
             bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            shutDown();
        }
    }

    public void connectAsync(int port, ServerBootstrap b) throws InterruptedException {
        channelFuture = b.bind(port);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    OkChatLog.print("服务器启动成功");
                } else {
                    OkChatLog.print("服务器启动失败");
                    if (reboot == null) {
                        reboot = new Timer();
                    }
                    //10秒后重启
                    reboot.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            start(mPort);
                        }
                    }, 10000);
                }
            }
        });
        // 将ServerChannel保存下来
        serverChannel = channelFuture.channel();
        // 阻塞至channel关闭
        serverChannel.closeFuture().sync();
    }

    @Override
    public void offline() {

    }

    @Override
    public void deviceConnect(String uuid, String ipHost) {
        //  mInBoundMessageCall.deviceConnect(uuid, ipHost);
        if (okChatServerListenner!=null){
            okChatServerListenner.onConnectSuccess(ipHost);
        }
    }

    @Override
    public void deviceDisConnect(String uuid, String ipHost) {
        //  mInBoundMessageCall.deviceDisConnect(uuid, ipHost);
        if (okChatServerListenner!=null){
            okChatServerListenner.onDisConnect(ipHost);
        }
    }

    @Override
    public void messageRead(String host,String cmd) {
        CmdResultInfo info=CmdResultInfo.load("",host,cmd);
        if (cmd.startsWith("7e")&&cmd.endsWith("7f")&&cmd.length()==42){
            if (info.getCmdArray()!=null&&info.getCmdArray().size()==21
            &&info.getCmdArray().get(13).equals("03")){
                String data=info.getData();
                String th = data.substring(14,data.length());

                // if (th.startsWith("03")){
                // th.replace("03","");
                String temperature = th.substring(0, 4);
                String humidity = th.substring(4, 8);
                String fianlT = temperature.substring(2, 4) + temperature.substring(0, 2);
                String fianlH = humidity.substring(2, 4) + humidity.substring(0, 2);
                double temperatureInt = (hex2int(fianlT) * 175.72) / 65536.0 - 46.85;
                double humidityInt = (hex2int(fianlH) * 125.0) / 65536.0 - 6;
                OkChatLog.print("Listenner4NewTh 温度:"+temperatureInt+" ,湿度："+humidityInt);
                //  }
                if (okChatServerListenner!=null){
                    okChatServerListenner.onRead(host,temperatureInt,humidityInt);
                }
            }

        }

    }

    @Override
    public void writeOutTime(String uuid, String ipHost) {
        // mInBoundMessageCall.writeOutTime(uuid, ipHost);
    }

    @Override
    public void readOutTime(String uuid, String ipHost) {
        // mInBoundMessageCall.readOutTime(uuid, ipHost);
    }

    @Override
    public void outTime(String uuid, String ipHost) {
        // mInBoundMessageCall.outTime(uuid, ipHost);
    }



    /**
     * 关闭当前server
     */

    public void shutDown() {
        if (serverChannel != null) {
            serverChannel.close();
            serverChannel = null;
        }
    }

    public boolean isServerAlive() {
        return serverChannel != null;
    }

    public void send(String host, byte[] msg) {
        send(host, msg, null);
    }

    public void send(String  host, byte[] msg, OkCommonCall<Integer> resultCall) {
        SocketChannel channel=Gateway.get().getGatewayChannel(host);
        if (channel!=null){
            channel.writeAndFlush(msg)
                    .addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            if (resultCall != null) {
                                resultCall.onOkCommonCall(channelFuture.isSuccess()?0:1);
                            }
                        }
                    });
        }else {
            resultCall.onOkCommonCall(2);
        }

    }
    public static int hex2int(String hex) {
        return Integer.valueOf(hex, 16);
    }
}
