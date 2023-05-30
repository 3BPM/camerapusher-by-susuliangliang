package com.xxg.natx.common.codec;

import com.xxg.natx.common.protocol.NatxMessage;
import com.xxg.natx.common.protocol.NatxMessageType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

public class NatxMessageDecoder extends MessageToMessageDecoder<ByteBuf> {
  protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
    int type = msg.readInt();
    NatxMessageType natxMessageType = NatxMessageType.valueOf(String.valueOf(type));
    int metaDataLength = msg.readInt();
    CharSequence metaDataString = msg.readCharSequence(metaDataLength, CharsetUtil.UTF_8);
    JSONObject jsonObject = new JSONObject(metaDataString.toString());
    Map<String, Object> metaData = jsonObject.toMap();
    byte[] data = null;
    if (msg.isReadable())
      data = ByteBufUtil.getBytes(msg); 
    NatxMessage natxMessage = new NatxMessage();
    natxMessage.setType(natxMessageType);
    natxMessage.setMetaData(metaData);
    natxMessage.setData(data);
    out.add(natxMessage);
  }


}
