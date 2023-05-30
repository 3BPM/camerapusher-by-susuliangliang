package com.xxg.natx.common.protocol;

import com.xxg.natx.common.exception.NatxException;

public enum NatxMessageType {
  REGISTER(1),
  REGISTER_RESULT(2),
  CONNECTED(3),
  DISCONNECTED(4),
  DATA(5),
  KEEPALIVE(6);
  
  private int code;
  
  NatxMessageType(int code) {
    this.code = code;
  }
  
  public int getCode() {
    return this.code;
  }
}
