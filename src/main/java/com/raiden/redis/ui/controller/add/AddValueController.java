package com.raiden.redis.ui.controller.add;

import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.ui.mode.RedisNode;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 22:54 2022/5/23
 * @Modified By:
 */
public class AddValueController {

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
    public void addList(RedisNode redisNode, Stage window, String key) {
        submit.setOnAction((event) -> {
            String value = this.valueTextArea.getText();
            if (StringUtils.isBlank(value)){
                Alert alert = new Alert(Alert.AlertType.WARNING, "value不能为空!");
                alert.showAndWait();
                return;
            }
            RedisClient redisClient = redisNode.getRedisClient();
            redisClient.rPush(key, value);
            window.close();
        });
    }
}
