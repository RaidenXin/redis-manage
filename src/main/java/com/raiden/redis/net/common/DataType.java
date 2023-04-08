package com.raiden.redis.net.common;

import com.raiden.redis.net.exception.RedisClientException;
import com.raiden.redis.ui.controller.add.AddElementsController;
import com.raiden.redis.ui.controller.add.AddHashElementsController;
import com.raiden.redis.ui.mode.RedisNode;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

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

    NONE("none", null, null) {
        @Override
        public void add(FXMLLoader fxmlLoader, RedisNode redisNode, Stage window) {
            throw new RuntimeException();
        }
    },
    STRING("string", "data/redis_string_data_table_view.fxml", "add/add_elements_view.fxml") {
        @Override
        public void add(FXMLLoader fxmlLoader, RedisNode redisNode, Stage window) {
            AddElementsController controller = fxmlLoader.getController();
            controller.addString(redisNode, window);
        }
    },
    LIST("list", "data/redis_list_data_table_view.fxml", "add/add_elements_view.fxml"){
        @Override
        public void add(FXMLLoader fxmlLoader, RedisNode redisNode, Stage window) {
            AddElementsController controller = fxmlLoader.getController();
            controller.addList(redisNode, window);
        }
    },
    SET("set", "data/redis_set_data_table_view.fxml", "add/add_elements_view.fxml"){
        @Override
        public void add(FXMLLoader fxmlLoader, RedisNode redisNode, Stage window) {
            AddElementsController controller = fxmlLoader.getController();
            controller.sAdd(redisNode, window);
        }
    },
    ZSET("zset", "data/redis_z_set_data_table_view.fxml", "add/add_hash_z_set_elements_view.fxml"){
        @Override
        public void add(FXMLLoader fxmlLoader, RedisNode redisNode, Stage window) {
            AddHashElementsController controller = fxmlLoader.getController();
            controller.zAdd(redisNode, window);
        }
    },
    HASH("hash", "data/redis_hash_data_table_view.fxml", "add/add_hash_z_set_elements_view.fxml"){
        @Override
        public void add(FXMLLoader fxmlLoader, RedisNode redisNode, Stage window) {
            AddHashElementsController controller = fxmlLoader.getController();
            controller.addHash(redisNode, window);
        }
    };

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

    public abstract void add(FXMLLoader fxmlLoader, RedisNode redisNode, Stage window);
}
