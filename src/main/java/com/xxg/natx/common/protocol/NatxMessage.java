package com.xxg.natx.common.protocol;

import java.util.Map;

public class NatxMessage {
  private NatxMessageType type;
  
  private Map<String, Object> metaData;
  
  private byte[] data;
  
  public NatxMessageType getType() {
    return this.type;
  }
  
  public void setType(NatxMessageType type) {
    this.type = type;
  }
  
  public Map<String, Object> getMetaData() {
    return this.metaData;
  }
  
  public void setMetaData(Map<String, Object> metaData) {
    this.metaData = metaData;
  }
  
  public byte[] getData() {
    return this.data;
  }
  
  public void setData(byte[] data) {
    this.data = data;
  }
}
