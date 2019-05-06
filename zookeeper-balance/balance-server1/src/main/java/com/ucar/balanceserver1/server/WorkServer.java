package com.ucar.balanceserver1.server;

import com.ucar.balancecommon.entity.ServerData;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liaohong
 * @since 2018/12/12 10:19
 */
public class WorkServer {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ZkClient zkClient = null;
    private String serverPath;
    private int SESSIONTIMEOUT = 15000;
    private int CONNECTIONTIMEOUT = 15000;
    private ServerData serverData;

    public WorkServer() {
    }

    public WorkServer(String ipAddress, String serverPath, ServerData serverData) {
        this.zkClient = new ZkClient(ipAddress, SESSIONTIMEOUT, CONNECTIONTIMEOUT, new SerializableSerializer());
        this.serverPath = serverPath;
        this.serverData = serverData;
    }

    public void start() {
        logger.info("balance-server1启动，准备初始化....");
        init();
    }

    public void init() {
        logger.info("balance-server1 开始初始化....");
        boolean exists = zkClient.exists(serverPath);
        if (!exists) {
            zkClient.createPersistent(serverPath);
            logger.info("创建{}持久节点成功", serverPath);
        }
        boolean exists1 = zkClient.exists(serverPath.concat(serverData.getServerName()));
        if (!exists1) {
            zkClient.createEphemeral(serverPath.concat(serverData.getServerName()), serverData);
            logger.info("创建{}临时节点成功", serverPath.concat(serverData.getServerName()));
        }
        zkClient.subscribeDataChanges(serverPath.concat(serverData.getServerName()), new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {
                logger.info("节点{}数据发生改变...", s);
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                logger.info("节点{}被删除.....", s);
            }
        });
    }
}
