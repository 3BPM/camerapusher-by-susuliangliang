package com.xxg.natx.client;

import com.xxg.natx.client.handler.NatxClientHandler;
import com.xxg.natx.client.net.TcpConnection;
import com.xxg.natx.common.codec.NatxMessageDecoder;
import com.xxg.natx.common.codec.NatxMessageEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import java.io.IOException;

public class NatxClient {
  public void connect(final String serverAddress, final int serverPort, final String password, final int remotePort, final String proxyAddress, final int proxyPort) throws IOException, InterruptedException {
    TcpConnection natxConnection = new TcpConnection();
    ChannelFuture future = natxConnection.connect(serverAddress, serverPort, new ChannelInitializer<SocketChannel>() {
          public void initChannel(SocketChannel ch) throws Exception {
            NatxClientHandler natxClientHandler = new NatxClientHandler(remotePort, password, proxyAddress, proxyPort);
            ch.pipeline().addLast(new ChannelHandler[] { new LengthFieldBasedFrameDecoder(2147483647, 0, 4, 0, 4), new NatxMessageDecoder(), new NatxMessageEncoder(), new IdleStateHandler(60, 30, 0), natxClientHandler });
          }
        });
    future.addListener(future1 -> (new Thread() {
          public void run() {
            while (true) {
              try {
                NatxClient.this.connect(serverAddress, serverPort, password, remotePort, proxyAddress, proxyPort);
                break;
              } catch (Exception e) {
                e.printStackTrace();
                try {
                  Thread.sleep(10000L);
                } catch (InterruptedException e1) {
                  e1.printStackTrace();
                } 
              } 
            } 
          }
        }).start());
  }
}
