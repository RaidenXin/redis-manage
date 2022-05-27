package com.raiden.redis.ui.controller.data;

import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.ui.controller.Controller;
import com.raiden.redis.ui.mode.RedisNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 16:48 2022/5/26
 * @Modified By:
 */
public class RedisStringDataViewController implements Controller, Initializable {

    @FXML
    private Button editorButton;
    @FXML
    private TextArea valueTextArea;

    private RedisNode redisNode;
    private String key;
    @Override
    public void init(RedisNode redisNode, String key) {
        this.key = key;
        this.redisNode = redisNode;
        RedisClient redisClient = redisNode.getRedisClient();
        String value = redisClient.get(key);
        valueTextArea.setText(value);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        editorButton.setGraphic(new ImageView("/icon/editor.png"));
    }

    public void updateValue(){
        String value = this.valueTextArea.getText();
        if (StringUtils.isBlank(value)){
            Alert alert = new Alert(Alert.AlertType.WARNING, "value不能为空!");
            alert.showAndWait();
            return;
        }
        RedisClient redisClient = redisNode.getRedisClient();
        redisClient.set(this.key, value);
    }

}
