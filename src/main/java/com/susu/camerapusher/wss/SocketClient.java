package com.susu.camerapusher.wss;

import io.netty.util.internal.StringUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

public class SocketClient extends WebSocketClient {
  public static WebSocketClient webSocketClient;
  
  public static String initData;
  
  public static boolean start = false;
  
  public static Client socketClient;
  
  public static void main(String[] args) throws URISyntaxException {
    webSocketClient = new SocketClient(new URI("wss://chatwss109074.kugou.com/acksocket"));
    webSocketClient.connect();
    System.out.println(UUID.randomUUID().toString());
  }
  
  public static void start(String url) {
    try {
      String roomid = url;
      String requrl = "https://fx2.service.kugou.com/socket_scheduler/pc/binary/v1/address.jsonp?_p=0&_v=7.0.0&pv=20211201&rid=" + roomid + "&cid=100&at=102&_=" + System.currentTimeMillis();
      String response = HttpClientUtil.doGet(requrl);
      JSONObject jsonObject = new JSONObject(response);
      JSONObject data = jsonObject.getJSONObject("data");
      JSONArray addrs = data.getJSONArray("addrs");
      String host = addrs.getJSONObject(1).getString("host");
      String token = data.getString("soctoken");
      String deviceNo = UUID.randomUUID().toString();
      String sid = UUID.randomUUID().toString();
      initData = "{\"cmd\":201,\"roomid\":" + roomid + ",\"kugouid\":0,\"token\":\"token\",\"appid\":1010,\"referer\":0,\"clientid\":100,\"v\":20211201,\"soctoken\":\"" + token + "\",\"sid\":\"" + sid + "\",\"socsid\":\"\",\"deviceNo\":\"" + deviceNo + "\",\"screen\":0,\"platid\":7}";
      String wss = "wss://" + host;
      webSocketClient = new SocketClient(new URI("wss://chatwss121085.kugou.com/acksocket"));
      webSocketClient.connectBlocking();
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public SocketClient(URI serverUri) {
    super(serverUri);
  }
  
  public void onOpen(ServerHandshake handshakedata) {
    System.out.println("opened connection");
    start = true;
    webSocketClient.send(initData);
    (new Thread(new Runnable() {
          public void run() {
            try {
              SocketClient.socketClient = new Client("127.0.0.1", 25565);
              SocketClient.socketClient.connect();
              while (SocketClient.start) {
                Thread.sleep(1000L);
                SocketClient.webSocketClient.sendPing();
              } 
            } catch (Exception e) {
              e.printStackTrace();
            } 
          }
        })).start();
  }
  
  public void onMessage(String message) {
    try {
      if (message.startsWith("{")) {
        JSONObject jsonObject = new JSONObject(message);
        int cmd = jsonObject.getInt("cmd");
        String msg = "", name = "";
        if (cmd == 201) {
          String wellcome = jsonObject.getJSONObject("content").getJSONObject("content").getString("wellcomes");
          name = jsonObject.getJSONObject("content").getJSONObject("content").getString("nickname");
          wellcome = wellcome.replace("%nick", name);
          msg = wellcome;
        } 
        if (cmd == 501) {
          String chatmsg = jsonObject.getJSONObject("content").getJSONObject("content").getString("chatmsg");
          name = jsonObject.getJSONObject("content").getJSONObject("content").getString("sendername");
          msg = chatmsg;
        } 
        if (!StringUtil.isNullOrEmpty(msg)) {
          System.out.println(msg);
          if (msg.equals("加入")) {
            String join = "join#" + name;
            socketClient.write(join.getBytes(StandardCharsets.UTF_8), join.length());
          } else {
            String send = "msg#id@" + name + "@0" + msg;
            System.out.println(send);
            socketClient.write(send.getBytes(StandardCharsets.UTF_8), send.length());
          } 
        } 
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public static void stop() {
    if (webSocketClient != null && webSocketClient.isOpen()) {
      webSocketClient.close();
      start = false;
    } 
  }
  
  public void onClose(int code, String reason, boolean remote) {
    start = false;
    System.out.println("Connection closed by " + (remote ? "remote peer" : "us") + " reason: " + reason + " code: " + code);
  }
  
  public void onError(Exception ex) {
    start = false;
    ex.printStackTrace();
  }
  
  public void onWebsocketPing(WebSocket conn, Framedata f) {
    super.onWebsocketPing(conn, f);
  }
  
  public void onWebsocketPong(WebSocket conn, Framedata f) {
    webSocketClient.send("H");
    super.onWebsocketPong(conn, f);
  }
}
