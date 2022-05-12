package com.raiden.redis.model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 9:24 2022/5/8
 * @Modified By:
 */
public class RedisClusterNodeInfo implements Comparable<RedisClusterNodeInfo>{

    private String id;
    private String hostAndPort;
    private String host;
    private int port;
    /**
     * 节点间通信的端口号
     */
    private int processCommunicationPort;
    /**
     * 是否自己
     */
    private boolean myself;
    /**
     * 是否Master
     */
    private boolean master;
    /**
     * 逗号分割的标记位，可能的值有: myself, master, slave, fail?, fail, handshake, noaddr, noflags.
     */
    private String[] flags;
    /**
     * 如果节点是slave，并且已知master节点，则这里列出master节点ID,否则的话这里列出”-“
     */
    private String masterId;
    /**
     * 最近一次发送ping的时间，这个时间是一个unix毫秒时间戳，0代表没有发送过
     */
    private long pingSent;
    /**
     * 最近一次收到pong的时间，使用unix时间戳表示
     */
    private long pingRecv;
    /**
     * 朝代值 选举用的 每当节点发生失败切换时，都会创建一个新的，独特的，递增的epoch。如果多个节点竞争同一个哈希槽时，epoch值更高的节点会抢夺到。
     */
    private int configEpoch;
    /**
     *  node-to-node集群总线使用的链接的状态，我们使用这个链接与集群中其他节点进行通信.值可以是 connected 和 disconnected
     */
    private String linkState;
    /**
     * 哈希槽值或者一个哈希槽范围. 从第9个参数开始，后面最多可能有16384个 数(limit never reached)。代表当前节点可以提供服务的所有哈希槽值。
     * 如果只是一个值,那就是只有一个槽会被使用。如果是一个范围，这个值表示为起始槽-结束槽，节点将处理包括起始槽和结束槽在内的所有哈希槽。
     */
    private List<RedisSlot> slots;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHostAndPort() {
        return hostAndPort;
    }

    public void setHostAndPort(String hostAndPort) {
        this.hostAndPort = hostAndPort;
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

    public int getProcessCommunicationPort() {
        return processCommunicationPort;
    }

    public void setProcessCommunicationPort(int processCommunicationPort) {
        this.processCommunicationPort = processCommunicationPort;
    }

    public boolean isMyself() {
        return myself;
    }

    public void setMyself(boolean myself) {
        this.myself = myself;
    }

    public boolean isMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }

    public String[] getFlags() {
        return flags;
    }

    public void setFlags(String[] flags) {
        this.flags = flags;
    }

    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public long getPingSent() {
        return pingSent;
    }

    public void setPingSent(long pingSent) {
        this.pingSent = pingSent;
    }

    public long getPingRecv() {
        return pingRecv;
    }

    public void setPingRecv(long pingRecv) {
        this.pingRecv = pingRecv;
    }

    public int getConfigEpoch() {
        return configEpoch;
    }

    public void setConfigEpoch(int configEpoch) {
        this.configEpoch = configEpoch;
    }

    public String getLinkState() {
        return linkState;
    }

    public void setLinkState(String linkState) {
        this.linkState = linkState;
    }

    public List<RedisSlot> getSlots() {
        return slots;
    }

    public void setSlots(List<RedisSlot> slots) {
        this.slots = slots;
    }

    public static final RedisClusterNodeInfo build(String[] data){
        if (data.length < 9){
            return null;
        }
        RedisClusterNodeInfo node = new RedisClusterNodeInfo();
        node.id = data[0];
        String hostAndPort = data[1];
        if (StringUtils.isNotBlank(hostAndPort)){
            node.hostAndPort = hostAndPort;
            int start = hostAndPort.indexOf(":");
            int end = hostAndPort.indexOf("@");
            if (start > -1){
                node.host = hostAndPort.substring(0, start);
                if (end > -1){
                    node.port = Integer.parseInt(hostAndPort.substring(start + 1, end));
                    node.processCommunicationPort = Integer.parseInt(hostAndPort.substring(end + 1));
                }else {
                    node.port = Integer.parseInt(hostAndPort.substring(start + 1));
                }
            }
        }
        String flags = data[2];
        if (StringUtils.isNotBlank(flags)){
            node.myself = flags.indexOf("myself") > -1;
            node.master = flags.indexOf("master") > -1;
            node.flags = StringUtils.split(data[2], ",");
        }
        node.masterId = data[3];
        node.pingSent = Long.parseLong(data[4]);
        node.pingRecv = Long.parseLong(data[5]);
        node.configEpoch = Integer.parseInt(data[6]);
        node.linkState = data[7];
        List<RedisSlot> slots = new ArrayList<>(data.length - 8);
        for (int i = 8; i < data.length; i++) {
            String slot = data[8];
            if (StringUtils.isBlank(slot)){
                continue;
            }
            slots.add(RedisSlot.build(slot));
        }
        node.slots = slots;
        return node;
    }

    @Override
    public String toString() {
        return "RedisClusterNode{" +
                "id='" + id + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", processCommunicationPort=" + processCommunicationPort +
                ", flags=" + Arrays.toString(flags) +
                ", masterId='" + masterId + '\'' +
                ", pingSent=" + pingSent +
                ", pingRecv=" + pingRecv +
                ", configEpoch=" + configEpoch +
                ", linkState='" + linkState + '\'' +
                ", slots=" + slots +
                '}';
    }

    @Override
    public int compareTo(RedisClusterNodeInfo o) {
        if (o.myself){
            return 1;
        }
        return myself ? -1 : this.hostAndPort.compareTo(o.hostAndPort);
    }
}
