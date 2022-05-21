package com.raiden.redis.ui.controller;

import com.raiden.redis.net.model.*;
import com.raiden.redis.ui.util.FXMLLoaderUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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

    private AtomicReference<RedisNodeInfo> beforeInfo;

    public RedisServerInfoController(){
        this.beforeInfo = new AtomicReference<>(null);
    }


    public void refresh(RedisNodeInfo info){
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
            /**
             * redis进程单cpu的消耗率可以通过如下公式计算:
             * ((used_cpu_sys_now-used_cpu_sys_before)/(now-before))*100
             * used_cpu_sys_now : now时间点的used_cpu_sys值
             * used_cpu_sys_before : before时间点的used_cpu_sys值
             */
            AnchorPane root = FXMLLoaderUtils.getNode("redis_cpu_bar_chart.fxml");
            if (root != null){
                RedisNodeInfo beforeRedisNodeInfo = beforeInfo.get();
                if (beforeRedisNodeInfo == null){
                    beforeInfo.compareAndSet(beforeRedisNodeInfo, info);
                }else {
                    long beforeTimeStamp = beforeRedisNodeInfo.getTimeStamp();
                    long nowTimeStamp = info.getTimeStamp();
                    RedisCpuInfo beforeCpu = beforeRedisNodeInfo.getCpu();
                    BarChart redisCPUBarChart = (BarChart) root.lookup("#redisCPUBarChart");
                    String systemCpuUsage = "系统CPU使用率";
                    String userCpuUsage = "用户CPU使用率";
                    RedisCpuInfo cpu = info.getCpu();
                    ObservableList<XYChart.Series<String, Double>> items = redisCPUBarChart.getData();
                    items.clear();
                    //Redis服务
                    XYChart.Series<String,Double> series = new XYChart.Series<>();
                    ObservableList<XYChart.Data<String, Double>> data = series.getData();
                    series.setName("Redis主进程");
                    double usedCpuSys = usageRate(cpu.getUsedCpuSys(), beforeCpu.getUsedCpuSys(), nowTimeStamp, beforeTimeStamp);
                    double usedCpuUser = usageRate(cpu.getUsedCpuUser(), beforeCpu.getUsedCpuUser(), nowTimeStamp, beforeTimeStamp);
                    data.add(new XYChart.Data(systemCpuUsage, usedCpuSys));
                    data.add(new XYChart.Data(userCpuUsage, usedCpuUser));
                    items.add(series);

                    //Redis主线程
                    XYChart.Series<String,Double> mainThread = new XYChart.Series<>();
                    data = mainThread.getData();
                    mainThread.setName("Redis主线程");
                    double usedCpuSysMainThread = usageRate(cpu.getUsedCpuSysMainThread(), beforeCpu.getUsedCpuSysMainThread(), nowTimeStamp, beforeTimeStamp);
                    double usedCpuUserMainThread = usageRate(cpu.getUsedCpuUserMainThread(), beforeCpu.getUsedCpuUserMainThread(), nowTimeStamp, beforeTimeStamp);
                    data.add(new XYChart.Data(systemCpuUsage, usedCpuSysMainThread));
                    data.add(new XYChart.Data(userCpuUsage, usedCpuUserMainThread));
                    items.add(mainThread);

                    //后台进程
                    XYChart.Series<String,Double> backgroundProcess = new XYChart.Series<>();
                    data = backgroundProcess.getData();
                    backgroundProcess.setName("Redis后台进程");
                    double usedCpuSysChildren = usageRate(cpu.getUsedCpuSysChildren(), beforeCpu.getUsedCpuSysChildren(), nowTimeStamp, beforeTimeStamp);
                    double usedCpuUserChildren = usageRate(cpu.getUsedCpuUserChildren(), beforeCpu.getUsedCpuUserChildren(), nowTimeStamp, beforeTimeStamp);
                    data.add(new XYChart.Data(systemCpuUsage, usedCpuSysChildren));
                    data.add(new XYChart.Data(userCpuUsage, usedCpuUserChildren));
                    items.add(backgroundProcess);
                    redisCPUTitledPane.setContent(root);
                    beforeInfo.compareAndSet(beforeRedisNodeInfo, info);
                }
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

    private double usageRate(double now,double before,long nowTimeStamp,long beforeTimeStamp){
        return (now - before) / (nowTimeStamp - beforeTimeStamp) * 100D;
    }
}
