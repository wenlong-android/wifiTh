package com.ebig.socket.netty;
import com.ebig.client.ChatMsg;
import com.ebig.socket.utils.GsonUtils;
import com.ebig.socket.utils.OkChatHexUtils;
import com.ebig.socket.utils.OkChatLog;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class InBoundHandler extends SimpleChannelInboundHandler<byte[]> {
    private static SocketMonitorCall inBoundMessageCall;

    public static InBoundHandler getInstance(SocketMonitorCall call) {
        inBoundMessageCall = call;
        return new InBoundHandler();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        //往channel map中添加channel信息
        String uuid = OkChatIpUtils.getUuid(ctx);
        String ip = OkChatIpUtils.getIPString(ctx);
        Gateway.get().addGatewayChannel(ip , (SocketChannel) ctx.channel());
        OkChatLog.print("Netty CLIENT" + ctx.channel().id().asLongText());
        OkChatLog.print("Netty CLIENT" + OkChatIpUtils.getRemoteAddress(ctx) + " 接入连接");
        inBoundMessageCall.deviceConnect(uuid, ip);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //删除Channel Map中的失效Client
        String ip = OkChatIpUtils.getIPString(ctx);
        String uuid = OkChatIpUtils.getUuid(ctx);
        inBoundMessageCall.deviceDisConnect(uuid, ip);
        Gateway.get().removeGatewayChannel(ip);
        OkChatLog.print("Netty CLIENT" + OkChatIpUtils.getRemoteAddress(ctx) + " 断开连接");
        ctx.close();
        inBoundMessageCall.offline();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        //删除Channel Map中的失效Client
        String ip = OkChatIpUtils.getIPString(ctx);
        String uuid = OkChatIpUtils.getUuid(ctx);
        inBoundMessageCall.deviceDisConnect(uuid, ip);
        Gateway.get().removeGatewayChannel(ip);
        OkChatLog.print("Netty CLIENT"  + "exceptionCaught断开连接:"+cause.getMessage());
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,  byte[] msg) throws Exception {
        //OkChatLog.print("Netty CLIENT" +   "channelRead :"+msg);
        /*读取成功，重置输入流超时状态*/
        String ip = OkChatIpUtils.getIPString(ctx);
        String uuid = OkChatIpUtils.getUuid(ctx);
        String cmd = OkChatHexUtils.bytesToHex(msg);
        OkChatLog.print("Netty CLIENT" +   "channelRead :"+cmd);
        inBoundMessageCall.messageRead(ip,cmd);
    }

    /**
     * 超时监听，socket意义上的超时，并不是硬件通信的超时
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        String ip = OkChatIpUtils.getIPString(ctx);
        String uuid = OkChatIpUtils.getUuid(ctx);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {

                OkChatLog.print("Netty Client: " + ip + " READER_IDLE 读超时");
                inBoundMessageCall.readOutTime(uuid, ip);
                ctx.disconnect();
            } else if (event.state() == IdleState.WRITER_IDLE) {
                OkChatLog.print("Netty Client: " + ip + " WRITER_IDLE 写超时");
                inBoundMessageCall.writeOutTime(uuid, ip);
                ctx.disconnect();
            } else if (event.state() == IdleState.ALL_IDLE) {
                OkChatLog.print("Netty Client: " + ip + " ALL_IDLE 总超时");
                inBoundMessageCall.outTime(uuid, ip);
                ctx.disconnect();
            }
        }
    }


}
