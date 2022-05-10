package com.raiden.redis.ui.page;

import com.raiden.redis.ui.mode.RedisNode;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public interface IPageService {

	Node generatePage(RedisNode redisNode, HBox root);
}
