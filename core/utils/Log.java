package utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

    public static void v(String t, String  msg){
        Logger.getLogger(t).log(Level.INFO, msg);
    }

    public static void i(String t, String  msg){
        Logger.getLogger(t).log(Level.INFO, msg);
    }

    public static void d(String t, String  msg){
        Logger.getLogger(t).log(Level.INFO, msg);
    }

    public static void e(String t, String  msg){
        Logger.getLogger(t).log(Level.WARNING, msg);
    }

    public static void w(String t, String  msg){
        Logger.getLogger(t).log(Level.INFO, msg);
    }

    public static void v(String t, String  msg, Throwable tr){
        Logger.getLogger(t).log(Level.INFO, msg);
    }

    public static void i(String t, String  msg, Throwable tr){
        Logger.getLogger(t).log(Level.INFO, msg);
    }

    public static void d(String t, String  msg, Throwable tr){
        Logger.getLogger(t).log(Level.INFO, msg);
    }

    public static void e(String t, String  msg, Throwable tr){
        Logger.getLogger(t).log(Level.WARNING, msg);
    }

    public static void w(String t, String  msg, Throwable tr){
        Logger.getLogger(t).log(Level.WARNING, msg);
    }
}
