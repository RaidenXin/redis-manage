package com.raiden.redis.ui.controller;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 9:00 2022/5/14
 * @Modified By:
 */
public class RedisSentinelController implements Initializable {

    public void connectionRedisCluster(){

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BeanContext.setBean(this.getClass().getName(), this);
    }

    public void addRecord(){

    }

    public void clearInputField(){

    }
}
