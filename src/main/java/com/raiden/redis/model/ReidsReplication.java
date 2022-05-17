package com.raiden.redis.model;

import java.util.List;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 19:35 2022/5/15
 * @Modified By:
 */
public class ReidsReplication {
    /**
     * 如果实例不是任何节点的从节点，则值是”master”，如果实例从某个节点同步数据，则是”slave”。 请注意，一个从节点可以是另一个从节点的主节点（菊花链）
     */
    private String role;
    /**
     * 主节点的Host名称或IP地址 (从节点才有)
     */
    private String masterHost;
    /**
     * 主节点监听的TCP端口 (从节点才有)
     */
    private int masterPort;
    /**
     * 已连接的从节点数
     */
    private int connectedSlaves;
    /**
     * 连接状态（up或者down）(从节点才有)
     */
    private String masterLinkStatus;
    /**
     * 自上次与主节点交互以来，经过的秒数 (从节点才有)
     */
    private int masterLastIoSecondsAgo;
    /**
     * 指示主节点正在与从节点同步
     */
    private int masterSyncInProgress;
    /**
     * 如果主从节点之间的连接断开了，则会提供一个额外的字段：
     */
    private int masterLinkDownSinceSeconds;
    /**
     * 从节点
     */
    /**
     * 如果主从节点之间的连接断开了，则会提供一个额外的字段：
     */
    private List<RedisSlave> slave;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMasterHost() {
        return masterHost;
    }

    public void setMasterHost(String masterHost) {
        this.masterHost = masterHost;
    }

    public int getMasterPort() {
        return masterPort;
    }

    public void setMasterPort(int masterPort) {
        this.masterPort = masterPort;
    }

    public int getConnectedSlaves() {
        return connectedSlaves;
    }

    public void setConnectedSlaves(int connectedSlaves) {
        this.connectedSlaves = connectedSlaves;
    }

    public String getMasterLinkStatus() {
        return masterLinkStatus;
    }

    public void setMasterLinkStatus(String masterLinkStatus) {
        this.masterLinkStatus = masterLinkStatus;
    }

    public int getMasterLastIoSecondsAgo() {
        return masterLastIoSecondsAgo;
    }

    public void setMasterLastIoSecondsAgo(int masterLastIoSecondsAgo) {
        this.masterLastIoSecondsAgo = masterLastIoSecondsAgo;
    }

    public int getMasterSyncInProgress() {
        return masterSyncInProgress;
    }

    public void setMasterSyncInProgress(int masterSyncInProgress) {
        this.masterSyncInProgress = masterSyncInProgress;
    }

    public int getMasterLinkDownSinceSeconds() {
        return masterLinkDownSinceSeconds;
    }

    public void setMasterLinkDownSinceSeconds(int masterLinkDownSinceSeconds) {
        this.masterLinkDownSinceSeconds = masterLinkDownSinceSeconds;
    }

    public List<RedisSlave> getSlave() {
        return slave;
    }

    public void setSlave(List<RedisSlave> slave) {
        this.slave = slave;
    }

    public static class RedisSlave{
        private String name;
        private String ip;
        private int port;
        private String state;
        private String offset;
        private int lag;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getOffset() {
            return offset;
        }

        public void setOffset(String offset) {
            this.offset = offset;
        }

        public int getLag() {
            return lag;
        }

        public void setLag(int lag) {
            this.lag = lag;
        }

        @Override
        public String toString() {
            return "RedisSlave{" +
                    "ip='" + ip + '\'' +
                    ", port=" + port +
                    ", state='" + state + '\'' +
                    ", offset='" + offset + '\'' +
                    ", lag=" + lag +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ReidsReplication{" +
                "role='" + role + '\'' +
                ", masterHost='" + masterHost + '\'' +
                ", masterPort=" + masterPort +
                ", connectedSlaves=" + connectedSlaves +
                ", masterLinkStatus='" + masterLinkStatus + '\'' +
                ", masterLastIoSecondsAgo=" + masterLastIoSecondsAgo +
                ", masterSyncInProgress=" + masterSyncInProgress +
                ", masterLinkDownSinceSeconds=" + masterLinkDownSinceSeconds +
                ", slave=" + slave +
                '}';
    }
}
