package com.raiden.redis.ui.controller;

import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.net.client.RedisSentinelClient;
import com.raiden.redis.net.model.sentinel.RedisMaster;
import com.raiden.redis.net.model.sentinel.RedisSlave;
import com.raiden.redis.ui.DataPageView;
import com.raiden.redis.ui.common.Path;
import com.raiden.redis.ui.context.BeanContext;
import com.raiden.redis.ui.dao.RecordDao;
import com.raiden.redis.ui.mode.RedisNode;
import com.raiden.redis.ui.mode.RedisSentinelNode;
import com.raiden.redis.ui.mode.RedisSingleNode;
import com.raiden.redis.ui.tab.SentinelRedisInfoTabPane;
import com.raiden.redis.ui.util.FXMLLoaderUtils;
import com.raiden.redis.ui.util.WindowUtils;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 9:00 2022/5/14
 * @Modified By:
 */
public class RedisSentinelController extends AbstractRedisController {

    private static final Logger LOGGER = LogManager.getLogger(RedisSentinelController.class);

    @FXML
    private TextField sentinelHost;
    @FXML
    private TextField sentinelPort;

    public RedisSentinelController() {
        super(new RecordDao(Path.REDIS_SENTINEL_HISTORICAL_RECORD_DATA_PATH));
    }

    public void connectionRedisCluster(){
        String host = sentinelHost.getText();
        String port = sentinelPort.getText();
        RedisLoginController redisController = BeanContext.getBean(RedisLoginController.class.getName());
        if (StringUtils.isNoneBlank(host, port)){
            try {
                RedisSentinelNode sentinelNode = RedisSentinelNode.build(host, Integer.parseInt(port));
                RedisSentinelClient redisClient = (RedisSentinelClient) sentinelNode.getRedisClient();
                List<RedisMaster> redisMasters = redisClient.getMasters();
                if (redisMasters.isEmpty()){
                    return;
                }
                List<String> masterNames = redisMasters.stream().map(RedisMaster::getName).collect(Collectors.toList());
                //弹出选择 master 名称的列表
                TitledPane titledPane = FXMLLoaderUtils.getNode("sentinel/redis_sentinel_masters_list_view.fxml");
                ListView<String> listView = (ListView) titledPane.getContent().lookup("#masterList");
                listView.getItems().addAll(masterNames);
                Stage selectionList = WindowUtils.newWindow("选择列表", titledPane);
                //添加点击事件
                listView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) ->{
                    if (StringUtils.isBlank(newValue)) {
                        return;
                    }
                    //清理旧的数据
                    redisController.clear();
                    List<RedisNode> masterAddrByName = redisClient.getMasterAddrByName(newValue);
                    List<RedisNode> masters =  new ArrayList<>(masterAddrByName.size() + 1);
                    masters.add(sentinelNode);
                    for (RedisNode master : masterAddrByName){
                        if (isVerification.isSelected()){
                            //如果选了校验 就留下校验成功的
                            RedisClient client = master.getRedisClient();
                            if (!verification(client)){
                                continue;
                            }
                            master.setPassword(getPassword());
                        }
                        masters.add(master);
                    }
                    List<RedisSlave> slaves = redisClient.getSlaves(newValue);
                    for (RedisSlave slave : slaves){
                        RedisSingleNode slaveNode = RedisSingleNode.build(slave.getIp(), slave.getPort());
                        if (isVerification.isSelected()){
                            //如果选了校验 就留下校验成功的
                            RedisClient client = slaveNode.getRedisClient();
                            if (!verification(client)){
                                continue;
                            }
                            slaveNode.setPassword(getPassword());
                        }
                        masters.add(slaveNode);
                    }
                    //关闭弹窗
                    SentinelRedisInfoTabPane redisInfoTabPane = new SentinelRedisInfoTabPane();
                    Pane pane = redisInfoTabPane.createInstance(masters);
                    redisController.setShutDownCallback(() -> redisInfoTabPane.shutDown());
                    DataPageView dataPageView = new DataPageView(pane, () -> showLoginView());
                    dataPageView.start();
                    //设置关闭回调
                    selectionList.close();
                    //关闭登录界面
                    closeLoginView();
                });
                selectionList.showAndWait();
            }catch (Exception e){
                LOGGER.error(e.getMessage(), e);
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @Override
    protected String getHost() {
        return sentinelHost.getText();
    }

    @Override
    protected String getPort() {
        return sentinelPort.getText();
    }

    @Override
    protected void setHost(String host) {
        sentinelHost.setText(host);
    }

    @Override
    protected void setPort(String port) {
        sentinelPort.setText(port);
    }
}
