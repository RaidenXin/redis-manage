package com.raiden.redis.ui.mode;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:48 2022/5/15
 * @Modified By:
 */
public class RedisDatas {

    private List<String> items;
    private String nextCursor;

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }

    public static final RedisDatas build(List<String> items,String nextIndex){
        if (items == null || StringUtils.isBlank(nextIndex)){
            throw new IllegalArgumentException("参数异常！");
        }
        RedisDatas datas = new RedisDatas();
        datas.items = items;
        datas.nextCursor = nextIndex;
        return datas;
    }
}
