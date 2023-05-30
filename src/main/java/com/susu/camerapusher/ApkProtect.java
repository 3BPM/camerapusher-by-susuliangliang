package com.susu.camerapusher;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApkProtect {
  private static final Logger log = LoggerFactory.getLogger(ApkProtect.class);
  
  public static String basePath = "/Users/suxiaoliang/Android/SDK/build-tools/30.0.3/";
  
  public static void signApk(String signApkPath, String keystorePath, String unsignApkPath, String name) {
    try {
      log.info(name + "----开始apk签名");
      Process process = Runtime.getRuntime().exec(basePath + "apksigner sign --ks " + keystorePath + " --ks-key-alias car --ks-pass pass:sxl19931205 --key-pass pass:sxl19931205 --v1-signing-enabled true --v2-signing-enabled true --out " + signApkPath + " " + unsignApkPath);
      process.waitFor(10L, TimeUnit.SECONDS);
      if (process.exitValue() != 0) {
        log.info(name + "----apk签名失败");
      } else {
        log.info(name + "----apk签名成功");
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public static void leguProtect(String signApkPath, String outApkPath, String name) {
    try {
      log.info(name + "----开始apk加固");
      String id = "AKIDujXQ9htaMyPjsqoGiz9EAvWwCZn3uC3j";
      String key = "fx771mNvIWaTIwQjicNxX0IQPEl9NQIz";
      Process process = Runtime.getRuntime().exec("java -Dfile.encoding=utf-8 -jar " + basePath + "ms-shield.jar -sid " + id + " -skey " + key + " -uploadPath " + signApkPath + " -downloadPath " + outApkPath);
      process.waitFor();
      if (process.exitValue() != 0) {
        log.info(name + "----apk加固失败");
      } else {
        log.info(name + "----apk加固成功");
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public static void alignUnsignedApk(String unsignedApkPath, String alignedUnsignedApkPath, String name) {
    try {
      log.info(name + "----开始apk对齐");
      Process process = Runtime.getRuntime().exec(basePath + "zipalign -v -p 4 " + unsignedApkPath + " " + alignedUnsignedApkPath);
      process.waitFor(10L, TimeUnit.SECONDS);
      log.info(name + "----apk对齐成功");
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public static void main(String[] args) {
    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
    String[] names = { "佳阳科技" };
    String version = "V5.7.0";
    for (int i = 0; i < names.length; i++)
      fixedThreadPool.execute(new Run(names[i], version)); 
  }
  
  public static class Run implements Runnable {
    public String name;
    
    public String version;
    
    public Run(String name, String version) {
      this.name = name;
      this.version = version;
    }
    
    public void run() {
      ApkProtect.apkProtect(this.name, this.version);
    }
  }
  
  public static void deleteFile(String path) {
    File file = new File(path);
    file.delete();
  }
  
  public static void apkProtect(String name, String version) {
    long start = System.currentTimeMillis() / 1000L;
    String baseDir = "/Users/suxiaoliang/Project/AndroidProject/LivePusher/";
    String signApkPath = baseDir + "app/" + name + "/release/" + name + "Release_" + version + "_sign.apk";
    String unsignApkPath = baseDir + "app/" + name + "/release/" + name + "Release_" + version + ".apk";
    String outApkPath = baseDir + "app/" + name + "/release/";
    String outUnsignApk = outApkPath + name + "Release_" + version + "_sign_legu.apk";
    String unsignAlignApkPath = outApkPath + name + "Release_" + version + "_sign_legu_align.apk";
    String outProjectSignApkPath = baseDir + "apks/" + name + "_sign.apk";
    String keystorePath = baseDir + "app/car.jks";
    signApk(signApkPath, keystorePath, unsignApkPath, name);
    leguProtect(signApkPath, outApkPath, name);
    alignUnsignedApk(outUnsignApk, unsignAlignApkPath, name);
    signApk(outProjectSignApkPath, keystorePath, unsignAlignApkPath, name);
    deleteFile(signApkPath);
    deleteFile(signApkPath + ".idsig");
    deleteFile(outUnsignApk);
    deleteFile(unsignAlignApkPath);
    deleteFile(outProjectSignApkPath + ".idsig");
    long end = System.currentTimeMillis() / 1000L;
    log.info("耗时：" + (end - start) + " 秒");
  }
}
