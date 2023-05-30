package com.susu.camerapusher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
  public static final String regexCIp = "^192\\.168\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)$";
  
  public static final String regexAIp = "^10\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)$";
  
  public static final String regexBIp = "^172\\.(1[6-9]|2\\d|3[0-1])\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)$";
  
  public static String getHostIp() {
    Pattern ip = Pattern.compile("(^10\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)$)|(^172\\.(1[6-9]|2\\d|3[0-1])\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)$)|(^192\\.168\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)\\.(\\d{1}|[1-9]\\d|1\\d{2}|2[0-4]\\d|25\\d)$)");
    Enumeration<NetworkInterface> networkInterfaces = null;
    try {
      networkInterfaces = NetworkInterface.getNetworkInterfaces();
    } catch (SocketException e) {
      e.printStackTrace();
    } 
    while (networkInterfaces.hasMoreElements()) {
      NetworkInterface networkInterface = networkInterfaces.nextElement();
      Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
      while (inetAddresses.hasMoreElements()) {
        InetAddress address = inetAddresses.nextElement();
        String hostAddress = address.getHostAddress();
        Matcher matcher = ip.matcher(hostAddress);
        if (matcher.matches()) {
          String hostIp = hostAddress;
          return hostIp;
        } 
      } 
    } 
    return null;
  }
  
  private String toHexString(byte[] arg, int length) {
    String result = new String();
    if (arg != null) {
      for (int i = 0; i < length; i++)
        result = result + ((Integer.toHexString((arg[i] < 0) ? (arg[i] + 256) : arg[i]).length() == 1) ? ("0" + Integer.toHexString((arg[i] < 0) ? (arg[i] + 256) : arg[i])) : Integer.toHexString((arg[i] < 0) ? (arg[i] + 256) : arg[i])) + " "; 
      return result;
    } 
    return "";
  }
  
  public static String getTextCenter(String text, String begin, String end) {
    try {
      int b = text.indexOf(begin) + begin.length();
      int e = text.indexOf(end, b);
      return text.substring(b, e);
    } catch (Exception e) {
      return "";
    } 
  }
  
  private byte[] toByteArray(String arg) {
    if (arg != null) {
      char[] NewArray = new char[1000];
      char[] array = arg.toCharArray();
      int length = 0;
      for (int i = 0; i < array.length; i++) {
        if (array[i] != ' ') {
          NewArray[length] = array[i];
          length++;
        } 
      } 
      int EvenLength = (length % 2 == 0) ? length : (length + 1);
      if (EvenLength != 0) {
        int[] data = new int[EvenLength];
        data[EvenLength - 1] = 0;
        for (int j = 0; j < length; j++) {
          if (NewArray[j] >= '0' && NewArray[j] <= '9') {
            data[j] = NewArray[j] - 48;
          } else if (NewArray[j] >= 'a' && NewArray[j] <= 'f') {
            data[j] = NewArray[j] - 97 + 10;
          } else if (NewArray[j] >= 'A' && NewArray[j] <= 'F') {
            data[j] = NewArray[j] - 65 + 10;
          } 
        } 
        byte[] byteArray = new byte[EvenLength / 2];
        for (int k = 0; k < EvenLength / 2; k++)
          byteArray[k] = (byte)(data[k * 2] * 16 + data[k * 2 + 1]); 
        return byteArray;
      } 
    } 
    return new byte[0];
  }
  
  private byte[] toByteArray2(String arg) {
    if (arg != null) {
      char[] NewArray = new char[1000];
      char[] array = arg.toCharArray();
      int length = 0;
      for (int i = 0; i < array.length; i++) {
        if (array[i] != ' ') {
          NewArray[length] = array[i];
          length++;
        } 
      } 
      byte[] byteArray = new byte[length];
      for (int j = 0; j < length; j++)
        byteArray[j] = (byte)NewArray[j]; 
      return byteArray;
    } 
    return new byte[0];
  }
  
  public static void readConfig() {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    try {
      File file = new File("./camerapusher.yaml");
      CameraPusherConfig cfg = mapper.<CameraPusherConfig>readValue(file, CameraPusherConfig.class);
      CameraPusherConfig.INSTANCE = cfg;
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static String getDate() {
    Date date = new Date(System.currentTimeMillis());
    return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(date);
  }
}
