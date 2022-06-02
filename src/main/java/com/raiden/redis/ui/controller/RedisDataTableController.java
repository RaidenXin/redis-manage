package com.raiden.redis.ui.controller;

import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.net.common.DataType;
import com.raiden.redis.net.common.Separator;
import com.raiden.redis.net.exception.UnknownCommandException;
import com.raiden.redis.ui.controller.add.AddElementsController;
import com.raiden.redis.ui.controller.add.AddHashElementsController;
import com.raiden.redis.ui.mode.RedisDatas;
import com.raiden.redis.ui.mode.RedisNode;
import com.raiden.redis.ui.util.FXMLLoaderUtils;
import com.raiden.redis.ui.util.MemoryComputingUtil;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import static com.raiden.redis.net.common.ScanCommonParams.*;


/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 16:22 2022/5/15
 * @Modified By:
 */
public class RedisDataTableController implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger(RedisDataTableController.class);

    private static final String MOVED = "MOVED";

    @FXML
    private Button searchButton;
    @FXML
    private Button add;
    @FXML
    private Button delete;
    @FXML
    private TextField searchKey;
    @FXML
    private Pane bottomBar;
    @FXML
    private CheckBox isFuzzySearch;
    @FXML
    private ComboBox<String> pageSize;
    @FXML
    private ListView<String> keyList;
    @FXML
    private ComboBox<String> dataType;
    @FXML
    private TextField key;
    @FXML
    private AnchorPane dataView;
    @FXML
    private Text memoryUsage;

    private RedisNode redisNode;
    private AtomicReference<String> currentIndex;
    private AtomicReference<String> nextIndex;
    private Stack<String> stack;
    private AtomicBoolean isInitTab;

    public RedisDataTableController(){
        this.isInitTab = new AtomicBoolean(false);
    }

    public void setRedisNode(RedisNode redisNode){
        this.redisNode = redisNode;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchButton.setGraphic(new ImageView("/icon/search.jpg"));
        add.setGraphic(new ImageView("/icon/add.png"));
        delete.setGraphic(new ImageView("/icon/delete.png"));
        keyList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) ->{
            if (redisNode != null){
                if (StringUtils.isNotBlank(newValue)){
                    RedisClient redisClient = redisNode.getRedisClient();
                    DataType type = redisClient.type(newValue);
                    dataType.getSelectionModel().select(type.getType());
                    FXMLLoader fxmlLoader = FXMLLoaderUtils.getFXMLLoader(type.getShowView());
                    try {
                        Node load = fxmlLoader.load();
                        Controller controller = fxmlLoader.getController();
                        controller.init(redisNode, newValue);
                        try {
                            //这里要捕获一个没有找到命令的异常 因为可能没有开启这个命令
                            long memoryUsage = redisClient.memoryUsage(newValue);
                            this.memoryUsage.setText(MemoryComputingUtil.getReadableMemory(memoryUsage));
                        }catch (UnknownCommandException e){
                        }
                        dataView.getChildren().add(load);
                    } catch (Exception e) {
                        String message = e.getMessage();
                        LOGGER.error(message, e);
                        Alert alert = new Alert(Alert.AlertType.ERROR, message);
                        alert.showAndWait();
                    }
                }else {
                    dataType.getSelectionModel().select(null);
                }
                key.setText(newValue);
            }
        });
    }

    public void search() {
        String text = searchKey.getText();
        if (StringUtils.isBlank(text)) {
            keyList.getItems().clear();
            initTableData();
        } else {
            String key = text.trim();
            RedisClient client = redisNode.getRedisClient();
            //是否模糊查找
            if (isFuzzySearch.isSelected()) {
                RedisDatas datas = scanRedisDatas(client, START_INDEX, key);
                if (datas == null){
                    return;
                }
                stack.clear();
                currentIndex.set(START_INDEX);
                nextIndex.set(START_INDEX);
                ObservableList items = keyList.getItems();
                items.clear();
                items.addAll(datas.getItems());
                setButtonEvent(client, START_INDEX, datas.getNextCursor());
            } else {
                //精确查找
                String value = client.get(key);
                ObservableList items = keyList.getItems();
                if (StringUtils.isNotBlank(value)) {
                    if (value.startsWith(MOVED)) {
                        String[] values = StringUtils.split(value, Separator.BLANK);
                        if (values.length != 3) {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "系统异常！");
                            alert.showAndWait();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.WARNING, "请跳转到HOST:{ " + values[2] + " }");
                            alert.showAndWait();
                        }
                        return;
                    } else {
                        stack.clear();
                        currentIndex.set(START_INDEX);
                        nextIndex.set(START_INDEX);
                        items.clear();
                        keyList.getItems().add(key);
                    }
                } else {
                    stack.clear();
                    currentIndex.set(START_INDEX);
                    nextIndex.set(START_INDEX);
                    items.clear();
                }
            }
        }
    }

    private RedisDatas scanRedisDatas(RedisClient client, String index, String pattern){
        boolean selected = isFuzzySearch.isSelected();
        String[] keys;
        if (selected){
            //如果不是以模糊搜索后缀结尾的 补上后缀
            if (!pattern.endsWith(FUZZY_SEARCH_SUFFIX)){
                pattern += FUZZY_SEARCH_SUFFIX;
            }
            keys = client.scanMatch(START_INDEX, pattern, pageSize.getValue());
        }else {
            keys = client.scan(index, pageSize.getValue());
        }

        List<String> items = new ArrayList<>(keys.length - 1);
        for (int i = 1; i < keys.length; i++){
            items.add(keys[i]);
        }
        //如果没有返回任何数据 或者 只返回了下一次的下标 的认为没有数据了
        if (keys.length < 2){
            return null;
        }
        return RedisDatas.build(items, keys[0]);
    }





    public void initTable(){
        if (isInitTab.compareAndSet(false, true)){
            initTableData();
        }
    }

    private void initTableData(){
        RedisClient client = redisNode.getRedisClient();
        String[] keys = client.scan(START_INDEX, pageSize.getValue());
        ObservableList items = keyList.getItems();
        for (int i = 1; i < keys.length; i++){
            items.add(keys[i]);
        }
        setButtonEvent(client, START_INDEX, keys[0]);
    }


    private void setButtonEvent(RedisClient client, String current, String next){
        Button prePage = (Button) bottomBar.getChildren().get(0);
        currentIndex = new AtomicReference<>(current);
        nextIndex = new AtomicReference<>(next);
        stack = new Stack<>();
        prePage.setOnMouseClicked((event) -> {
            if (stack.size() == 0){
                return;
            }
            //从栈中弹出上一页
            String index = stack.pop();
            //如果没有证明当前是第一页
            if (index != null){
                //刷新数据
                RedisDatas datas = scanRedisDatas(client, index, searchKey.getText());
                if (datas == null){
                    return;
                }
                //清理掉之前的数据
                ObservableList items = keyList.getItems();
                items.clear();
                items.addAll(datas.getItems());
                //将上一页标记为当前页
                String currentPageIndex = currentIndex.get();
                currentIndex.compareAndSet(currentPageIndex, index);
                //给下一页赋值
                String nextPageIndex = nextIndex.get();
                nextIndex.compareAndSet(nextPageIndex, datas.getNextCursor());
            }
        });
        Button nextPage = (Button) bottomBar.getChildren().get(1);
        nextPage.setOnMouseClicked((event) -> {
            //获取下一页的下标
            String index = nextIndex.get();
            //如果为空或者 为 0 证明没有下一页
            if (index != null && !START_INDEX.equals(index)){
                //获取下一页key
                RedisDatas datas = scanRedisDatas(client, index, searchKey.getText());
                if (datas == null){
                    return;
                }
                //获取当前 的下标
                String currentValue = currentIndex.get();
                //将当前的下标 放入栈中存储 供上一页按钮使用
                stack.add(currentValue);
                //清理掉之前的数据
                ObservableList items = keyList.getItems();
                items.clear();
                items.addAll(datas.getItems());
                //完成翻页后 将下一个设置为当前页
                currentIndex.compareAndSet(currentValue, index);
                //如果下一页 index 赋值
                nextIndex.compareAndSet(index, datas.getNextCursor());
            }
        });
    }

    public void add(){
        Stage window = new Stage();
        window.setTitle("添加");
        //modality要使用Modality.APPLICATION_MODEL
        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinWidth(300);
        window.setMinHeight(150);

        String type = dataType.getSelectionModel().getSelectedItem();
        if (StringUtils.isBlank(type)){
            Alert alert = new Alert(Alert.AlertType.WARNING, "请选择数据类型!");
            alert.showAndWait();
            return;
        }
        try {
            DataType dataType = DataType.of(type);
            FXMLLoader fxmlLoader = FXMLLoaderUtils.getFXMLLoader(dataType.getAddView());
            TitledPane load = fxmlLoader.load();
            switch (dataType){
                case HASH:{
                    AddHashElementsController controller = fxmlLoader.getController();
                    controller.addHash(redisNode, window);
                    break;
                }
                case LIST:{
                    AddElementsController controller = fxmlLoader.getController();
                    controller.addList(redisNode, window);
                    break;
                }
                case SET:{
                    AddElementsController controller = fxmlLoader.getController();
                    controller.sAdd(redisNode, window);
                    break;
                }
                case STRING:{
                    AddElementsController controller = fxmlLoader.getController();
                    controller.addString(redisNode, window);
                    break;
                }
                case ZSET:{
                    AddHashElementsController controller = fxmlLoader.getController();
                    controller.zAdd(redisNode, window);
                    break;
                }
            }
            Scene scene = new Scene(load);
            window.setScene(scene);
            window.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "系统发生错误!" + e.getMessage());
            alert.showAndWait();
        }
    }
}
