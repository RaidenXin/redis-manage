package com.raiden.redis.core.model;

import net.whitbeck.rdbparser.KeyValuePair;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 7:43 2022/6/12
 * @Modified By:
 */
public class DataItem implements Comparable{

    private static final Charset ASCII = Charset.forName("ASCII");

    private String key;
    private int valueSize;

    private DataItem(){
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getValueSize() {
        return valueSize;
    }

    public void setValueSize(int valueSize) {
        this.valueSize = valueSize;
    }


    public static final DataItem build(KeyValuePair pair){
        if (pair == null){
            throw new NullPointerException();
        }
        DataItem item = new DataItem();
        item.key = new String(pair.getKey(), ASCII);
        List<byte[]> values = pair.getValues();
        if (values != null){
            int sum = values.stream().filter(Objects::nonNull).mapToInt(bytes -> bytes.length).sum();
            item.valueSize = sum;
        }else {
            throw new IllegalArgumentException("Value值不正确！");
        }
        return item;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof DataItem){
            return Integer.compare(valueSize, ((DataItem) o).valueSize);
        }
        throw new IllegalArgumentException("DataItem 比较方法参数异常!");
    }
}
