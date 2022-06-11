package com.raiden.redis.ui.tab;

import javafx.scene.layout.Pane;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 15:25 2022/6/5
 * @Modified By:
 */
public interface RedisTabPane {
    /**
     * 关闭方法
     */
    void shutDown();

    default Pane getRoot(){
        Pane pane = new Pane();
        pane.setPrefHeight(1000.0D);
        pane.setPrefWidth(1083.0D);
        return pane;
    }
}

