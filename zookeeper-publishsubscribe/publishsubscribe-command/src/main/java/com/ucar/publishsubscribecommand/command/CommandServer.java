package com.ucar.publishsubscribecommand.command;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author liaohong
 * @since 2018/12/11 15:12
 */
public class CommandServer {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ZkClient zkClient = null;
    private String commandPath;
    private String commandData;
    private int SESSIONTIMEOUT = 15000;
    private int CONNECTIONTIMEOUT = 15000;

    public CommandServer() {
    }

    public CommandServer(String ipAddress, String commandPath) {
        this.zkClient = new ZkClient(ipAddress, SESSIONTIMEOUT, CONNECTIONTIMEOUT, new BytesPushThroughSerializer());
        this.commandPath = commandPath;
    }

    public void start() {
        logger.info("publishsubscribe-command 服务开始启动，准备初始化....");
        init();
    }

    public void init() {
        logger.info("publishsubscribe-command 开始初始化.......");
        boolean exists = zkClient.exists(commandPath);
        try {
            if (!exists) {
                commandData = readProperties();
                zkClient.createPersistent(commandPath, commandData.getBytes());
            } else {
                commandData = readProperties();
                String readData = zkClient.readData(commandPath).toString();
                if (!commandData.equals(readData)) {
                    zkClient.writeData(commandPath, commandData.getBytes());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readProperties() throws IOException {
        InputStream inputStream = CommandServer.class.getClassLoader()
                .getResourceAsStream("db.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        String driverName = properties.getProperty("jdbc.driverName");
        String url = properties.getProperty("jdbc.url");
        String username = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");
        String commandData = driverName.concat("&").concat(url).concat("&").concat(username).concat("&").concat(password);
        return commandData;
    }

}
