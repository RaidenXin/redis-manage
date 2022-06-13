package com.raiden.redis.ui.controller;

import com.raiden.redis.ui.DataPageView;
import com.raiden.redis.ui.context.BeanContext;
import com.raiden.redis.ui.shutdown.ShutDownCallback;
import com.raiden.redis.ui.tab.AnalyzingBigKeyTabPane;
import com.raiden.redis.ui.util.AlertUtil;
import com.raiden.redis.ui.util.FXMLLoaderUtils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:26 2022/5/8
 * @Modified By:
 */
public class RedisLoginController implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger(RedisLoginController.class);

    private static final String[] LOGIN_VIEW_PATHS = { "titles/single_point_titled_pane.fxml",  "titles/sentinel_titled_pane.fxml", "titles/cluster_titled_pane.fxml",};

    @FXML
    private Accordion titles;

    private Stage loginView;
    /**
     * 关闭的回调方法
     */
    private ShutDownCallback callback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BeanContext.setBean(this.getClass().getName(), this);
        ObservableList<TitledPane> panes = titles.getPanes();
        for (String longinViewPath : LOGIN_VIEW_PATHS){
            FXMLLoader fxmlLoader = FXMLLoaderUtils.getFXMLLoader(longinViewPath);
            try {
                TitledPane load = fxmlLoader.load();
                AbstractRedisController controller = fxmlLoader.getController();
                controller.setLoginController(this);
                panes.add(load);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
                AlertUtil.error("加载登录页面错误！", e);
            }
        }
    }

    public void show(){
        if (loginView != null){
            loginView.show();
        }
    }

    public void close(){
        if (loginView != null){
            loginView.close();
        }
    }

    public void setLoginView(Stage loginView){
        this.loginView = loginView;
    }

    public void clear(){
        if (callback != null){
            callback.callback();
            callback = null;
        }
    }

    public void setShutDownCallback(ShutDownCallback callback){
        this.callback = callback;
    }


    public void bigKeyAnalysis(){
        AnalyzingBigKeyTabPane utilTabPane = new AnalyzingBigKeyTabPane();
        Pane instance = utilTabPane.createInstance();
        close();
        DataPageView dataPageView = new DataPageView(instance, () -> show());
        dataPageView.start();
    }

}
