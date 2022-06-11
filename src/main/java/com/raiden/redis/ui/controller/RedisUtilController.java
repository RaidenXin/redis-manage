package com.raiden.redis.ui.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 16:08 2022/6/11
 * @Modified By:
 */
public class RedisUtilController {

    private static final long STEP_LENGTH = 1024;

    @FXML
    private TextField path;
    @FXML
    private Button analyse;
    @FXML
    private ProgressBar progressBar;

    private Pane root;



    public void selectFile(){
        FileChooser fileChooser = new FileChooser();
        ObservableList<FileChooser.ExtensionFilter> extensionFilters = fileChooser.getExtensionFilters();
        extensionFilters.add(new FileChooser.ExtensionFilter("RDB Files", "*.rdb"));
        File rdb = fileChooser.showOpenDialog(new Stage());
        if (rdb != null){
            path.setText(rdb.getPath());
            analyse.setOnAction(event -> {
                try {
                    FileChannel open = FileChannel.open(rdb.toPath(), StandardOpenOption.READ);
                    //整个文件的大小
                    long size = open.size();
                    long sum = size / STEP_LENGTH + 1l;
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    for (long i = 0; i < sum ; i++){
                        int read = open.read(byteBuffer);
                        if(read < 0){
                            break;
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
