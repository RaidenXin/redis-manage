package com.raiden.redis.ui.controller.data;

import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.net.client.RedisClusterClient;
import com.raiden.redis.net.exception.MovedException;
import com.raiden.redis.net.exception.RedisException;
import com.raiden.redis.net.model.ScanResult;
import com.raiden.redis.ui.controller.Controller;
import com.raiden.redis.ui.controller.add.AddElementsController;
import com.raiden.redis.ui.mode.RedisNode;
import com.raiden.redis.ui.util.FXMLLoaderUtils;
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
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;

import static com.raiden.redis.net.common.ScanCommonParams.*;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:04 2022/5/22
 * @Modified By:
 */
public class RedisHashDataViewController implements Controller, Initializable {

    @FXML
    private TableView<Pair<String, String>> tableView;
    @FXML
    private TableColumn<Pair<String,String>, String> field;
    @FXML
    private TableColumn<Pair<String,String>, String> value;
    @FXML
    private TextArea fieldTextArea;
    @FXML
    private TextArea valueTextArea;
    @FXML
    private TextField searchKey;
    @FXML
    private CheckBox isFuzzySearch;
    @FXML
    private Button pre;
    @FXML
    private Button next;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;

    private RedisNode redisNode;
    private AtomicReference<String> currentIndex;
    private AtomicReference<String> nextIndex;
    private Stack<String> stack;
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
                            deleteButton.setOnAction((actionEvent) -> {
                                RedisClient redisClient = redisNode.getRedisClient();
                                redisClient.hDel(this.key, rowData.getKey());
                                refreshTableData();
                            });
                            if (clickCount == 2 ) {
                                fieldTextArea.setText(rowData.getKey());
                                valueTextArea.setText(rowData.getValue());
                            }
                        }
                    });
                    return row ;
                }
        );
        field.setCellFactory(TextFieldTableCell.forTableColumn());
        value.setCellFactory(TextFieldTableCell.forTableColumn());
        field.setCellValueFactory(new PropertyValueFactory<>("key"));
        value.setCellValueFactory(new PropertyValueFactory<>("value"));
        addButton.setGraphic(new ImageView("/icon/add.png"));
        deleteButton.setGraphic(new ImageView("icon/delete.png"));
    }

    @Override
    public void init(RedisNode redisNode,String key) {
        this.key = key;
        this.redisNode = redisNode;
        if (redisNode != null){
            refreshTableData();
        }
    }

    public void search() {
        String text = searchKey.getText();
        if (StringUtils.isBlank(text)) {
            tableView.getItems().clear();
            refreshTableData();
        } else {
            String field = text.trim();
            RedisClient client = redisNode.getRedisClient();
            //是否模糊查找
            if (isFuzzySearch.isSelected()) {
                ScanResult<Pair<String, String>> datas = hscan(client, START_INDEX, field);
                stack.clear();
                currentIndex.set(START_INDEX);
                nextIndex.set(START_INDEX);
                ObservableList items = tableView.getItems();
                items.clear();
                items.addAll(datas.getResult());
                setButtonEvent(client, START_INDEX, datas.getCursor());
            } else {
                try {
                    //精确查找
                    String value = client.hGet(this.key, field);
                    ObservableList items = tableView.getItems();
                    if (StringUtils.isNotBlank(value)) {
                        stack.clear();
                        currentIndex.set(START_INDEX);
                        nextIndex.set(START_INDEX);
                        items.clear();
                        tableView.getItems().add(new Pair<>(field, value));
                    } else {
                        stack.clear();
                        currentIndex.set(START_INDEX);
                        nextIndex.set(START_INDEX);
                        items.clear();
                    }
                }catch (MovedException e){
                    String[] values = StringUtils.split(e.getMessage(), " ");
                    Alert alert = new Alert(Alert.AlertType.WARNING, "请跳转到HOST:{ " + values[2] + " }");
                    alert.showAndWait();
                }catch (RedisException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR, "系统异常！" + e.getMessage());
                    alert.showAndWait();
                }
            }
        }
    }

    private ScanResult<Pair<String, String>> hscan(RedisClient client, String index, String pattern){
        boolean selected = isFuzzySearch.isSelected();
        ScanResult<Pair<String, String>> scan;
        if (selected){
            //如果不是以模糊搜索后缀结尾的 补上后缀
            if (!pattern.endsWith(FUZZY_SEARCH_SUFFIX)){
                pattern += FUZZY_SEARCH_SUFFIX;
            }
            scan = client.hScanMatch(this.key, START_INDEX, pattern, STEP_LENGTH);
        }else {
            scan = client.hScan(this.key, index, STEP_LENGTH);
        }
        return scan;
    }

    private void refreshTableData(){
        RedisClusterClient client = (RedisClusterClient) redisNode.getRedisClient();
        ScanResult<Pair<String, String>> scan = client.hScan(this.key, START_INDEX, STEP_LENGTH);
        ObservableList<Pair<String, String>> items = tableView.getItems();
        items.clear();
        items.addAll(scan.getResult());
        setButtonEvent(client, START_INDEX, scan.getCursor());
    }


    private void setButtonEvent(RedisClient client, String current, String nextCursor){
        currentIndex = new AtomicReference<>(current);
        nextIndex = new AtomicReference<>(nextCursor);
        stack = new Stack<>();
        pre.setOnMouseClicked((event) -> {
            if (stack.size() == 0){
                return;
            }
            //从栈中弹出上一页
            String index = stack.pop();
            //如果没有证明当前是第一页
            if (index != null){
                //清理掉之前的数据
                ObservableList items = tableView.getItems();
                items.clear();
                //刷新数据
                ScanResult<Pair<String, String>> datas = hscan(client, index, searchKey.getText());
                items.addAll(datas.getResult());
                //将上一页标记为当前页
                String currentPageIndex = currentIndex.get();
                currentIndex.compareAndSet(currentPageIndex, index);
                //给下一页赋值
                String nextPageIndex = nextIndex.get();
                nextIndex.compareAndSet(nextPageIndex, datas.getCursor());
            }
        });
        next.setOnMouseClicked((event) -> {
            //获取下一页的下标
            String index = nextIndex.get();
            //如果为空或者 为 0 证明没有下一页
            if (index != null && !START_INDEX.equals(index)){
                //清理掉之前的数据
                ObservableList items = tableView.getItems();
                items.clear();
                //获取当前 的下标
                String currentValue = currentIndex.get();
                //将当前的下标 放入栈中存储 供上一页按钮使用
                stack.add(currentValue);
                //获取下一页key
                ScanResult<Pair<String, String>> datas = hscan(client, index, searchKey.getText());
                items.addAll(datas.getResult());
                //完成翻页后 将下一个设置为当前页
                currentIndex.compareAndSet(currentValue, index);
                //如果下一页 index 赋值
                nextIndex.compareAndSet(index, datas.getCursor());
            }
        });
    }


    public void updateValue(){
        String field = this.fieldTextArea.getText();
        String value = this.valueTextArea.getText();
        if (StringUtils.isBlank(field)){
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
        redisClient.hSet(this.key, field, value);
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

        FXMLLoader fxmlLoader = FXMLLoaderUtils.getFXMLLoader("add/add_elements_view.fxml");
        try {
            TitledPane load = fxmlLoader.load();
            AddElementsController controller = fxmlLoader.getController();
            controller.addHashField(redisNode, window, this.key);
            Scene scene = new Scene(load);
            window.setScene(scene);
            window.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "系统发生错误!" + e.getMessage());
            alert.showAndWait();
        }
    }
}
