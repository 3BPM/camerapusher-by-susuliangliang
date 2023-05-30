package com.susu.camerapusher.av;

import com.susu.camerapusher.CameraPusherConfig;
import com.susu.camerapusher.SessionManager;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;

public class VideoServer {
  private static OpenCVFrameConverter.ToIplImage openCVConverter = new OpenCVFrameConverter.ToIplImage();
  
  public static FrameGrabber frameGrabber;
  
  public static boolean isrun = false;
  
  public static void start() {
    (new Thread(new Runnable() {
          public void run() {
            VideoServer.fetchALLFrame();
          }
        })).start();
  }
  
  public static void stop() {
    if (frameGrabber != null)
      try {
        isrun = false;
        frameGrabber.stop();
      } catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
        e.printStackTrace();
      }  
  }
  
  public static void fetchALLFrame() {
    try {
      isrun = true;
      int cameraIndex = CameraPusherConfig.INSTANCE.getCameraindex();
      int width = CameraPusherConfig.INSTANCE.getWidth();
      int height = CameraPusherConfig.INSTANCE.getHeight();
      int fps = CameraPusherConfig.INSTANCE.getFps();
      frameGrabber = new OpenCVFrameGrabber(cameraIndex);
      frameGrabber.setImageWidth(width);
      frameGrabber.setImageHeight(height);
      Java2DFrameConverter converter = new Java2DFrameConverter();
      frameGrabber.start();
      int i = 0;
      Frame frame = null;
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Point point = new Point(15, 35);
      while (isrun) {
        frame = frameGrabber.grabFrame();
        if (frame.image != null) {
          Mat mat = openCVConverter.convertToMat(frame);
          opencv_imgproc.putText(mat, simpleDateFormat
              .format(new Date()), point, 0, 0.8D, new Scalar(0.0D, 200.0D, 255.0D, 0.0D), 1, 0, false);
          BufferedImage bufferedImage = converter.convert(openCVConverter.convert(mat));
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          ImageIO.write(bufferedImage, "jpg", out);
          byte[] videoData = GZipUtils.compress(byteMerger("video".getBytes(), out.toByteArray()));
          SessionManager.getInstance().writeToServer(videoData);
        } 
        i++;
        Thread.sleep(getInterval(fps));
      } 
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
  
  public static byte[] imageToBytes(BufferedImage bImage) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      ImageIO.write(bImage, "jpg", out);
    } catch (IOException e) {
      e.printStackTrace();
    } 
    return out.toByteArray();
  }
  
  public static int getInterval(int frameRate) {
    return 1000 / frameRate;
  }
}
