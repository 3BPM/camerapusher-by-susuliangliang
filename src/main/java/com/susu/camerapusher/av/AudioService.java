package com.susu.camerapusher.av;

import com.susu.camerapusher.SessionManager;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioService {
  public static AudioFormat audioFormat;
  
  public static TargetDataLine targetDataLine;
  
  public static boolean flag = true;
  
  public static List<byte[]> audiobyte = (List)new ArrayList<>();
  
  public static SourceDataLine getSourceDataLine(AudioFormat format) {
    SourceDataLine sdl = null;
    try {
      DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
      sdl = (SourceDataLine)AudioSystem.getLine(info);
      System.out.println("speaker: " + sdl.getLineInfo().toString());
    } catch (Exception e) {
      e.printStackTrace();
    } 
    return sdl;
  }
  
  public static TargetDataLine getTargetLine(AudioFormat format) {
    TargetDataLine tdl = null;
    try {
      DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
      tdl = (TargetDataLine)AudioSystem.getLine(info);
      System.out.println("mic: " + tdl.getLineInfo().toString());
    } catch (Exception e) {
      e.printStackTrace();
    } 
    return tdl;
  }
  
  public static void play() {
    try {
      AudioFormat format = new AudioFormat(44100.0F, 16, 1, true, false);
      final SourceDataLine sdl = getSourceDataLine(format);
      sdl.open(format);
      sdl.start();
      (new Thread(new Runnable() {
            public void run() {
              try {
                boolean isPlay = false;
                while (true) {
                  if (AudioService.audiobyte.size() != 0) {
                    if (AudioService.audiobyte.size() >= 5)
                      isPlay = true; 
                    if (isPlay) {
                      byte[] data = AudioService.audiobyte.get(0);
                      if (data != null) {
                        sdl.write(data, 5, data.length - 5);
                        AudioService.audiobyte.remove(data);
                      } 
                    } 
                  } else {
                    isPlay = false;
                  } 
                  Thread.sleep(1L);
                } 
              } catch (Exception e) {
                e.printStackTrace();
                return;
              } 
            }
          })).start();
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public static void stopRecognize() {
    flag = false;
    targetDataLine.stop();
    targetDataLine.close();
  }
  
  public static void startRecognize() {
    try {
      flag = true;
      (new Thread(new Runnable() {
            public void run() {
              try {
                AudioService.audioFormat = new AudioFormat(44100.0F, 16, 1, true, false);
                AudioService.targetDataLine = AudioService.getTargetLine(AudioService.audioFormat);
                AudioService.targetDataLine.open(AudioService.audioFormat);
                AudioService.targetDataLine.start();
                byte[] fragment = new byte[1024];
                while (AudioService.flag) {
                  AudioService.targetDataLine.read(fragment, 0, fragment.length);
                  byte[] audioData = GZipUtils.compress(AudioService.byteMerger("audio".getBytes(), fragment));
                  SessionManager.getInstance().writeToServer(audioData);
                } 
              } catch (Exception e) {
                e.printStackTrace();
              } 
            }
          })).start();
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public static byte[] byteMerger(byte[] bt1, byte[] bt2) {
    byte[] bt3 = new byte[bt1.length + bt2.length];
    System.arraycopy(bt1, 0, bt3, 0, bt1.length);
    System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
    return bt3;
  }
  
  public static void main(String[] args) {}
}
