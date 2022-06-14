package com.raiden.redis.ui.tab;

import com.raiden.redis.ui.controller.RedisAnalyzingBigKeyController;
import com.raiden.redis.ui.util.FXMLLoaderUtils;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 15:40 2022/6/11
 * @Modified By:
 */
public class AnalyzingBigKeyTabPane implements RedisTabPane{

    public Pane createInstance() {
        Pane root = getRoot();
        FXMLLoader fxmlLoader = FXMLLoaderUtils.getFXMLLoader("util/redis_analyzing_big_key_table_view.fxml");
        RedisAnalyzingBigKeyController controller = fxmlLoader.getController();
        ObservableList<Node> children = root.getChildren();
        Node load = null;
        try {
            load = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        children.add(load);
        return root;
    }

    @Override
    public void shutDown() {

    }
}
