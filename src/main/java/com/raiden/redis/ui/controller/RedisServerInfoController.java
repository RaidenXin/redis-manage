package com.raiden.redis.ui.controller;

import com.raiden.redis.client.RedisClient;
import com.raiden.redis.model.RedisClientInfo;
import com.raiden.redis.model.RedisCpuInfo;
import com.raiden.redis.model.RedisNodeInfo;
import com.raiden.redis.model.RedisServerInfo;
import com.raiden.redis.ui.mode.RedisNode;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Pair;

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
    private BarChart<String,Double> redisCPUBarChart;
    @FXML
    private TableView redisClientTable;
    @FXML
    private TableColumn<Pair<String, String>, String> redisClientTableTableKey;
    @FXML
    private TableColumn<Pair<String, String>, String> redisClientTableTableValue;
    @FXML
    private TableView redisStatsTable;
    @FXML
    private TableColumn<Pair<String, String>, String> redisStatsTableTableKey;
    @FXML
    private TableColumn<Pair<String, String>, String> redisStatsTableTableValue;

    private RedisNode redisNode;


    public void init(RedisNode redisNode){
        if (redisNode != null){
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
    }
}
