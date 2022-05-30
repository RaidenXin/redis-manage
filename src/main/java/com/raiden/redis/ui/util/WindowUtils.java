package com.raiden.redis.ui.util;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:37 2022/5/29
 * @Modified By: 窗口工具
 */
public final class WindowUtils {

    public static Stage newWindow(String title,Parent load){
        Stage window = new Stage();
        window.setTitle(title);
        //modality要使用Modality.APPLICATION_MODEL
        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinWidth(300);
        window.setMinHeight(150);
        Scene scene = new Scene(load);
        window.setScene(scene);
        return window;
    }
}
