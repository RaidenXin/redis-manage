package com.raiden.redis.ui.controller;

import com.raiden.redis.ui.context.BeanContext;
import com.raiden.redis.ui.shutdown.ShutDownCallback;
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

    /**
     * 关闭的回调方法
     */
    private ShutDownCallback callback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BeanContext.setBean(this.getClass().getName(), this);
    }

    public Pane getRedisDataPage(){
        return redisDataPage;
    }

    public void clear(){
        if (callback != null){
            callback.callback();
            callback = null;
        }
    }

    public void setShutDownCallback(ShutDownCallback callback){
        this.callback = callback;
    }

}
