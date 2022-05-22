package com.raiden.redis.core.task;


/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:45 2022/5/19
 * @Modified By:
 */
public interface Task extends Runnable {
    long getDelayTime();
}
