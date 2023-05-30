package com.xxg.natx.common.handler;

import com.xxg.natx.common.protocol.NatxMessage;
import com.xxg.natx.common.protocol.NatxMessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class NatxCommonHandler extends ChannelInboundHandlerAdapter {
  protected ChannelHandlerContext ctx;
  
  public ChannelHandlerContext getCtx() {
    return this.ctx;
  }
  
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    this.ctx = ctx;
  }
  
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    System.out.println("Exception caught ...");
    cause.printStackTrace();
  }
  
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      IdleStateEvent e = (IdleStateEvent)evt;
      if (e.state() == IdleState.READER_IDLE) {
        System.out.println("Read idle loss connection.");
        ctx.close();
      } else if (e.state() == IdleState.WRITER_IDLE) {
        NatxMessage natxMessage = new NatxMessage();
        natxMessage.setType(NatxMessageType.KEEPALIVE);
        ctx.writeAndFlush(natxMessage);
      } 
    } 
  }
}
