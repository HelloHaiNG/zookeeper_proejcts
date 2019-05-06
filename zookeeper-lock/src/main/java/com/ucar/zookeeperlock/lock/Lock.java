package com.ucar.zookeeperlock.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author liaohong
 * @since 2018/12/12 16:07
 */
public class Lock {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ZkClient zkClient = null;
    private int SESSIONTIMEOUT = 25000;
    private int CONNECTIONTIMEOUT = 25000;

    public Lock() {
    }

    public Lock(String ipAddress) {
        this.zkClient = new ZkClient(ipAddress, SESSIONTIMEOUT, CONNECTIONTIMEOUT, new SerializableSerializer());
    }

    public void start() {
        logger.info("Lock 服务启动，准备初始化...");
        init();
    }

    public void init() {
        logger.info("Lock 开始初始化...");
    }

    /**
     * 创建节点
     *
     * @param data
     */
    public String create(String data) {
        boolean exists = zkClient.exists("/lock");
        if (!exists) {
            zkClient.createPersistent("/lock");
        }
        String path = "/lock".concat("/node-");
        String nodePath = zkClient.createEphemeralSequential(path, data);
        logger.info("{}创建{}节点，节点数据是：{}", data, nodePath, data);
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nodePath;
    }

    /**
     * 获取锁
     *
     * @param nodePath
     */
    public void lock(String nodePath) {
        List<String> list = zkClient.getChildren("/lock");
        //升序排序
        Collections.sort(list, new Comparator<String>() {
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
        logger.info("{}:节点{}的子节点有：{}", nodePath, "/lock", list.toString());
        String firtNodePath = "/lock".concat("/").concat(list.get(0));
        if (nodePath.equals(firtNodePath)) {
            String data = zkClient.readData(nodePath);
            logger.info("{}获取了锁", data);
            try {
                Thread.sleep(10000);
                release(nodePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String data = zkClient.readData(nodePath);
            logger.info("{}等待获取锁", data);
            //获取上一个节点的位置信息
            String nodePath1 = StringUtils.substringAfterLast(nodePath, "/");
            int temp = 0;
            for (String string : list) {
                if (!string.equals(nodePath1)) {
                    temp++;
                }
            }
            String previousNodePath = "/lock".concat("/").concat(list.get(temp - 1));
            zkClient.subscribeDataChanges(previousNodePath, new IZkDataListener() {
                @Override
                public void handleDataChange(String s, Object o) throws Exception {

                }

                @Override
                public void handleDataDeleted(String s) throws Exception {
                    logger.info("{}节点释放了锁", s);
                    zkClient.unsubscribeDataChanges(s, new IZkDataListener() {
                        @Override
                        public void handleDataChange(String s, Object o) throws Exception {

                        }

                        @Override
                        public void handleDataDeleted(String s) throws Exception {

                        }
                    });
                    lock(nodePath);
                }
            });
        }
    }

    /**
     * 释放锁
     *
     * @param nodePath
     */
    public void release(String nodePath) {
        logger.info("{}释放锁", nodePath);
        zkClient.delete(nodePath);
    }
}
