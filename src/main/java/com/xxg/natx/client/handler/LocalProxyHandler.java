package com.xxg.natx.client.handler;

import com.xxg.natx.common.handler.NatxCommonHandler;
import com.xxg.natx.common.protocol.NatxMessage;
import com.xxg.natx.common.protocol.NatxMessageType;
import io.netty.channel.ChannelHandlerContext;
import java.util.HashMap;

public class LocalProxyHandler extends NatxCommonHandler {
  private NatxCommonHandler proxyHandler;
  
  private String remoteChannelId;
  
  public LocalProxyHandler(NatxCommonHandler proxyHandler, String remoteChannelId) {
    this.proxyHandler = proxyHandler;
    this.remoteChannelId = remoteChannelId;
  }
  
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    byte[] data = (byte[])msg;
    NatxMessage message = new NatxMessage();
    message.setType(NatxMessageType.DATA);
    message.setData(data);
    HashMap<String, Object> metaData = new HashMap<>();
    metaData.put("channelId", this.remoteChannelId);
    message.setMetaData(metaData);
    this.proxyHandler.getCtx().writeAndFlush(message);
  }
  
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    NatxMessage message = new NatxMessage();
    message.setType(NatxMessageType.DISCONNECTED);
    HashMap<String, Object> metaData = new HashMap<>();
    metaData.put("channelId", this.remoteChannelId);
    message.setMetaData(metaData);
    this.proxyHandler.getCtx().writeAndFlush(message);
  }
}
