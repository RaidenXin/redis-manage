package com.raiden.redis.core.common;

import com.raiden.redis.core.task.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:47 2022/5/19
 * @Modified By: 任务处理中心
 */
public final class TaskProcessingCenter {

    private static final Logger LOGGER = LogManager.getLogger(TaskProcessingCenter.class);

    private static final int CORE_POOL_SIZE = 2;
    private static final ExecutorService TASK_THREAD_POOL;
    private static final AtomicInteger COUNT;
    private static final AtomicBoolean SIGN;
    private static final AtomicBoolean SUSPENSION;
    private static final DelayQueue<DelayedTask> TASK_DELAY_QUEUE;
    private static final ReentrantLock LOCK;
    private static final Condition CONDITION;

    static {
        LOCK = new ReentrantLock();
        CONDITION = LOCK.newCondition();
        TASK_DELAY_QUEUE = new DelayQueue<>();
        COUNT = new AtomicInteger(1);
        SIGN = new AtomicBoolean(true);
        SUSPENSION = new AtomicBoolean(false);
        ThreadFactory threadFactory = (Runnable r) -> new Thread(r, "task-handler-thread-" + COUNT.getAndIncrement());
        TASK_THREAD_POOL = new ThreadPoolExecutor(CORE_POOL_SIZE, CORE_POOL_SIZE, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), threadFactory);
        for (int i = 0; i < CORE_POOL_SIZE; i++) {
            TASK_THREAD_POOL.submit(() -> {
                //这里是为了安全结束任务设置的结束标识
                while (SIGN.get()){
                    try {
                        checkSuspensionStatus();
                        //如果没有就等待 1 秒钟
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

    /**
     * 校验暂停状态
     */
    private static void checkSuspensionStatus(){
        //如果是暂停
        while (SUSPENSION.get() && SIGN.get()){
            //并休眠
            LOCK.lock();
            try {
                CONDITION.await(8, TimeUnit.SECONDS);
            }catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }finally {
                LOCK.unlock();
            }
        }
    }

    /**
     * 启动
     */
    public static void start(){
        if (SUSPENSION.compareAndSet(true, false)){
            LOCK.lock();
            try {
                //唤醒所有的线程
                CONDITION.signalAll();
            }finally {
                LOCK.unlock();
            }
        }
    }

    /**
     * 暂停
     */
    public static void suspension(){
        if (SUSPENSION.compareAndSet(false, true)){
            LOCK.lock();
            try {
                //清空任务
                TASK_DELAY_QUEUE.clear();
            }finally {
                LOCK.unlock();
            }
        }
    }

    public static final void submit(Task task){
        TASK_DELAY_QUEUE.add(DelayedTask.build(task));
    }

    /**
     * 关闭
     */
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
                LOGGER.error(e.getMessage(), e);
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
