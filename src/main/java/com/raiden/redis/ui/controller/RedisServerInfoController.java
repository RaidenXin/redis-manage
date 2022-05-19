package com.raiden.redis.ui.controller;

import com.raiden.redis.core.common.TaskProcessingCenter;
import com.raiden.redis.core.task.Task;
import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.net.model.*;
import com.raiden.redis.ui.mode.RedisNode;
import com.raiden.redis.ui.util.FXMLLoaderUtils;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 19:29 2022/5/18
 * @Modified By:
 */
public class RedisServerInfoController {
    @FXML
    private TableView redisServerTable;
    @FXML
    private TableColumn<Pair<String, String>, String> redisServerTableKey;
    @FXML
    private TableColumn<Pair<String, String>, String> redisServerTableValue;
    @FXML
    private TitledPane redisCPUTitledPane;
    @FXML
    private TableView redisClientTable;
    @FXML
    private TableColumn<Pair<String, String>, String> redisClientTableTableKey;
    @FXML
    private TableColumn<Pair<String, String>, String> redisClientTableTableValue;
    @FXML
    private TreeTableView redisStatsTable;
    @FXML
    private TreeTableColumn<Object, String> redisStatsTableTableKey;
    @FXML
    private TreeTableColumn<Object, String> redisStatsTableTableValue;

    private RedisNode redisNode;


    public void init(RedisNode redisNode){
        if (redisNode != null){
            this.redisNode = redisNode;
            RedisClient redisClient = redisNode.getRedisClient();
            if (redisClient == null){
                return;
            }
            if (!redisClient.isActive()){
                redisClient.close();
                return;
            }
            //刷新数据
            refresh(redisClient);
            TaskProcessingCenter.submit(new RefreshTask(this), 5, TimeUnit.SECONDS);
        }
    }


