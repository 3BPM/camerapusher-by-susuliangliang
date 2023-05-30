package com.susu.camerapusher;

import com.susu.camerapusher.av.AudioService;
import com.susu.camerapusher.av.VideoServer;

public class Main {
  public static void main(String[] args) {
    try {
      Utils.readConfig();
      VideoServer.start();
      AudioService.startRecognize();
      AudioService.play();
      MainServer.startServer();
      NatxUtils.initNatx();
      SerialUtils.init("ttyUSB0");
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
}
