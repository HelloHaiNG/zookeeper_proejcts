package com.ucar.publishsubscribeserver3.server;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liaohong
 * @since 2018/12/11 16:23
 */
public class WorkServer {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ZkClient zkClient = null;
    private String serverPath;
    private String serverData;
    private int SESSIONTIMEOUT = 15000;
    private int CONNECTIONTIMEOUT = 15000;
    private String ownServerPath;

    public WorkServer() {
    }

    public WorkServer(String ipAddress, String serverPath, String ownServerPath) {
        this.zkClient = new ZkClient(ipAddress, SESSIONTIMEOUT, CONNECTIONTIMEOUT, new BytesPushThroughSerializer());
        this.serverPath = serverPath;
        this.ownServerPath = ownServerPath;
    }

    public void start() {
        logger.info("publishsubscribe-server3启动，准备初始化....");
        init();
    }

    public void init() {
        logger.info("publishsubscribe-server3 初始化........");
        boolean exists = zkClient.exists(serverPath);
        if (!exists) {
            zkClient.createPersistent(serverPath);
        }
        boolean exists1 = zkClient.exists(serverPath.concat(ownServerPath));
        if (!exists1) {
            zkClient.createEphemeral(serverPath.concat(ownServerPath),"WorkServer3".getBytes());
        }
        zkClient.subscribeDataChanges("/config/jdbcInfo", new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {
                logger.info("数据库信息发生改变");
                logger.info("当前数据库信息为:{}", zkClient.readData(s).toString());
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {

            }
        });
    }
}
