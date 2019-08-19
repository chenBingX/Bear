package utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 
 */

public class ThreadPool {

    private ScheduledExecutorService threadPool;

    private ThreadPool(){
        threadPool = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        //threadPool = Executors.newSingleThreadScheduledExecutor();
    }

    private static final class Holder{
        private static final ThreadPool instance = new ThreadPool();
    }

    static ThreadPool get(){
        return Holder.instance;
    }

    public static void close(){
        get().threadPool.shutdownNow();
    }

    public static void run(Runnable r){
        runDelay(r, 0L);
    }

    public static void runDelay(Runnable r, long delay){
        get().threadPool.schedule(r, delay, TimeUnit.MILLISECONDS);
    }
}
