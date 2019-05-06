package com.ucar.zookeeperqueue.queue;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author liaohong
 * @since 2018/12/13 10:08
 */
public class ZookeeperQueue {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ZkClient zkClient = null;
    private int SESSIONTIMEOUT = 25000;
    private int CONNECTIONTIMEOUT = 25000;

    public ZookeeperQueue() {
    }

    public ZookeeperQueue(String ipAddress) {
        this.zkClient = new ZkClient(ipAddress, SESSIONTIMEOUT, CONNECTIONTIMEOUT, new SerializableSerializer());
    }

    public void start() {
        logger.info("ZookeeperQueue 服务启动，准备初始化...");
        init();
    }

    public void init() {
        logger.info("ZookeeperQueue 开始初始化...");
    }

    /**
     * 入队
     */
    public void offer(String nodeData) {
        boolean exists = zkClient.exists("/queue");
        if (!exists) {
            logger.info("创建{}节点", "/queue");
            zkClient.createPersistent("/queue");
        }
        String path = "/queue" + "/queue-";
        String ephemeralSequential = zkClient.createEphemeralSequential(path, nodeData);
        logger.info("{}创建{}队列节点", nodeData, ephemeralSequential);
    }

    public void poll() {
        //得到"/queue"的子节点
        List<String> children = zkClient.getChildren("/queue");
        //升序排序
        Collections.sort(children, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                o1 = StringUtils.substringAfter(o1, "-");
                o2 = StringUtils.substringAfter(o2, "-");
                int num1 = Integer.parseInt(o1);
                int num2 = Integer.parseInt(o2);
                if (num1 > num2) {
                    return 1;
                } else if (num1 < num2) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        logger.info("节点{}的子节点有：{}", "/queue", children.toString());
        for (String string : children) {
            String path = "/queue/".concat(string);
            String data = zkClient.readData(path).toString();
            logger.info("节点：{}出队，信息为：{}", path, data);
            zkClient.delete(path);
        }
        logger.info("队列节点{}完成出队", "/queue");
    }

}
