package com.raiden.redis.ui.queue;


import java.util.ArrayList;
import java.util.List;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 22:00 2022/5/20
 * @Modified By:
 */
public class CircularFifoQueue<T> {

    private Object[] arr;
    private int size;
    private int index;

    public CircularFifoQueue(int size){
        if (size < 0){
            throw new IllegalArgumentException("The length cannot be less than 0");
        }
        this.index = 0;
        this.size = size;
        this.arr = new Object[size];
    }

    public void add(T t){
        if (t == null){
            throw new NullPointerException();
        }
        synchronized (this){
            //环形数组实现 如果下标到达了长度 就设置为0
            arr[index] = t;
            //index 指向下一个
            this.index = (index + 1) % size;
        }
    }

    public void clear(){
        this.arr = new Object[size];
    }

    public List<T> getAll(){
        List<T> result;
        synchronized (this){
            //先要找到头部 分两种可能
            //第一种还未成环 故此时 index 下标指向的数组位置为空 如长度为5的数组,index = 4,[1,2,3,4,null]。此起始下标为0
            if (arr[this.index] == null){
                result = new ArrayList<>(index);
                for (Object a : arr){
                    if (a == null){
                        break;
                    }
                    result.add((T) a);
                }
            }else {
            //第二种情况 index 指向的位置有值,说明数组已经成环,那起始下标就是 index
                result = new ArrayList<>(size);
                for (int i = 0;i < size; i++){
                    T t = (T) arr[(index + i) % size];
                    result.add(t);
                }
            }
        }
        return result;
    }

    public List<T> getDesc(int limit){
        limit = limit > size ? size : limit;
        List<T> result = new ArrayList<>(limit);
        /**
         * 环形数组 如果倒叙 index - i(向前移动的值) 可能为负数
         *  所以 (index - i(向前移动的值) + size) 在对 size 取模
         *  如果是负数 加上 size 变成正数 取模就获得了真实下标
         *  如果是正数 加上 size 还是正数而且 大于 size  在取模获得真实下标
         */
        synchronized (this){
            int tempIndex = index - 1;
            for (int i = 0;i < limit; i++){
                int index = (tempIndex - i + size) % size;
                T t = (T) arr[index];
                //如果是遍历到为空的 说明还未成环 而且有数据的地方已经遍历过了 打断循环
                if (t == null){
                    break;
                }
                result.add(t);
            }
        }
        return result;
    }

    /**
     * 这个方法可能拿到 null 在未初始化的时候
     * @return
     */
    public T getLast(){
        synchronized (this){
            //先要找到尾部 分两种可能
            //下一个下标值大于 0 那尾部就是 他的前一个 即 index - 1
            if (this.index > 0){
                return (T) arr[this.index - 1];
            }else {
                //第二种情况 index 等于 0 那说明前一个是真正数组的末尾 即 size - 1
                return (T) arr[size - 1];
            }
        }
    }
}
