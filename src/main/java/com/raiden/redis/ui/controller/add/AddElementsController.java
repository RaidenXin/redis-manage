package com.raiden.redis.ui.controller.add;

import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.ui.mode.RedisNode;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 22:54 2022/5/23
 * @Modified By:
 */
public class AddElementsController {

    @FXML
    private Label keyTitle;
    @FXML
    private TextArea keyTextArea;
    @FXML
    private TextArea valueTextArea;
    @FXML
    private Button submit;

    /**
     * hash 和 ZSet 添加 子元素时候用的初始化方法
     * @param redisNode
     * @param window
     * @param key
     */
    public void addHashField(RedisNode redisNode, Stage window, String key) {
        this.keyTitle.setText("Field:");
        if (redisNode == null || window == null){
            throw new RuntimeException("参数不能为空！");
        }
        submit.setOnAction((event) -> {
            Pair<String, String> keyAndValue = getKeyAndValue();
            if (valueTextArea != null){
                RedisClient redisClient = redisNode.getRedisClient();
                redisClient.hSet(key, keyAndValue.getKey(), keyAndValue.getValue());
                window.close();
            }
        });
    }

    public void addString(RedisNode redisNode, Stage window) {
        if (redisNode == null || window == null){
            throw new RuntimeException("参数不能为空！");
        }
        submit.setOnAction((event) -> {
            Pair<String, String> keyAndValue = getKeyAndValue();
            if (keyAndValue != null){
                RedisClient redisClient = redisNode.getRedisClient();
                redisClient.set(keyAndValue.getKey(), keyAndValue.getValue());
                window.close();
            }
        });
    }


    public void addList(RedisNode redisNode, Stage window) {
        if (redisNode == null || window == null){
            throw new RuntimeException("参数不能为空！");
        }
        submit.setOnAction((event) -> {
            Pair<String, String> keyAndValue = getKeyAndValue();
            if (keyAndValue != null){
                RedisClient redisClient = redisNode.getRedisClient();
                redisClient.rPush(keyAndValue.getKey(), keyAndValue.getValue());
                window.close();
            }
        });
    }

    public void sAdd(RedisNode redisNode, Stage window) {
        if (redisNode == null || window == null){
            throw new RuntimeException("参数不能为空！");
        }
        submit.setOnAction((event) -> {
            Pair<String, String> keyAndValue = getKeyAndValue();
            if (keyAndValue != null){
                RedisClient redisClient = redisNode.getRedisClient();
                redisClient.sAdd(keyAndValue.getKey(), keyAndValue.getValue());
                window.close();
            }
        });
    }

    private Pair<String,String> getKeyAndValue(){
        String key = this.keyTextArea.getText();
        String value = this.valueTextArea.getText();
        if (StringUtils.isBlank(key)){
            Alert alert = new Alert(Alert.AlertType.WARNING, "key不能为空!");
            alert.showAndWait();
            return null;
        }
        if (StringUtils.isBlank(value)){
            Alert alert = new Alert(Alert.AlertType.WARNING, "value不能为空!");
            alert.showAndWait();
            return null;
        }
        return new Pair<>(key, value);
    }
}
