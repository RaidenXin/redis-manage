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

    NONE("none", null, null),
    STRING("string", "", "add/add_hash_z_set_elements_view.fxml"),
    LIST("list", "", "add/add_hash_z_set_elements_view.fxml"),
    SET("set", "", "add/add_hash_z_set_elements_view.fxml"),
    ZSET("zset", "", "add/add_hash_z_set_elements_view.fxml"),
    HASH("hash", "add/redis_h_set_data_table_view.fxml", "add/add_hash_z_set_elements_view.fxml");

    private static final Map<String,DataType> CODE_LOOKUP;

    static {
        CODE_LOOKUP = EnumSet.allOf(DataType.class).stream().collect(Collectors.toMap(DataType::getType, type -> type));
    }

    private String type;
    private String showView;
    private String addView;

     DataType(String type,String showView,String addView){
        this.type = type;
        this.showView = showView;
        this.addView = addView;
    }

    public String getType() {
        return type;
    }

    public String getShowView() {
        return showView;
    }

    public String getAddView(){
         return addView;
    }

    public static DataType of(String type){
        DataType dataType = CODE_LOOKUP.get(type);
        if (dataType == null){
            throw new RedisClientException("返回数据类型异常Type:" + type);
        }
        return dataType;
    }
}
