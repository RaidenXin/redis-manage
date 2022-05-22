package com.raiden.redis.net.common;

import com.raiden.redis.net.exception.RedisClientException;

import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 14:00 2022/5/22
 * @Modified By:
 */
public enum  DataType {

    NONE("none", null),
    STRING("string", ""),
    LIST("list", ""),
    SET("set", ""),
    ZSET("zset", ""),
    HASH("hash", "redis_h_set_data_table_view.fxml");

    private static final Map<String,DataType> CODE_LOOKUP;

    static {
        CODE_LOOKUP = EnumSet.allOf(DataType.class).stream().collect(Collectors.toMap(DataType::getType, type -> type));
    }

    private String type;
    private String path;

     DataType(String type,String path){
        this.type = type;
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public static DataType of(String type){
        DataType dataType = CODE_LOOKUP.get(type);
        if (dataType == null){
            throw new RedisClientException("返回数据类型异常Type:" + type);
        }
        return dataType;
    }
}
