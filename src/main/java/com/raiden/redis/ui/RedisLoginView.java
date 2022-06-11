package com.raiden.redis.ui;/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 11:27 2022/5/7
 * @Modified By:
 */

import com.raiden.redis.core.common.TaskProcessingCenter;
import com.raiden.redis.ui.controller.RedisLoginController;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;

public class RedisLoginView extends Application {

    private static final Logger LOGGER = LogManager.getLogger(RedisLoginView.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        LOGGER.info("启动窗口！");
        //初始化FXML布局文件内容
        FXMLLoader fxmlLoader = new FXMLLoader(RedisLoginView.class.getResource("window.fxml"));
        //父级
        Parent root = fxmlLoader.load();

        RedisLoginController controller = fxmlLoader.getController();
        controller.setLoginView(stage);

        Scene scene = new Scene(root, 415.0D, 1000.0D);
        stage.setTitle("Redis");
        stage.getIcons().add(new Image("/icon/redis.jpg"));
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    public void stop() throws Exception {
        //暂停
        TaskProcessingCenter.shutDown();
        System.err.println("关闭了！！！！！！！！！！！！！！！！！！！！");
    }
}
