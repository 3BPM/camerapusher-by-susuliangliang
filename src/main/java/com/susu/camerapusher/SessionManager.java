package com.susu.camerapusher;

import org.apache.mina.core.session.IoSession;

public class SessionManager {
  private static SessionManager instance = null;
  
  private IoSession session;
  
  public static SessionManager getInstance() {
    if (instance == null)
      synchronized (SessionManager.class) {
        if (instance == null)
          instance = new SessionManager(); 
      }  
    return instance;
  }
  
  public void setSession(IoSession session) {
    this.session = session;
  }
  
  public IoSession getSession() {
    return this.session;
  }
  
  public void writeToServer(Object msg) {
    if (this.session != null)
      this.session.write(msg); 
  }
  
  public void closeSession() {
    if (this.session != null)
      this.session.closeOnFlush(); 
  }
  
  public void removeSession() {
    this.session = null;
  }
}
