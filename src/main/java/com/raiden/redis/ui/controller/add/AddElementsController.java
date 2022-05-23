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
public class AddElementsController {

    @FXML
    private Label keyTitle;
    @FXML
    private TextArea keyTextArea;
    @FXML
    private TextArea valueTextArea;
    @FXML
    private Button submit;

    public void init(RedisNode redisNode, Stage window, boolean isHSet,String key) {
        if (isHSet){
            this.keyTitle.setText("field:");
        }
        if (redisNode == null || window == null){
            throw new RuntimeException("参数不能为空！");
        }
        submit.setOnAction((event) -> {
            String field = this.keyTextArea.getText();
            String value = this.valueTextArea.getText();
            if (StringUtils.isBlank(field)){
                Alert alert = new Alert(Alert.AlertType.WARNING, "field不能为空!");
                alert.showAndWait();
                return;
            }
            if (StringUtils.isBlank(value)){
                Alert alert = new Alert(Alert.AlertType.WARNING, "value不能为空!");
                alert.showAndWait();
                return;
            }
            RedisClient redisClient = redisNode.getRedisClient();
            if (isHSet){
                redisClient.hSet(key, field, value);
            }else {
                redisClient.set(field, value);
            }
            window.close();
        });
    }
}
