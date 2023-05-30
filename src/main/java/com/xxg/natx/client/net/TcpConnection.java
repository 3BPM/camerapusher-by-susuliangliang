package com.xxg.natx.client.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import java.io.IOException;

public class TcpConnection {
  public ChannelFuture connect(String host, int port, ChannelInitializer channelInitializer) throws InterruptedException, IOException {
    NioEventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      Bootstrap b = new Bootstrap();
      b.group(workerGroup);
      b.channel((Class)NioSocketChannel.class);
      b.option(ChannelOption.SO_KEEPALIVE, Boolean.valueOf(true));
      b.handler(channelInitializer);
      Channel channel = b.connect(host, port).sync().channel();
      return channel.closeFuture().addListener(future -> workerGroup.shutdownGracefully());
    } catch (Exception e) {
      workerGroup.shutdownGracefully();
      throw e;
    } 
  }
}
