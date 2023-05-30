package com.susu.camerapusher.wss;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
  private static final Logger log = LoggerFactory.getLogger(Client.class);
  
  private String ip;
  
  private int port;
  
  private Socket socket;
  
  private DataOutputStream dataOutputStream;
  
  private DataInputStream dataInputStream;
  
  private Date useDate;
  
  public Client(String ip, int port) {
    this.ip = ip;
    this.port = port;
    this.useDate = new Date();
  }
  
  public void connect() throws Exception {
    log.debug("Entry Method:connect()");
    try {
      close();
      this.socket = new Socket();
      SocketAddress socketAddress = new InetSocketAddress(this.ip, this.port);
      this.socket.connect(socketAddress, 1000);
      this.dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
      this.dataInputStream = new DataInputStream(this.socket.getInputStream());
      updateUseDate();
    } catch (Exception e) {
      this.socket = null;
      log.error("socket connect error ip:" + this.ip + ",port:" + this.port + ",Exception:" + e.getMessage());
      throw new Exception("socket connect error ip:" + this.ip + ",port:" + this.port + ",Exception:" + e
          .getMessage());
    } 
    log.debug("Exit Method:connect()");
  }
  
  public void write(byte[] msg, int len) throws IOException {
    log.trace("dataOutputStream.write");
    this.dataOutputStream.write(msg, 0, len);
    log.trace("dataOutputStream.flush");
    this.dataOutputStream.flush();
    updateUseDate();
  }
  
  public byte[] read(int bufferSize, int timeOut) throws IOException {
    this.socket.setSoTimeout(timeOut * 1000);
    byte[] bytes = new byte[bufferSize];
    log.trace("dataInputStream.read");
    int len = this.dataInputStream.read(bytes);
    updateUseDate();
    log.debug("readLen:" + len);
    byte[] tempBytes = null;
    if (len > 0) {
      tempBytes = new byte[len];
      System.arraycopy(bytes, 0, tempBytes, 0, len);
    } 
    return tempBytes;
  }
  
  public void close() {
    log.debug("Entry Method:close()");
    try {
      if (null != this.dataOutputStream)
        this.dataOutputStream.close(); 
      if (null != this.dataInputStream)
        this.dataInputStream.close(); 
      if (null != this.socket && !this.socket.isClosed())
        this.socket.close(); 
      this.socket = null;
    } catch (IOException e) {
      log.error("SocketClient close Exception:" + e.getMessage());
    } 
    log.debug("Exit Method:close()");
  }
  
  public boolean valid() throws Exception {
    if (null == this.socket || this.socket.isClosed() || this.socket
      .isInputShutdown() || this.socket.isOutputShutdown()) {
      if (this.dataInputStream != null)
        this.dataInputStream.close(); 
      if (this.dataOutputStream != null)
        this.dataOutputStream.close(); 
      if (this.socket != null)
        this.socket.close(); 
      return false;
    } 
    return true;
  }
  
  public long getTimePass() {
    log.trace("Entry Method:getTimePass(),useDate:{}", Long.valueOf(this.useDate.getTime()));
    Date date = new Date();
    log.debug("Exit Method:getTimePass(),timePass:{}", Long.valueOf(date.getTime() - this.useDate.getTime()));
    return date.getTime() - this.useDate.getTime();
  }
  
  public void updateUseDate() {
    this.useDate = new Date();
  }
  
  public static void main(String[] args) {
    System.out.println("SocketClient main start");
    try {
      System.out.println("----------try start----------");
      Client socketClient = new Client("localhost", 8080);
      socketClient.connect();
      String strInput = "hello server !";
      socketClient.write(strInput.getBytes(), strInput.length());
      byte[] recv = socketClient.read(1024, 10);
      String strOriginal = null;
      if (null != recv)
        strOriginal = new String(recv, StandardCharsets.ISO_8859_1); 
      log.info("strOriginal:" + strOriginal);
      System.out.println("----------try end----------");
    } catch (Exception e) {
      System.out.println("catch error:" + e.getMessage());
      e.printStackTrace();
    } 
    System.out.println("SocketClient main end");
  }
}
