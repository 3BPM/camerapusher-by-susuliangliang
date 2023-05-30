package com.susu.camerapusher;

import com.susu.camerapusher.av.AudioService;
import com.susu.camerapusher.av.VideoServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageRecvice {
  private static final Logger log = LoggerFactory.getLogger(MessageRecvice.class);
  
  public static void received(Object message) {
    try {
      if (message instanceof String) {
        String msg = message.toString();
        if (msg.substring(1, 2).equals("#") && msg.endsWith("|")) {
          String[] v;
          int width, height;
          String action = msg.substring(0, 1);
          String data = Utils.getTextCenter(msg, "#", "|");
          sendData(msg);
          switch (action) {
            case "F":
              log.info("forward：" + data);
              break;
            case "B":
              log.info("back：" + data);
              break;
            case "L":
              log.info("left：" + data);
              break;
            case "R":
              log.info("right：" + data);
              break;
            case "Z":
              log.info("camera left：" + data);
              break;
            case "Y":
              log.info("camera right：" + data);
              break;
            case "S":
              log.info("stop");
              break;
            case "H":
              log.info("camera reset");
              break;
            case "T":
              log.info("turn center");
              break;
            case "V":
              log.info("video：" + data);
              v = data.split("x");
              width = Integer.parseInt(v[0]);
              height = Integer.parseInt(v[1]);
              CameraPusherConfig.INSTANCE.setWidth(width);
              CameraPusherConfig.INSTANCE.setHeight(height);
              VideoServer.stop();
              Thread.sleep(1000L);
              VideoServer.start();
              break;
            case "C":
              log.info("camera：" + data);
              break;
            case "E":
              log.info("led on：" + data);
              break;
            case "G":
              log.info("led off：" + data);
              break;
            case "A":
              log.info("video off：" + data);
              if (data.equals("0")) {
                VideoServer.stop();
                AudioService.stopRecognize();
                break;
              } 
              if (data.equals("1")) {
                VideoServer.start();
                AudioService.startRecognize();
              } 
              break;
          } 
        } 
      } else if (message instanceof byte[]) {
        byte[] data = (byte[])message;
        data = GZipUtils.decompress(data);
        String action = new String(data, 0, 5);
        if (action.equals("audio"))
          AudioService.audiobyte.add(data); 
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public static void sendData(String data) {
    if (data != null && !data.equals("")) {
      try {
        byte[] to_send = data.getBytes("UTF-8");
        SerialUtils.write(to_send);
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
    } else {
      System.out.println("发送到Arduino的数据为空");
    } 
  }
}
