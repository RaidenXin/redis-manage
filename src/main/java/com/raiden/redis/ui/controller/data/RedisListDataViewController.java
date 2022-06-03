package com.raiden.redis.ui.controller.data;

import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.ui.controller.Controller;
import com.raiden.redis.ui.controller.add.AddValueController;
import com.raiden.redis.ui.mode.RedisNode;
import com.raiden.redis.ui.util.FXMLLoaderUtils;
import com.raiden.redis.ui.util.PropertyValueUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import static com.raiden.redis.net.common.ScanCommonParams.*;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:04 2022/5/22
 * @Modified By:
 */
public class RedisListDataViewController implements Controller, Initializable {


    private static final int PAGE_SIZE = 20;

    @FXML
    private TableView<Pair<String, String>> tableView;
    @FXML
    private TableColumn<Pair<String,String>, String> index;
    @FXML
    private TableColumn<Pair<String,String>, String> value;
    @FXML
    private TextArea indexTextArea;
    @FXML
    private TextArea valueTextArea;
    @FXML
    private Pagination pagination;
    @FXML
    private Button addButton;
    @FXML
    private Button editorButton;

    private RedisNode redisNode;
    private String key;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setRowFactory( tv -> {
                    TableRow<Pair<String, String>> row = new TableRow<>();
                    row.setOnMouseClicked(event -> {
                        int clickCount = event.getClickCount();
                        if ((clickCount == 1 || clickCount == 2) && !row.isEmpty()){
                            Pair<String, String> rowData = row.getItem();
                            if (clickCount == 2 ) {
                                indexTextArea.setText(rowData.getKey());
                                valueTextArea.setText(rowData.getValue());
                            }
                        }
                    });
                    return row ;
                }
        );
        index.setCellFactory(TextFieldTableCell.forTableColumn());
        value.setCellFactory(TextFieldTableCell.forTableColumn());
        index.setCellValueFactory(PropertyValueUtil.getPairKeyPropertyValueFactory());
        value.setCellValueFactory(PropertyValueUtil.getPairValuePropertyValueFactory());
        addButton.setGraphic(new ImageView("/icon/add.png"));
        editorButton.setGraphic(new ImageView("/icon/editor.png"));
    }

    @Override
    public void init(RedisNode redisNode,String key) {
        this.key = key;
        this.redisNode = redisNode;
        if (redisNode != null){
            refreshTableData();
        }
    }


    private void refreshTableData(){
        RedisClient client = redisNode.getRedisClient();
        int len = client.lLen(key);
        String[] values = client.lrAnge(this.key, START_INDEX, String.valueOf(PAGE_SIZE - 1));
        ObservableList<Pair<String, String>> items = tableView.getItems();
        items.clear();
        List<Pair<String, String>> data = new ArrayList<>();
        int index = 0;
        for (String value : values){
            data.add(new Pair<>(String.valueOf(index++), value));
        }
        items.addAll(data);
        //默认从0开始
        pagination.setCurrentPageIndex(0);
        //设置最大页面
        pagination.setPageCount((len / PAGE_SIZE) + (len % PAGE_SIZE == 0 ? 0 : 1));
        ChangeListener<Number> listener = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->{
            int start = newValue.intValue() * PAGE_SIZE;
            String[] list = client.lrAnge(key, String.valueOf(start), String.valueOf((start + 1) * PAGE_SIZE - 1));
            items.clear();
            List<Pair<String, String>> dataList = new ArrayList<>();
            int i = 0;
            for (String value : list) {
                dataList.add(new Pair<>(String.valueOf(start + i++), value));
            }
            items.addAll(dataList);
        };
        pagination.currentPageIndexProperty().addListener(listener);
    }


    public void updateValue(){
        String index = this.indexTextArea.getText();
        String value = this.valueTextArea.getText();
        if (StringUtils.isBlank(index)){
            Alert alert = new Alert(Alert.AlertType.WARNING, "field不能为空!");
            alert.showAndWait();
            return;
        }
        if (StringUtils.isBlank(value)){
            Alert alert = new Alert(Alert.AlertType.WARNING, "value不能为空!");
            alert.showAndWait();
            return;
        }
        RedisClient redisClient = redisNode.getRedisClient();
        redisClient.lSet(this.key, index, value);
        //刷新列表数据
        refreshTableData();
    }

    public void openAddView(){
        Stage window = new Stage();
        window.setTitle("添加");
        //modality要使用Modality.APPLICATION_MODEL
        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinWidth(300);
        window.setMinHeight(150);

        FXMLLoader fxmlLoader = FXMLLoaderUtils.getFXMLLoader("add/add_value_view.fxml");
        try {
            TitledPane load = fxmlLoader.load();
            AddValueController controller = fxmlLoader.getController();
            controller.addList(redisNode, window, this.key);
            Scene scene = new Scene(load);
            window.setScene(scene);
            window.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "系统发生错误!" + e.getMessage());
            alert.showAndWait();
        }
    }
}
