package com.raiden.redis.ui.tab;

import com.raiden.redis.client.RedisClient;
import com.raiden.redis.ui.Window;
import com.raiden.redis.ui.mode.RedisNode;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Pair;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 21:04 2022/5/11
 * @Modified By:
 */
public class ClusterRedisInfoTabPane {

    private static final String START_INDEX = "0";
    //初始化缓存 用来记录 已经初始化的 Tab
    private volatile Map<String, Tab> initCache;

    public ClusterRedisInfoTabPane(){
        this.initCache = new ConcurrentHashMap<>();
    }

    public void setRedisInfoTabPane(Pane root, List<RedisNode> hosts) {
        ObservableList<Node> children = root.getChildren();
        //清理过去的东西
        children.clear();;
        double prefHeight = root.getPrefHeight();
        double prefWidth = root.getPrefWidth();
        TabPane tabPane = new TabPane();
        tabPane.setPrefHeight(prefHeight);
        tabPane.setPrefWidth(prefWidth);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        //初始化FXML布局文件内容
        if (hosts != null) {
            Map<String, RedisNode> hostCache = new HashMap<>(hosts.size() << 1);
            List<Tab> tabs = hosts.stream().map(host -> {
                String hostAndPort = host.getHostAndPort();
                hostCache.put(hostAndPort, host);
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(Window.class.getResource("redis_cluster_tab_veiw.fxml"));
                    Tab tab = fxmlLoader.load();
                    ImageView iv = new ImageView("/icon/db.jpg");
                    tab.setGraphic(iv);
                    tab.setText(host.getHostAndPort());
                    if (host.isMyself()){
                        initCache.put(hostAndPort, tab);
                        initTab(host, tab);
                    }
                    return tab;
                } catch (IOException e) {
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
            tabPane.getTabs().addAll(tabs);
            //设置点击监听事件 切换新页面在刷新新页面的数据
            tabPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) -> {
                String hostAndPort = newValue.getText();
                Tab tab = initCache.get(hostAndPort);
                if (tab == null){
                    RedisNode redisNode = hostCache.get(hostAndPort);
                    synchronized (this){
                        tab = initCache.get(hostAndPort);
                        if (tab == null && redisNode != null){
                            initCache.put(hostAndPort, newValue);
                            initTab(redisNode, newValue);
                        }
                    }
                }
            });
        }
        children.add(tabPane);
    }

    private void initTab(RedisNode node, Tab tab){
        AnchorPane anchorPane = (AnchorPane) tab.getContent();
        VBox vBox = (VBox) anchorPane.getChildren().get(0);
        TableView tableView = (TableView) vBox.getChildren().get(0);
        TableColumn<Pair<String, String>, String> column1 = (TableColumn) tableView.getColumns().get(0);
        column1.setCellValueFactory(new PropertyValueFactory<>("key"));

        TableColumn<Pair<String, String>, String> column2 = (TableColumn) tableView.getColumns().get(1);
        column2.setCellValueFactory(new PropertyValueFactory<>("value"));
        RedisClient client = node.getRedisClient();
        String[] scan = client.scan(START_INDEX);
        String[] keys = new String[scan.length - 1];
        System.arraycopy(scan, 1, keys, 0, keys.length);
        String[] values = client.mGet(keys);
        int index = 0;
        for (String key : keys){
            tableView.getItems().add(new Pair(key, values[index++]));
        }
        setButtonEvent(vBox, tableView, client, START_INDEX, scan[0]);
    }


    private void setButtonEvent(VBox vBox,TableView tableView,RedisClient client,String current, String next){
        Pane pane = (Pane) vBox.getChildren().get(1);
        Button prePage = (Button) pane.getChildren().get(0);
        AtomicReference<String> currentIndex = new AtomicReference<>(current);
        AtomicReference<String> nextIndex = new AtomicReference<>(next);
        Stack<String> stack = new Stack<>();
        prePage.setOnMouseClicked((event) -> {
            if (stack.size() == 0){
                return;
            }
            //从栈中弹出上一页
            String index = stack.pop();
            //如果没有证明当前是第一页
            if (index != null){
                //清理掉之前的数据
                tableView.getItems().clear();
                //刷新数据
                String[] scan = client.scan(index);
                String[] keys = new String[scan.length - 1];
                System.arraycopy(scan, 1, keys, 0, keys.length);
                String[] values = client.mGet(keys);
                int i = 0;
                for (String key : keys){
                    tableView.getItems().add(new Pair(key, values[i++]));
                }
                //将上一页标记为当前页
                String currentPageIndex = currentIndex.get();
                currentIndex.compareAndSet(currentPageIndex, index);
                //给下一页赋值
                String nextPageIndex = nextIndex.get();
                nextIndex.compareAndSet(nextPageIndex, scan[0]);
            }
        });
        Button nextPage = (Button) pane.getChildren().get(1);
        nextPage.setOnMouseClicked((event) -> {
            //获取下一页的下标
            String index = nextIndex.get();
            //如果为空或者 为 0 证明没有下一页
            if (index != null && !START_INDEX.equals(index)){
                //清理掉之前的数据
                tableView.getItems().clear();
                //获取当前 的下标
                String currentValue = currentIndex.get();
                //将当前的下标 放入栈中存储 供上一页按钮使用
                stack.add(currentValue);
                //获取下一页key
                String[] scan = client.scan(index);
                String[] keys = new String[scan.length - 1];
                System.arraycopy(scan, 1, keys, 0, keys.length);
                String[] values = client.mGet(keys);
                int i = 0;
                for (String key : keys){
                    tableView.getItems().add(new Pair(key, values[i++]));
                }
                //完成翻页后 将下一个设置为当前页
                currentIndex.compareAndSet(currentValue, index);
                //如果下一页 index 赋值
                nextIndex.compareAndSet(index, scan[0]);
            }
        });
    }
}
