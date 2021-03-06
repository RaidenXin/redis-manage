package com.raiden.redis.ui.mode;

import com.raiden.redis.net.common.Separator;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.lang3.StringUtils;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 18:54 2022/5/14
 * @Modified By:
 */
public class Record {
    private String name;
    private String host;
    private int port;
    private String password;
    private AnchorPane operation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AnchorPane getOperation() {
        return operation;
    }

    public void setOperation(Button... operation) {
        this.operation = new AnchorPane(operation);
        this.operation.setPrefWidth(94.0D);
    }

    public static final Record build(String data){
        if (StringUtils.isBlank(data)){
            return null;
        }
        String[] split = StringUtils.split(data, Separator.VERTICAL_BAR);
        if (split.length != 4){
            return null;
        }
        Record record = new Record();
        record.name = split[0].substring(5);
        record.host = split[1].substring(5);
        record.port = Integer.parseInt(split[2].substring(5));
        record.password = split[3].substring(9);
        return record;
    }

    @Override
    public String toString() {
        return "name:" + name + Separator.VERTICAL_BAR + "host:" + host + Separator.VERTICAL_BAR + "port:" + port + Separator.VERTICAL_BAR + "password:" + password;
    }
}
