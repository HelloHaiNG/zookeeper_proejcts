package com.ucar.zookeepernameservice.nameservice;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liaohong
 * @since 2018/12/13 10:42
 */
public class ZookeeperNameService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ZkClient zkClient = null;
    private int SESSIONTIMEOUT = 25000;
    private int CONNECTIONTIMEOUT = 25000;

    public ZookeeperNameService() {
    }

    public ZookeeperNameService(String ipAddress) {
        this.zkClient = new ZkClient(ipAddress, SESSIONTIMEOUT, CONNECTIONTIMEOUT, new SerializableSerializer());
    }

    public void start() {
        logger.info("ZookeeperNameService 服务启动，准备初始化...");
        init();
    }

    public void init() {
        logger.info("ZookeeperNameService 开始初始化...");
    }

    /**
     * 节点命名服务
     */
    public void nameService() {
        boolean exists = zkClient.exists("/nameService");
        if (!exists) {
            zkClient.createPersistent("/nameService");
        }
        String path = "/nameService/" + "name-";
        for (int i = 0; i < 5; i++) {
            String ephemeralSequential = zkClient.createEphemeralSequential(path, null);
            logger.info("创建{}节点", ephemeralSequential);
        }
    }

    public static void main(String[] args) {
        ZookeeperNameService nameService = new ZookeeperNameService("192.168.202.128:2181");
        nameService.start();
        nameService.nameService();
    }
}
