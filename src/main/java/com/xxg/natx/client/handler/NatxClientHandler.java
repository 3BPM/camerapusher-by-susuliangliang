package com.xxg.natx.client.handler;

import com.xxg.natx.client.net.TcpConnection;
import com.xxg.natx.common.exception.NatxException;
import com.xxg.natx.common.handler.NatxCommonHandler;
import com.xxg.natx.common.protocol.NatxMessage;
import com.xxg.natx.common.protocol.NatxMessageType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class NatxClientHandler extends NatxCommonHandler {
  private int port;
  
  private String password;
  
  private String proxyAddress;
  
  private int proxyPort;
  
  private ConcurrentHashMap<String, NatxCommonHandler> channelHandlerMap = new ConcurrentHashMap<>();
  
  private ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
  
  public NatxClientHandler(int port, String password, String proxyAddress, int proxyPort) {
    this.port = port;
    this.password = password;
    this.proxyAddress = proxyAddress;
    this.proxyPort = proxyPort;
  }
  
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    NatxMessage natxMessage = new NatxMessage();
    natxMessage.setType(NatxMessageType.REGISTER);
    HashMap<String, Object> metaData = new HashMap<>();
    metaData.put("port", Integer.valueOf(this.port));
    metaData.put("password", this.password);
    natxMessage.setMetaData(metaData);
    ctx.writeAndFlush(natxMessage);
    super.channelActive(ctx);
  }
  
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    NatxMessage natxMessage = (NatxMessage)msg;
    if (natxMessage.getType() == NatxMessageType.REGISTER_RESULT) {
      processRegisterResult(natxMessage);
    } else if (natxMessage.getType() == NatxMessageType.CONNECTED) {
      processConnected(natxMessage);
    } else if (natxMessage.getType() == NatxMessageType.DISCONNECTED) {
      processDisconnected(natxMessage);
    } else if (natxMessage.getType() == NatxMessageType.DATA) {
      processData(natxMessage);
    } else if (natxMessage.getType() != NatxMessageType.KEEPALIVE) {
      throw new NatxException("Unknown type: " + natxMessage.getType());
    } 
  }
  
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    this.channelGroup.close();
    System.out.println("Loss connection to Natx server, Please restart!");
  }
  
  private void processRegisterResult(NatxMessage natxMessage) {
    if (((Boolean)natxMessage.getMetaData().get("success")).booleanValue()) {
      System.out.println("Register to Natx server");
    } else {
      System.out.println("Register fail: " + natxMessage.getMetaData().get("reason"));
      this.ctx.close();
    } 
  }
  
  private void processConnected(final NatxMessage natxMessage) throws Exception {
    try {
      final NatxClientHandler thisHandler = this;
      TcpConnection localConnection = new TcpConnection();
      localConnection.connect(this.proxyAddress, this.proxyPort, new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
              LocalProxyHandler localProxyHandler = new LocalProxyHandler(thisHandler, natxMessage.getMetaData().get("channelId").toString());
              ch.pipeline().addLast(new ChannelHandler[] { new ByteArrayDecoder(), new ByteArrayEncoder(), localProxyHandler });
              NatxClientHandler.this.channelHandlerMap.put(natxMessage.getMetaData().get("channelId").toString(), localProxyHandler);
              NatxClientHandler.this.channelGroup.add(ch);
            }
          });
    } catch (Exception e) {
      NatxMessage message = new NatxMessage();
      message.setType(NatxMessageType.DISCONNECTED);
      HashMap<String, Object> metaData = new HashMap<>();
      metaData.put("channelId", natxMessage.getMetaData().get("channelId"));
      message.setMetaData(metaData);
      this.ctx.writeAndFlush(message);
      this.channelHandlerMap.remove(natxMessage.getMetaData().get("channelId"));
      throw e;
    } 
  }
  
  private void processDisconnected(NatxMessage natxMessage) {
    String channelId = natxMessage.getMetaData().get("channelId").toString();
    NatxCommonHandler handler = this.channelHandlerMap.get(channelId);
    if (handler != null) {
      handler.getCtx().close();
      this.channelHandlerMap.remove(channelId);
    } 
  }
  
  private void processData(NatxMessage natxMessage) {
    String channelId = natxMessage.getMetaData().get("channelId").toString();
    NatxCommonHandler handler = this.channelHandlerMap.get(channelId);
    if (handler != null) {
      ChannelHandlerContext ctx = handler.getCtx();
      ctx.writeAndFlush(natxMessage.getData());
    } 
  }
}
