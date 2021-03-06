package com.raiden.redis.ui.controller.server;

import com.raiden.redis.net.model.*;
import com.raiden.redis.ui.util.PropertyValueUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 19:29 2022/5/18
 * @Modified By:
 */
public class RedisServerInfoController implements Initializable {
    @FXML
    private TableView redisServerTable;
    @FXML
    private TableColumn<Pair<String, String>, String> redisServerTableKey;
    @FXML
    private TableColumn<Pair<String, String>, String> redisServerTableValue;
    @FXML
    private TableView redisStatsTable;
    @FXML
    private TableColumn<Pair<String, Number>, String> redisStatsTableKey;
    @FXML
    private TableColumn<Pair<String, Number>, String> redisStatsTableValue;
    @FXML
    private TableView redisClientTable;
    @FXML
    private TableColumn<Pair<String, String>, String> redisClientTableTableKey;
    @FXML
    private TableColumn<Pair<String, String>, String> redisClientTableTableValue;
    @FXML
    private TreeTableView redisKeyspaceTable;
    @FXML
    private TreeTableColumn<Object, String> redisStatsTableTableKey;
    @FXML
    private TreeTableColumn<Object, String> redisStatsTableTableValue;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //server
        redisServerTableKey.setCellValueFactory(PropertyValueUtil.getPairKeyPropertyValueFactory());
        redisServerTableValue.setCellValueFactory(PropertyValueUtil.getPairValuePropertyValueFactory());
        //stats: 一般统计
        redisStatsTableKey.setCellValueFactory(PropertyValueUtil.getPairKeyPropertyValueFactory());
        redisStatsTableValue.setCellValueFactory(PropertyValueUtil.getPairValuePropertyValueFactory());
        //客户端信息
        redisClientTableTableKey.setCellValueFactory(PropertyValueUtil.getPairKeyPropertyValueFactory());
        redisClientTableTableValue.setCellValueFactory(PropertyValueUtil.getPairValuePropertyValueFactory());
        //server
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
    }


    public void refresh(RedisNodeInfo info){
        //server
        {
            RedisServerInfo server = info.getServer();
            if (server != null){
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
        //stats: 一般统计
        {
            RedisStats stats = info.getStats();
            if (stats != null){
                ObservableList<Pair<String, Number>> items = redisStatsTable.getItems();
                items.clear();
                items.add(new Pair<>("由于最大内存限制被淘汰的key的数量", stats.getEvictedKeys()));
                items.add(new Pair<>("拥有客户端订阅的全局pub/sub通道数", stats.getPubsubChannels()));
                items.add(new Pair<>("拥有客户端订阅的全局pub/sub模式数", stats.getPubsubPatterns()));
                items.add(new Pair<>("上一次fork操作的持续时间(微秒)", stats.getLatestForkUsec()));
                items.add(new Pair<>("主从完全同步成功次数", stats.getSyncFull()));
                items.add(new Pair<>("主从部分同步成功次数", stats.getSyncPartialOk()));
                items.add(new Pair<>("主从部分同步失败次数", stats.getSyncPartialErr()));
            }
        }
        //客户端信息
        {
            RedisClientInfo clients = info.getClients();
            if (clients != null){
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
        }
        //server
        {
            RedisKeyspace keyspace = info.getKeyspace();
            if (keyspace != null){
                final TreeItem root = new TreeItem<>("Keyspace");//创建树形结构根节点选项
                redisKeyspaceTable.setRoot(root);
                root.setExpanded(true);
                ObservableList<TreeItem<Pair<String, String>>> children = root.getChildren();
                children.clear();
                List<RedisKeyspace.RedisDB> db = keyspace.getDb();
                if (db != null){
                    for (RedisKeyspace.RedisDB redisDB : db){
                        TreeItem secondaryNode = new TreeItem<>(redisDB.getName(), new ImageView (new Image(getClass().getResourceAsStream("/icon/db.jpg"))));//创建树形结构次级节点
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
    }
}
