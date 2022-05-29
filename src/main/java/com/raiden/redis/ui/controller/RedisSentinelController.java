package com.raiden.redis.ui.controller;

import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.net.client.RedisSentinelClient;
import com.raiden.redis.net.client.RedisSingleClient;
import com.raiden.redis.ui.common.Path;
import com.raiden.redis.ui.dao.RecordDao;
import com.raiden.redis.ui.mode.RedisNode;
import com.raiden.redis.ui.mode.RedisSentinelNode;
import com.raiden.redis.ui.mode.RedisSingleNode;
import com.raiden.redis.ui.tab.SentinelRedisInfoTabPane;
import com.raiden.redis.ui.tab.SingleRedisInfoTabPane;
import com.raiden.redis.ui.util.RedisUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 9:00 2022/5/14
 * @Modified By:
 */
public class RedisSentinelController extends AbstractRedisController {

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
        RedisDataPageController redisController = BeanContext.getBean(RedisDataPageController.class.getName());
        if (StringUtils.isNoneBlank(host, port)){
            try {
                RedisSentinelNode sentinelNode = RedisSentinelNode.build(host, Integer.parseInt(port));
                RedisSentinelClient redisClient = (RedisSentinelClient) sentinelNode.getRedisClient();
//                redisClient.getMasters();
//                List<RedisNode> masters =  new ArrayList<>(masterAddrByName.size() + 1);
//                masters.add(sentinelNode);
//                for (RedisNode redisNode : redisClient.getMasterAddrByName(getName())){
//                    if (isVerification.isSelected()){
//                        //如果选了校验 就留下校验成功的
//                        RedisClient client = redisNode.getRedisClient();
//                        if (!verification(client)){
//                            continue;
//                        }
//                    }
//                    masters.add(redisNode);
//                }
                SentinelRedisInfoTabPane redisInfoTabPane = new SentinelRedisInfoTabPane();
                redisInfoTabPane.setRedisInfoTabPane(redisController.getRedisDataPage(), null);
            }catch (Exception e){
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
