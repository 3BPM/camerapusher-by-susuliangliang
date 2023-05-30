package com.susu.camerapusher;

public class CameraPusherConfig {
  public void setCameraindex(int cameraindex) {
    this.cameraindex = cameraindex;
  }
  
  public void setWidth(int width) {
    this.width = width;
  }
  
  public void setHeight(int height) {
    this.height = height;
  }
  
  public void setFps(int fps) {
    this.fps = fps;
  }
  
  public void setServer(String server) {
    this.server = server;
  }
  
  public void setPort(String port) {
    this.port = port;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof CameraPusherConfig))
      return false; 
    CameraPusherConfig other = (CameraPusherConfig)o;
    if (!other.canEqual(this))
      return false; 
    if (getCameraindex() != other.getCameraindex())
      return false; 
    if (getWidth() != other.getWidth())
      return false; 
    if (getHeight() != other.getHeight())
      return false; 
    if (getFps() != other.getFps())
      return false; 
    Object this$server = getServer(), other$server = other.getServer();
    if ((this$server == null) ? (other$server != null) : !this$server.equals(other$server))
      return false; 
    Object this$port = getPort(), other$port = other.getPort();
    if ((this$port == null) ? (other$port != null) : !this$port.equals(other$port))
      return false; 
    Object this$password = getPassword(), other$password = other.getPassword();
    return !((this$password == null) ? (other$password != null) : !this$password.equals(other$password));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof CameraPusherConfig;
  }
  
  public int hashCode() {
    int PRIME = 59;
    int result = 1;
    result = result * 59 + getCameraindex();
    result = result * 59 + getWidth();
    result = result * 59 + getHeight();
    result = result * 59 + getFps();
    Object $server = getServer();
    result = result * 59 + (($server == null) ? 43 : $server.hashCode());
    Object $port = getPort();
    result = result * 59 + (($port == null) ? 43 : $port.hashCode());
    Object $password = getPassword();
    return result * 59 + (($password == null) ? 43 : $password.hashCode());
  }
  
  public String toString() {
    return "CameraPusherConfig(cameraindex=" + getCameraindex() + ", width=" + getWidth() + ", height=" + getHeight() + ", fps=" + getFps() + ", server=" + getServer() + ", port=" + getPort() + ", password=" + getPassword() + ")";
  }
  
  public static CameraPusherConfig INSTANCE = null;
  
  int cameraindex;
  
  int width;
  
  int height;
  
  int fps;
  
  String server;
  
  String port;
  
  String password;
  
  public int getCameraindex() {
    return this.cameraindex;
  }
  
  public int getWidth() {
    return this.width;
  }
  
  public int getHeight() {
    return this.height;
  }
  
  public int getFps() {
    return this.fps;
  }
  
  public String getServer() {
    return this.server;
  }
  
  public String getPort() {
    return this.port;
  }
  
  public String getPassword() {
    return this.password;
  }
}