    public void refresh(RedisClient redisClient){
        RedisNodeInfo info = redisClient.info();
        //server
        {
            RedisServerInfo server = info.getServer();
            ObservableList<TableColumn> columns = redisServerTable.getColumns();
            columns.stream().forEach(c -> c.setCellFactory(TextFieldTableCell.forTableColumn()));
            redisServerTableKey.setCellValueFactory(new PropertyValueFactory<>("key"));
            redisServerTableValue.setCellValueFactory(new PropertyValueFactory<>("value"));
            ObservableList items = redisServerTable.getItems();
            items.clear();
            items.add(new Pair<>("Redis服务版本", server.getRedisVersion()));
            items.add(new Pair<>("服务器模式", server.getRedisMode()));
            items.add(new Pair<>("Redis服务器的随机标识符", server.getRunId()));
            items.add(new Pair<>("TCP/IP 监听端口", server.getTcpPort()));
            items.add(new Pair<>("运行时间(单位秒)", server.getUptimeInSeconds()));
            items.add(new Pair<>("运行时间(单位天)", server.getUptimeInDays()));
        }
        //cpu
        {

            AnchorPane root = FXMLLoaderUtils.getNode("redis_cpu_bar_chart.fxml");
            if (root != null){
                BarChart redisCPUBarChart = (BarChart) root.lookup("#redisCPUBarChart");
                String systemCpuUsage = "系统CPU使用率";
                String userCpuUsage = "用户CPU使用率";
                RedisCpuInfo cpu = info.getCpu();
                ObservableList<XYChart.Series<String, Double>> items = redisCPUBarChart.getData();
                items.clear();
                //Redis服务
                XYChart.Series<String,Double> series = new XYChart.Series<>();
                ObservableList<XYChart.Data<String, Double>> data = series.getData();
                series.setName("Redis服务");
                data.add(new XYChart.Data(systemCpuUsage, cpu.getUsedCpuSys()));
                data.add(new XYChart.Data(userCpuUsage, cpu.getUsedCpuUser()));
                items.add(series);

                //Redis主线程
                XYChart.Series<String,Double> mainThread = new XYChart.Series<>();
                data = mainThread.getData();
                mainThread.setName("Redis主线程");
                data.add(new XYChart.Data(systemCpuUsage, cpu.getUsedCpuSysMainThread()));
                data.add(new XYChart.Data(userCpuUsage, cpu.getUsedCpuUserMainThread()));
                items.add(mainThread);

                //后台进程
                XYChart.Series<String,Double> backgroundProcess = new XYChart.Series<>();
                data = backgroundProcess.getData();
                backgroundProcess.setName("后台进程");
                data.add(new XYChart.Data(systemCpuUsage, cpu.getUsedCpuSysChildren()));
                data.add(new XYChart.Data(userCpuUsage, cpu.getUsedCpuUserChildren()));
                items.add(backgroundProcess);
                redisCPUTitledPane.setContent(root);
            }
        }
        //客户端信息
        {
            RedisClientInfo clients = info.getClients();
            ObservableList<TableColumn> columns = redisClientTable.getColumns();
            columns.stream().forEach(c -> c.setCellFactory(TextFieldTableCell.forTableColumn()));
            redisClientTableTableKey.setCellValueFactory(new PropertyValueFactory<>("key"));
            redisClientTableTableValue.setCellValueFactory(new PropertyValueFactory<>("value"));
            ObservableList items = redisClientTable.getItems();
            items.clear();
            items.add(new Pair<>("客户端连接数", clients.getConnectedClients()));
            items.add(new Pair<>("集群之间通讯使用的连接数", clients.getClusterConnections()));
            items.add(new Pair<>("最大客户端连接数", clients.getMaxclients()));
            items.add(new Pair<>("当前连接客户端中最长的输出列表", clients.getClientLongestOutputList()));
            items.add(new Pair<>("当前连接的客户端当中最大输入缓存", clients.getClientBiggestInputBuf()));
            items.add(new Pair<>("阻塞等待的客户端数量", clients.getBlockedClients()));
            items.add(new Pair<>("被跟踪的客户数量", clients.getTrackingClients()));
            items.add(new Pair<>("客户端超时表中的客户端数量", clients.getClientsInTimeoutTable()));
        }
        //server
        {
            RedisKeyspace keyspace = info.getKeyspace();
            redisStatsTableTableKey.setCellValueFactory((TreeTableColumn.CellDataFeatures<Object, String> param) -> {
                Object value = param.getValue().getValue();
                if (value instanceof Pair){
                    Pair<String, String> pair = (Pair<String, String>) value;
                    return new ReadOnlyStringWrapper(pair.getKey());
                }
                return new ReadOnlyStringWrapper(param.getValue().getValue().toString());
            });
            redisStatsTableTableValue.setCellValueFactory((TreeTableColumn.CellDataFeatures<Object, String> param) ->{
                Object value = param.getValue().getValue();
                if (value instanceof Pair){
                    Pair<String, String> pair = (Pair<String, String>) value;
                    return new ReadOnlyStringWrapper(pair.getValue());
                }
                return  new ReadOnlyStringWrapper(null);
            });
            ImageView depIcon = new ImageView (new Image(getClass().getResourceAsStream("/icon/db.jpg")));
            final TreeItem root = new TreeItem<>("Keyspace");//创建树形结构根节点选项
            redisStatsTable.setRoot(root);
            root.setExpanded(true);
            ObservableList<TreeItem<Pair<String, String>>> children = root.getChildren();
            children.clear();
            List<RedisKeyspace.RedisDB> db = keyspace.getDb();
            if (db != null){
                for (RedisKeyspace.RedisDB redisDB : db){
                    TreeItem secondaryNode = new TreeItem<>(redisDB.getName(), depIcon);//创建树形结构次级节点
                    secondaryNode.setExpanded(true);
                    TreeItem<Pair<String, String>> childNode1 = new TreeItem<>(new Pair<>("-Key的数量", String.valueOf(redisDB.getKeys())));//创建树形结构子节点
                    TreeItem<Pair<String, String>> childNode2 = new TreeItem<>(new Pair<>("-设置了过期时间的Key的个数", String.valueOf(redisDB.getExpires())));//创建树形结构子节点
                    TreeItem<Pair<String, String>> childNode3 = new TreeItem<>(new Pair<>("-过期时间的平均时长", String.valueOf(redisDB.getAvgTtl())));//创建树形结构子节点
                    secondaryNode.getChildren().addAll(childNode1, childNode2, childNode3);
                    children.add(secondaryNode);
                }
            }
        }
    }

    private static class RefreshTask implements Task{

        private RedisServerInfoController controller;

        public RefreshTask(RedisServerInfoController controller){
            this.controller = controller;
        }

        @Override
        public void run() {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    controller.refresh(controller.redisNode.getRedisClient());
                }
            });
            TaskProcessingCenter.submit(new RefreshTask(controller), 5, TimeUnit.SECONDS);
        }
    }
}
