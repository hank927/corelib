package com.hank.corelib.logger;

import android.text.TextUtils;
import android.util.Log;

import com.hank.corelib.util.Check;
import com.hank.corelib.util.FileUtil;
import com.hank.corelib.util.TimeUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;

class AndroidLogAdapter implements LogAdapter {

  private String LOG_DIR ;
  private static final String VERBOSE_TAG = "VERBOSE";
  private static final String DEBUG_TAG = "DEBUG";
  private static final String WARNING_TAG = "WARNIG";
  private static final String ERORR_TAG = "ERROR";
  private static final String INFO_TAG = "INFO";

  private String LOG_CURR_FILENAME = "";

  private static final long MAX_LOGFILE_SIZE = 10 * 1024 * 1024;

  private BufferedWriter mOutWriter = null;

  private long mCurrFileSize = 0;

  private boolean isSaveToSdcard;

  @Override public void d(String tag, String message) {
    Log.d(tag, message);
    if(isSaveToSdcard){
      writeLog(DEBUG_TAG,tag,message,null);
    }
  }

  @Override public void e(String tag, String message) {
    Log.e(tag, message);
    if(isSaveToSdcard){
      writeLog(ERORR_TAG,tag,message,null);
    }
  }

  @Override public void w(String tag, String message) {
    Log.w(tag, message);
    if(isSaveToSdcard){
      writeLog(WARNING_TAG,tag,message,null);
    }
  }

  @Override public void i(String tag, String message) {
    Log.i(tag, message);
    if(isSaveToSdcard){
      writeLog(INFO_TAG,tag,message,null);
    }
  }

  @Override public void v(String tag, String message) {
    Log.v(tag, message);
    if(isSaveToSdcard){
      writeLog(VERBOSE_TAG,tag,message,null);
    }
  }

  @Override public void wtf(String tag, String message) {
    Log.wtf(tag, message);
    if(isSaveToSdcard){
      writeLog(WARNING_TAG,tag,message,null);
    }
  }

  private synchronized void writeLog(String level, String tag, String msg,
                                     Throwable tr) {
    if (null == mOutWriter) {
      boolean success = openFile();
      if (!success) {
        return;
      }
    }
    StringBuilder log = composeLog(level, tag, msg, tr);
    mCurrFileSize += log.length();

    try {
      mOutWriter.write(log.toString());
      mOutWriter.flush();
      mOutWriter.newLine();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NullPointerException e){
      e.printStackTrace();
    }finally {
    }

    if (mCurrFileSize > MAX_LOGFILE_SIZE) {
      switchToNewLog();
    }
  }

  public void close() {
    if (null != mOutWriter) {
      try {
        mOutWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    mOutWriter = null;
  }

  private StringBuilder composeLog(String level, String tag, String msg,
                                   Throwable tr) {
    StringBuilder log = new StringBuilder(512);

    log.append(TimeUtils.getCurTimeString());
    log.append(" ");
    log.append(level + "/" + tag);
    log.append("\t");
    if (!TextUtils.isEmpty(msg)) {
      log.append(msg);
      if (null != tr) {
        log.append("\n\t");
      }
    }
    log.append(Log.getStackTraceString(tr));

    return log;
  }
  /**
   * @return 返回要上传的文件名
   */
  public String switchToNewLog() {
    close();

    mCurrFileSize = 0;

    LOG_CURR_FILENAME = composeDebugName() + ".log";
    openFile();

    return this.LOG_CURR_FILENAME;
  }

  private String composeDebugName(){
    return TimeUtils.getCurTimeString(new SimpleDateFormat("yyyy-MM-dd"));
  }
  private boolean openFile() {
    Check.checkEmpty(LOG_DIR, "the log dir must be Initialized first");
    if(TextUtils.isEmpty(LOG_CURR_FILENAME)){
      LOG_CURR_FILENAME = ""+composeDebugName() + ".log";
    }
    File f = new File(LOG_DIR);

    boolean logDirExists = false;
    if (!f.exists()) {
      logDirExists = f.mkdirs();
    } else {
      logDirExists = true;
    }

    f = new File(LOG_DIR, "log");
    if (!f.exists()) {
      logDirExists = f.mkdirs();
    } else {
      logDirExists = true;
    }

    if (!logDirExists) {
      return false;
    }

    try {
      f = new File(LOG_DIR+"/log", LOG_CURR_FILENAME);
      Log.i("DebugLog","log:"+f.getAbsolutePath()+"--"+f.length());
      if(f.exists() && f.length()>MAX_LOGFILE_SIZE){
        Log.i("DebugLog","f delete"+f.getAbsolutePath()+"--"+f.length());
        f.delete();
      }
      if(!f.exists())
        f.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
    if(mCurrFileSize<=0)
      mCurrFileSize = f.length();
    mOutWriter = null;
    try {
      mOutWriter = new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream(f, true)));
      return true;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public void saveToFile(boolean isSave, String path) {
    isSaveToSdcard = isSave;
    LOG_DIR = path;
  }

}
