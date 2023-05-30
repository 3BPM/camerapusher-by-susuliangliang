package com.susu.camerapusher.av;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipUtils {
  private static final String UTF_8 = "UTF-8";
  
  public static final byte[] compress(String data) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    GZIPOutputStream gzipOutputtStream = new GZIPOutputStream(out);
    try {
      gzipOutputtStream.write(data.getBytes("UTF-8"));
    } finally {
      closeQuietly(gzipOutputtStream);
    } 
    return out.toByteArray();
  }
  
  public static final byte[] compress(byte[] data) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    GZIPOutputStream gzipOutputtStream = new GZIPOutputStream(out);
    try {
      gzipOutputtStream.write(data);
    } finally {
      closeQuietly(gzipOutputtStream);
    } 
    return out.toByteArray();
  }
  
  public static final byte[] decompress(byte[] data) throws IOException {
    ByteArrayOutputStream buffer = null;
    GZIPInputStream gizpInputStream = null;
    try {
      buffer = new ByteArrayOutputStream();
      gizpInputStream = new GZIPInputStream(new ByteArrayInputStream(data));
      int n = -1;
      byte[] _buffer = new byte[12288];
      while (-1 != (n = gizpInputStream.read(_buffer)))
        buffer.write(_buffer, 0, n); 
      return buffer.toByteArray();
    } finally {
      closeQuietly(gizpInputStream);
      closeQuietly(buffer);
    } 
  }
  
  public static final byte[] decompress(InputStream in) throws IOException {
    ByteArrayOutputStream buffer = null;
    GZIPInputStream gizpInputStream = null;
    try {
      buffer = new ByteArrayOutputStream();
      gizpInputStream = new GZIPInputStream(in);
      int n = -1;
      byte[] _buffer = new byte[12288];
      while (-1 != (n = gizpInputStream.read(_buffer)))
        buffer.write(_buffer, 0, n); 
      return buffer.toByteArray();
    } finally {
      closeQuietly(gizpInputStream);
      closeQuietly(buffer);
    } 
  }
  
  private static void closeQuietly(Closeable closeable) {
    try {
      if (closeable != null)
        closeable.close(); 
    } catch (IOException iOException) {}
  }
}
