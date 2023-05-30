package com.susu.camerapusher;

import java.net.InetSocketAddress;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class MainServer {
  public IoAcceptor acceptor;
  
  public static void startServer() {
    try {
      try {
        IoAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
        acceptor.setHandler(new ServerHandler());
        acceptor.getSessionConfig().setReadBufferSize(5120);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60);
        acceptor.bind(new InetSocketAddress(6000));
        System.out.println("NetWork Service Start!");
      } catch (Exception e) {
        e.printStackTrace();
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public static class ServerHandler extends IoHandlerAdapter {
    public void sessionCreated(IoSession session) throws Exception {
      SessionManager.getInstance().setSession(session);
      super.sessionCreated(session);
    }
    
    public void sessionOpened(IoSession session) throws Exception {
      SessionManager.getInstance().setSession(session);
      System.out.println("客户端连接：" + session.getRemoteAddress());
      super.sessionOpened(session);
    }
    
    public void messageReceived(IoSession session, Object message) throws Exception {
      MessageRecvice.received(message);
      super.messageReceived(session, message);
    }
    
    public void messageSent(IoSession session, Object message) throws Exception {
      super.messageSent(session, message);
    }
    
    public void sessionClosed(IoSession session) throws Exception {
      SessionManager.getInstance().removeSession();
      System.out.println("客户端断开：" + session.getRemoteAddress());
      super.sessionClosed(session);
    }
  }
  
  public static void main(String[] args) {
    startServer();
  }
}
