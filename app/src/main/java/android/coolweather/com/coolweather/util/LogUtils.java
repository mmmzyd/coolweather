package android.coolweather.com.coolweather.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 日志工具类
 *
 * @author 272388 | yanling002@deppon.com
 * @date 2017-07-10
 */

public class LogUtils {

    //定义日志的级别
    private final static int LEVEL_VERBOSE = 0;
    private final static int LEVEL_DEBUG = 1;
    private final static int LEVEL_INFO = 2;
    private final static int LEVEL_WARN = 3;
    private final static int LEVEL_ERROR = 4;

    //定义日志输出的的目标(0: 输出到终端；1：输出到文件；2：输出到终端和文件)
    public final static int LOG_TO_CONSOLE = 0;
    public final static int LOG_TO_FILE = 1;
    public final static int LOG_TO_ALL = 2;

    //定义日志文件命名格式
    private final static SimpleDateFormat LOG_FILENAME_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    //定义日志内容时间标注格式
    private final static SimpleDateFormat LOG_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //定义日志存储目录(默认在自带存储卡的log目录下)
    private static String logDir = Environment.getExternalStorageDirectory() + File.separator + "log";

    //定义变量保存是否输出日志，可用于快速的开启/关闭日志
    private static boolean isLog = true;

    private static ExecutorService singleExecutor = Executors.newSingleThreadExecutor();


    /**
     * 对外日志接口
     */

    /**
     * 日志工具初始化
     * @param logDir，日志保存的目录
     * @param saveDays，日志保存的天数
     */
    public static void init(String logDir, int saveDays){
        //设置当前日志的保存目录
        LogUtils.logDir = logDir;
        if (logDir != null && !"".equals(logDir)){
            //清除过期的日志
            //clearLog(saveDays);
        }
    }

    /**
     * 设置是否输出日志
     * @param isLog，true:表示输出；false:标示关闭输出
     */
    public static void setIsLog(boolean isLog){
        LogUtils.isLog = isLog;
    }


    /**
     * 输出日志级别为Verbose的到控制台
     * @param tag，日志标签
     * @param message，日志消息实体
     */
    public static void v(String tag, String message){
        log(LEVEL_VERBOSE, tag, message, LOG_TO_CONSOLE);
    }

    /**
     * 输出日志级别为Verbose的到控制台或文件（输出到文件的会默认输出到控制台）
     * @param tag，日志标签
     * @param message，日志信息实体
     * @param logTo，标志变量表示日志输出，主要是3种情况，LOG_TO_CONSOLE, LOG_TO_FILE, LOG_TO_ALL
     */
    public static void v(String tag, String message, int logTo){
        log(LEVEL_VERBOSE, tag, message, logTo);
    }


    public static void d(String tag, String message){
        log(LEVEL_DEBUG, tag, message, LOG_TO_CONSOLE);
    }

    public static void d(String tag, String message, int logTo){
        log(LEVEL_DEBUG, tag, message, logTo);
    }


    public static void i(String tag, String message){
        log(LEVEL_INFO, tag, message, LOG_TO_CONSOLE);
    }

    public static void i(String tag, String message, int logTo){
        log(LEVEL_INFO, tag, message, logTo);
    }


    public static void w(String tag, String message){
        log(LEVEL_WARN, tag, message, LOG_TO_CONSOLE);
    }

    public static void w(String tag, String message, int logTo){
        log(LEVEL_WARN, tag, message, logTo);
    }


    public static void e(String tag, String message){
        log(LEVEL_ERROR, tag, message, LOG_TO_CONSOLE);
    }

    public static void e(String tag, String message, int logTo){
        log(LEVEL_ERROR, tag, message, logTo);
    }


    /**
     * 输出日志到控制台，文件
     * @param level，日志级别
     * @param tag，日志标签
     * @param message，日志实体信息
     * @param logTo，日志输出目标
     */
    private static void log(int level, final String tag, final String message, int logTo){

        //判断是否输出日志
        if (!isLog){
            //进行日志输出,直接退出
            return;
        }

        //定义变量保存日志类别用于保存文件
        String levelType = "";
        //判断日志类别
        switch (level){
            case LEVEL_VERBOSE:
                //判断是否需要向控制台输出
                if (logTo == LogUtils.LOG_TO_CONSOLE || logTo == LogUtils.LOG_TO_ALL){
                    Log.v(tag, message);
                }
                levelType = "Verbose";
                break;
            case LEVEL_DEBUG:
                //判断是否需要向控制台输出
                if (logTo == LogUtils.LOG_TO_CONSOLE || logTo == LogUtils.LOG_TO_ALL){
                    Log.d(tag, message);
                }
                levelType = "Debug";
                break;
            case LEVEL_INFO:
                //判断是否需要向控制台输出
                if (logTo == LogUtils.LOG_TO_CONSOLE || logTo == LogUtils.LOG_TO_ALL){
                    Log.i(tag, message);
                }
                levelType = "Info";
                break;
            case LEVEL_WARN:
                //判断是否需要向控制台输出
                if (logTo == LogUtils.LOG_TO_CONSOLE || logTo == LogUtils.LOG_TO_ALL){
                    Log.w(tag, message);
                }
                levelType = "Warn";
                break;
            case LEVEL_ERROR:
                //判断是否需要向控制台输出
                if (logTo == LogUtils.LOG_TO_CONSOLE || logTo == LogUtils.LOG_TO_ALL){
                    Log.e(tag, message);
                }
                levelType = "Error";
                break;
        }
        //输出到文件
        if (logTo == LOG_TO_FILE || logTo == LOG_TO_ALL){
            final String tmp_levelType = levelType;
            synchronized (LogUtils.class){
                if (singleExecutor == null){
                    singleExecutor = Executors.newSingleThreadExecutor();
                }
                //通过线程池写入到文件
                singleExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        //writeToFile(tmp_levelType, tag, message);
                        //写入加密的日志
                        //writeToFileEncrypt(tmp_levelType, tag, message);
                    }
                });
            }
        }
    }

    /**
     * 将日志按照一定的格式写入文件
     * @param levelType，日志级别
     * @param tag，日志标签
     * @param message， 日志实体消息
     */
    private static void writeToFile(String levelType, String tag, String message){

        //首先判断日志目录是否存在
        File dir = new File(logDir);
        if (!dir.exists()){
            dir.mkdirs();
        }
        //写入文件
        FileWriter fw = null;
        String logFileName = LOG_FILENAME_FORMAT.format(new Date()) + ".dplg";
        try {
            fw = new FileWriter(dir.getPath() + File.separator + logFileName, true);
            //先添加时间标签
            fw.append(LOG_TIME_FORMAT.format(new Date()) + "  ");
            //再添加日志级别
            fw.append(levelType + "  ");
            //再添加日志标签
            fw.append(tag + "  ");
            //然后添加日志内容
            fw.append(message);
            //最后换行
            fw.append("\r\n");

            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭输出流
            if (fw != null){
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
