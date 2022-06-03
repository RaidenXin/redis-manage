package com.raiden.redis.ui.controller;

import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.net.client.RedisSingleClient;
import com.raiden.redis.ui.common.Path;
import com.raiden.redis.ui.context.BeanContext;
import com.raiden.redis.ui.dao.RecordDao;
import com.raiden.redis.ui.mode.RedisSingleNode;
import com.raiden.redis.ui.tab.SingleRedisInfoTabPane;
import com.raiden.redis.ui.util.RedisUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.commons.lang3.StringUtils;
/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 8:56 2022/5/14
 * @Modified By:
 */
public class RedisSinglePointController extends AbstractRedisController {


    @FXML
    private TextField singlePointHost;
    @FXML
    private TextField singlePointPort;

    public RedisSinglePointController() {
        super(new RecordDao(Path.REDIS_SINGLE_HISTORICAL_RECORD_DATA_PATH));
    }

    public void connectionRedis(){
        String host = singlePointHost.getText();
        String port = singlePointPort.getText();
        RedisDataPageController redisController = BeanContext.getBean(RedisDataPageController.class.getName());
        if (StringUtils.isNoneBlank(host, port)){
            try {
                RedisSingleNode redisSingleNode = RedisSingleNode.build(host, Integer.parseInt(port));
                RedisClient redisClient = redisSingleNode.getRedisClient();
                if (isVerification.isSelected()){
                    //验证不成功停止执行
                    if (!verification(redisClient)){
                        return;
                    }
                    redisSingleNode.setPassword(getPassword());
                }
                SingleRedisInfoTabPane redisInfoTabPane = new SingleRedisInfoTabPane();
                redisInfoTabPane.setRedisInfoTabPane(redisController.getRedisDataPage(), redisSingleNode);
            }catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @Override
    protected String getHost() {
        return singlePointHost.getText();
    }

    @Override
    protected String getPort() {
        return singlePointPort.getText();
    }

    @Override
    protected void setHost(String host) {
        singlePointHost.setText(host);
    }

    @Override
    protected void setPort(String port) {
        singlePointPort.setText(port);
    }
}
