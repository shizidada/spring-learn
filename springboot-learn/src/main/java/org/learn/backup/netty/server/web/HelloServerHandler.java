//package org.learn.backup.netty.server.web;
//
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelHandler;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.channel.group.ChannelGroup;
//import io.netty.channel.group.DefaultChannelGroup;
//import io.netty.util.concurrent.GlobalEventExecutor;
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Service;
//
//import java.net.InetAddress;
//
//@Service("helloServerHandler")
//@Scope("prototype")
//@ChannelHandler.Sharable
//public class HelloServerHandler extends SimpleChannelInboundHandler<String> {
//    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
//
//    @Override
//    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {  // (2)
//        Channel incoming = ctx.channel();
//        for (Channel channel : channels) {
//            channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " 加入\n");
//        }
//        channels.add(incoming);
//    }
//
//    @Override
//    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {  // (3)
//        Channel incoming = ctx.channel();
//        for (Channel channel : channels) {
//            channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " 离开\n");
//        }
//        channels.remove(incoming);
//    }
//
//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
//        // 收到消息直接打印输出
//        System.out.println(ctx.channel().remoteAddress() + " Say : " + msg);
//
//        // 返回客户端消息 - 我已经接收到了你的消息
//        ctx.writeAndFlush("Received your message !\n");
//    }
//
//    /*
//     * 覆盖 channelActive 方法 在channel被启用的时候触发 (在建立连接的时候)
//     * */
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//
//        System.out.println("RamoteAddress : " + ctx.channel().remoteAddress() + " active !");
//
//        ctx.writeAndFlush("Welcome to " + InetAddress.getLocalHost().getHostName() + " service!\n");
//
//        super.channelActive(ctx);
//    }
//
//}
