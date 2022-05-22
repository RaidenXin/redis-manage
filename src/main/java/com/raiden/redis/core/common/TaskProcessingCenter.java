package com.raiden.redis.core.common;

import com.raiden.redis.core.task.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:47 2022/5/19
 * @Modified By: 任务处理中心
 */
public final class TaskProcessingCenter {

    private static final Logger LOGGER = LogManager.getLogger(TaskProcessingCenter.class);

    private static final int CORE_POOL_SIZE = 3;
    private static final ExecutorService TASK_THREAD_POOL;
    private static final AtomicInteger COUNT;
    private static final AtomicBoolean SIGN;
    private static final DelayQueue<DelayedTask> TASK_DELAY_QUEUE;

    static {
        TASK_DELAY_QUEUE = new DelayQueue<>();
        COUNT = new AtomicInteger(1);
        SIGN = new AtomicBoolean(true);
        ThreadFactory threadFactory = (Runnable r) -> new Thread(r, "task-handler-thread" + COUNT.getAndAdd(1));
        TASK_THREAD_POOL = new ThreadPoolExecutor(CORE_POOL_SIZE, CORE_POOL_SIZE, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), threadFactory);
        for (int i = 0; i < CORE_POOL_SIZE; i++) {
            TASK_THREAD_POOL.submit(() -> {
                while (SIGN.get()){
                    try {
                        DelayedTask poll = TASK_DELAY_QUEUE.poll(1, TimeUnit.SECONDS);
                        if (poll != null){
                            poll.run();
                        }
                    } catch (InterruptedException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            });
        }
    }

    public static final void submit(Task task){
        TASK_DELAY_QUEUE.add(DelayedTask.build(task));
    }


    public static synchronized void shutDown(){
        SIGN.compareAndSet(true, false);
        TASK_THREAD_POOL.shutdown();
    }

    private static class DelayedTask implements Delayed{

        //下次任务执行时间
        private long nextExecutionTime;
        private Task task;

        public DelayedTask(){
        }

        public boolean run() {
            try {
                task.run();
                return false;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert((nextExecutionTime) - System.currentTimeMillis(),unit);
        }

        @Override
        public int compareTo(Delayed o) {
            return Long.compare(this.getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS));
        }

        public static final DelayedTask build(Task task){
            DelayedTask delayedTask = new DelayedTask();
            delayedTask.nextExecutionTime = System.currentTimeMillis() + task.getDelayTime();
            delayedTask.task = task;
            return delayedTask;
        }
    }
}
