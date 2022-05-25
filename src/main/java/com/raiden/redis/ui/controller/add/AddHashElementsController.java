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
public class AddHashElementsController {

    @FXML
    private Label keyTitle;
    @FXML
    private TextArea keyTextArea;
    @FXML
    private TextArea fieldTextArea;
    @FXML
    private TextArea valueTextArea;
    @FXML
    private Button submit;

    public void init(RedisNode redisNode, Stage window) {
        if (redisNode == null || window == null){
            throw new RuntimeException("参数不能为空！");
        }
        submit.setOnAction((event) -> {
            String key = this.keyTextArea.getText();
            String field = fieldTextArea.getText();
            String value = this.valueTextArea.getText();
            if (StringUtils.isNoneBlank(key, field, value)){
                Alert alert = new Alert(Alert.AlertType.WARNING, "数据不能为空!");
                alert.showAndWait();
                return;
            }
            RedisClient redisClient = redisNode.getRedisClient();
            redisClient.hSet(key, field, value);
            window.close();
        });
    }
}
