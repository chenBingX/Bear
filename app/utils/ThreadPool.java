package utils;

import java.util.concurrent.*;

/**
 * 
 */

public class ThreadPool {

    private ScheduledExecutorService threadPool;
    private ScheduledThreadPoolExecutor globleExecutor;

    private ThreadPool(){
        threadPool = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        globleExecutor = new ScheduledThreadPoolExecutor(1, new ThreadPoolExecutor.DiscardPolicy());
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

    public static void postDelay(Runnable r, long delay){
        get().globleExecutor.schedule(r, delay, TimeUnit.MILLISECONDS);
    }

    public static void remove(Runnable task){
        get().globleExecutor.remove(task);
    }
}
