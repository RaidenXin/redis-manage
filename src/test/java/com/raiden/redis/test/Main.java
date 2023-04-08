package com.raiden.redis.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        AnchorPane root = loader.load();
        VBox vBox = (VBox) loader.getNamespace().get("vBox");

        // 监听外层 AnchorPane 的大小变化，并根据新的大小调整内层 VBox 的大小
        root.widthProperty().addListener((observable, oldValue, newValue) -> {
            vBox.setPrefWidth(newValue.doubleValue());
        });
        root.heightProperty().addListener((observable, oldValue, newValue) -> {
            vBox.setPrefHeight(newValue.doubleValue());
        });

        primaryStage.setScene(new Scene(root, 200, 200));
        primaryStage.show();

        // 设置按钮事件处理程序，当按钮被点击时，改变 VBox 的高度
        Button resizeButton = (Button) loader.getNamespace().get("resizeButton");
        resizeButton.setOnAction(event -> {
            vBox.setPrefHeight(vBox.getPrefHeight() + 10);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
