package com.raiden.redis.ui.controller;

import com.raiden.redis.ui.context.BeanContext;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:26 2022/5/8
 * @Modified By:
 */
public class RedisDataPageController implements Initializable {

    @FXML
    private Pane redisDataPage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BeanContext.setBean(this.getClass().getName(), this);
    }

    public Pane getRedisDataPage(){
        return redisDataPage;
    }

}
