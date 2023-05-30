package com.susu.camerapusher;

import com.xxg.natx.client.handler.NatxClientHandler;
import com.xxg.natx.client.net.TcpConnection;
import com.xxg.natx.common.codec.NatxMessageDecoder;
import com.xxg.natx.common.codec.NatxMessageEncoder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NatxUtils {
  private static final Logger log = LoggerFactory.getLogger(NatxUtils.class);
  
  public static void initNatx() {
    String serverAddress = CameraPusherConfig.INSTANCE.getServer();
    String serverPort = CameraPusherConfig.INSTANCE.getPort();
    final String password = CameraPusherConfig.INSTANCE.getPassword();
    if (serverAddress != null && !serverAddress.equals("") && serverPort != null && !serverPort.equals("") && password != null && !password.equals("")) {
      final String proxyAddress = "localhost";
      final String proxyPort = "6000";
      final String remotePort = "6000";
      TcpConnection natxConnection = new TcpConnection();
      try {
        natxConnection.connect(serverAddress, Integer.parseInt(serverPort), new ChannelInitializer<SocketChannel>() {
              public void initChannel(SocketChannel ch) throws Exception {
                NatxClientHandler natxClientHandler = new NatxClientHandler(Integer.parseInt(remotePort), password, proxyAddress, Integer.parseInt(proxyPort));
                ch.pipeline().addLast(new ChannelHandler[] { new LengthFieldBasedFrameDecoder(2147483647, 0, 4, 0, 4), new NatxMessageDecoder(), new NatxMessageEncoder(), new IdleStateHandler(60, 30, 0), natxClientHandler });
              }
            });
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } 
    } else {
      log.info("未填写内网穿透服务器，不启动映射服务");
    } 
  }
}
