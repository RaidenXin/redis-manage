package com.raiden.redis.model;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 10:05 2022/5/8
 * @Modified By:
 */
public class RedisSlot {
    /**
     * 开始槽位
     */
    private int startSlot;
    /**
     *  结束槽位
     */
    private int endSlot;

    public int getStartSlot() {
        return startSlot;
    }

    public void setStartSlot(int startSlot) {
        this.startSlot = startSlot;
    }

    public int getEndSlot() {
        return endSlot;
    }

    public void setEndSlot(int endSlot) {
        this.endSlot = endSlot;
    }

    public static final RedisSlot build(String data){
        int index = data.indexOf("-");
        RedisSlot slot = new RedisSlot();
        if (index > -1){
            slot.startSlot = Integer.parseInt(data.substring(0, index));
            slot.endSlot = Integer.parseInt(data.substring(index + 1));
        }else {
            slot.startSlot = Integer.parseInt(data);
        }
        return slot;
    }

    @Override
    public String toString() {
        return "RedisSlot{" +
                "startSlot=" + startSlot +
                ", endSlot=" + endSlot +
                '}';
    }
}
