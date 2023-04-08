package com.raiden.redis.core.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步任务执行器
 * @author 疆戟
 * @date 2023/4/5 22:06
 */
public final class AsynchronousTaskExecutor {

    private static final AtomicInteger COUNT = new AtomicInteger(1);
    private static ExecutorService EXECUTOR = new ThreadPoolExecutor(
            1,
            1,
            0,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(10),
            (Runnable r) -> new Thread(r, "asynchronous-task-handler-thread-" + COUNT.getAndIncrement()),
            new ThreadPoolExecutor.DiscardPolicy());


    public static void submit(Runnable task) {
        EXECUTOR.submit(task);
    }

    /**
     * 关闭
     */
    public static synchronized void shutDown(){
        EXECUTOR.shutdown();
    }
}
