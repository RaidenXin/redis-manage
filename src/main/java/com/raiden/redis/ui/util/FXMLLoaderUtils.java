package com.raiden.redis.ui.util;

import com.raiden.redis.ui.RedisLoginView;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 0:31 2022/5/20
 * @Modified By:
 */
public final class FXMLLoaderUtils {

    private static final Logger LOGGER = LogManager.getLogger(FXMLLoaderUtils.class);

    public static final FXMLLoader getFXMLLoader(String fxmlName){
        return new FXMLLoader(RedisLoginView.class.getResource(fxmlName));
    }

    public static final <T> T getNode(String fxmlName){
        FXMLLoader loader = new FXMLLoader(RedisLoginView.class.getResource(fxmlName));
        try {
            return loader.load();
        } catch (IOException e) {
            String errorMessage = "加载节点失败！Fxml文件名称:" + fxmlName;
            LOGGER.error(errorMessage);
            LOGGER.error(e.getMessage(), e);
            Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage);
            alert.showAndWait();
            return null;
        }
    }
}
