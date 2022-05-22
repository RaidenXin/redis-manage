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
    private String nextIndex;

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public String getNextIndex() {
        return nextIndex;
    }

    public void setNextIndex(String nextIndex) {
        this.nextIndex = nextIndex;
    }

    public static final RedisDatas build(List<String> items,String nextIndex){
        if (items == null || StringUtils.isBlank(nextIndex)){
            throw new IllegalArgumentException("参数异常！");
        }
        RedisDatas datas = new RedisDatas();
        datas.items = items;
        datas.nextIndex = nextIndex;
        return datas;
    }
}
