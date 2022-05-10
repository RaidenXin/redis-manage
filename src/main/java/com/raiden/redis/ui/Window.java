package com.raiden.redis.ui;/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 11:27 2022/5/7
 * @Modified By:
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;

public class Window extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        //初始化FXML布局文件内容
        FXMLLoader fxmlLoader = new FXMLLoader(Window.class.getResource("window.fxml"));
        //父级
        Parent root = fxmlLoader.load();

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(root);

        Scene scene = new Scene(stackPane, 995, 480);
        stage.setScene(scene);
        stage.show();
    }
}
