package com.susu.camerapusher;

import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerialUtils {
  private static final Logger log = LoggerFactory.getLogger(SerialUtils.class);
  
  public static SerialPort mSerialPort;
  
  public static SerialUtils serialUtils;
  
  public SerialUtils(String portName) {
    mSerialPort = SerialPort.getCommPort(portName);
    mSerialPort.setFlowControl(0);
    mSerialPort.setComPortParameters(115200, 8, 1, 0);
    mSerialPort.setComPortTimeouts(272, 1000, 1000);
    boolean isopen = mSerialPort.openPort();
    System.out.println("serialOpenStatus:" + isopen);
  }
  
  public static void init(String portName) {
    if (serialUtils == null) {
      serialUtils = new SerialUtils(portName);
      startRead();
    } 
  }
  
  public static void close() {
    if (mSerialPort != null && mSerialPort.isOpen()) {
      mSerialPort.closePort();
      mSerialPort = null;
    } 
  }
  
  public static int write(byte[] data) {
    log.info("ready to send data");
    if (mSerialPort == null || !mSerialPort.isOpen()) {
      log.info("send failed");
      return 0;
    } 
    int result = mSerialPort.writeBytes(data, data.length);
    log.info("send success result:" + result);
    return result;
  }
  
  public static int read(byte[] data) {
    if (mSerialPort == null || !mSerialPort.isOpen())
      return 0; 
    return mSerialPort.readBytes(data, data.length);
  }
  
  public static void startRead() {
    (new Thread(new Runnable() {
          public void run() {
            try {
              while (true) {
                if (SerialUtils.mSerialPort.bytesAvailable() > 0) {
                  byte[] readBuffer = new byte[SerialUtils.mSerialPort.bytesAvailable()];
                  int numRead = SerialUtils.mSerialPort.readBytes(readBuffer, readBuffer.length);
                  System.out.println("Read:" + new String(readBuffer));
                  SessionManager.getInstance().writeToServer(new String(readBuffer));
                } 
                Thread.sleep(200L);
              } 
            } catch (Exception e) {
              e.printStackTrace();
              return;
            } 
          }
        })).start();
  }
}
