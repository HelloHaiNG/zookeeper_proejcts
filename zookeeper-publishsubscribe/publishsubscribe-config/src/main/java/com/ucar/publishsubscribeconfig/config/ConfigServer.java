package com.ucar.publishsubscribeconfig.config;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author liaohong
 * @since 2018/12/11 15:54
 */
public class ConfigServer {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ZkClient zkClient = null;
    private String configRootPath;
    private String configServerPath;
    private String configJdbcPath;
    private String serverPath;
    private String commandPath;
    private int SESSIONTIMEOUT = 15000;
    private int CONNECTIONTIMEOUT = 15000;

    public ConfigServer() {
    }

    public ConfigServer(String ipAddress, String configRootPath, String configServerPath, String configJdbcPath,
                        String serverPath, String commandPath) {
        this.zkClient = new ZkClient(ipAddress, SESSIONTIMEOUT, CONNECTIONTIMEOUT, new BytesPushThroughSerializer());
        this.configRootPath = configRootPath;
        this.configServerPath = configServerPath;
        this.configJdbcPath = configJdbcPath;
        this.serverPath = serverPath;
        this.commandPath = commandPath;
    }

    public void start() {
        logger.info("publishsubscribe-config启动，准备初始化.....");
        init();
    }

    public void init() {
        logger.info("publishsubscribe-config 开始初始化.....");
        subscribeServer();
        subscribeCommand();
    }

    /**
     * 订阅工作服务器的节点变化
     */
    public void subscribeServer() {
        boolean exists = zkClient.exists(configRootPath);
        if (!exists) {
            zkClient.createPersistent(configRootPath);
        }
        boolean exists1 = zkClient.exists(configRootPath.concat(configServerPath));
        if (!exists1) {
            zkClient.createEphemeral(configRootPath.concat(configServerPath));
        }
        zkClient.subscribeChildChanges(serverPath, new IZkChildListener() {
            @Override
            public void handleChildChange(String s, List<String> list) throws Exception {
                logger.info("当前可以工作服务器节点信息是：{}", list.toString());
                zkClient.writeData(configRootPath.concat(configServerPath), list.toString().getBytes());
            }
        });
    }

    /**
     * 订阅配置服务器的节点变化
     */
    public void subscribeCommand() {
        boolean exists = zkClient.exists(configRootPath);
        if (!exists) {
            zkClient.createPersistent(configRootPath);
        }
        boolean exists1 = zkClient.exists(configRootPath.concat(configJdbcPath));
        if (!exists1) {
            zkClient.createEphemeral(configRootPath.concat(configJdbcPath));
        }
        zkClient.subscribeDataChanges(commandPath, new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {
                logger.info("节点：{}内容发送变化--》{}", s, zkClient.readData(s).toString());
                zkClient.writeData(configRootPath.concat(configJdbcPath), o.toString().getBytes());
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                logger.info("节点：{}被删除", s);
            }
        });
    }
}
