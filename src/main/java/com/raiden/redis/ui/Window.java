package com.raiden.redis.ui;/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 11:27 2022/5/7
 * @Modified By:
 */

import com.raiden.redis.core.common.TaskProcessingCenter;
import com.raiden.redis.ui.util.RedisUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;

public class Window extends Application {

    private static final Logger LOGGER = LogManager.getLogger(Window.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        LOGGER.info("启动窗口！");
        //初始化FXML布局文件内容
        FXMLLoader fxmlLoader = new FXMLLoader(Window.class.getResource("window.fxml"));
        //父级
        Parent root = fxmlLoader.load();

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(root);
        Scene scene = new Scene(stackPane, 1500, 1000);
        stage.setTitle("Redis");
        stage.getIcons().add(new Image("/icon/redis.jpg"));
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setResizable(false);
        stage.show();
    }

    public void stop() throws Exception {
        RedisUtils.shutDown();
        TaskProcessingCenter.shutDown();
        System.err.println("关闭了！！！！！！！！！！！！！！！！！！！！");
    }
}
