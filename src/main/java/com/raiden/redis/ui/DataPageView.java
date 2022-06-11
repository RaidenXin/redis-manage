package com.raiden.redis.ui;

import com.raiden.redis.core.common.TaskProcessingCenter;
import com.raiden.redis.ui.shutdown.ShutDownCallback;
import com.raiden.redis.ui.util.RedisUtils;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 22:35 2022/6/9
 * @Modified By:
 */
public class DataPageView {

    private static final Logger LOGGER = LogManager.getLogger(DataPageView.class);
    private Parent root;

    private ShutDownCallback callback;

    public DataPageView(Parent root, ShutDownCallback callback){
        this.root = root;
        this.callback = callback;
    }

    public void start() {
        //启动任务处理器
        TaskProcessingCenter.start();
        LOGGER.info("启动数据窗口！");
        Stage stage = new Stage();
        Scene scene = new Scene(root, 1087.0D, 1000.0D);
        stage.setTitle("Redis");
        stage.getIcons().add(new Image("/icon/redis.jpg"));
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setResizable(false);
        stage.setOnHidden(event -> stop());
        stage.show();
    }

    public void stop() {
        //暂停
        TaskProcessingCenter.suspension();
        RedisUtils.shutDown();
        if (callback != null){
            callback.callback();
        }
        LOGGER.info("数据窗口关闭了！！！！！！！！！！！！！！！！！！！！");
    }
}
