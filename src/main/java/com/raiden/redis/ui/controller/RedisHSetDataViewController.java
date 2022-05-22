package com.raiden.redis.ui.controller;

import com.raiden.redis.net.client.RedisClient;
import com.raiden.redis.ui.mode.RedisNode;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 20:04 2022/5/22
 * @Modified By:
 */
public class RedisHSetDataViewController implements Controller {

    @FXML
    private TableView tableView;

    private RedisNode redisNode;

    @Override
    public void setRedisNode(RedisNode redisNode) {
        this.redisNode = redisNode;
    }

    @Override
    public void init(String key) {
        if (redisNode != null){
            RedisClient redisClient = redisNode.getRedisClient();

        }
    }
}
