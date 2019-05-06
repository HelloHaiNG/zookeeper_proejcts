package com.ucar.masterclient1.server.impl;

import com.ucar.masterclient1.server.MasterService;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author liaohong
 * @since 2018/12/10 15:23
 */
@Service
public class MasterServiceImpl implements MasterService, Watcher {

    private static Logger logger = LoggerFactory.getLogger(MasterServiceImpl.class);
    private static CountDownLatch latch = new CountDownLatch(1);
    private ZooKeeper zk;
    private static final int SESSION_TIMEOUT = 15000;

    private ExecutorService executorService = Executors.newFixedThreadPool(3);
    private String masterData = null;

    public MasterServiceImpl() {
    }

    public MasterServiceImpl(String zkServers) {
        try {
            zk = new ZooKeeper(zkServers, SESSION_TIMEOUT, this);
            latch.await();
            logger.debug("connected to zookeeper");
        } catch (Exception ex) {
            logger.error("create zookeeper client failure", ex);
        }
    }

    @Override
    public void master() {
        masterData = "WorkServer1";
        logger.info("开始监听........");
        Task task = new Task();
        executorService.execute(task);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected)
            latch.countDown();
    }

    private class Task implements Runnable {

        @Override
        public void run() {
            try {
                zk.exists("/master", new Watcher() {
                    @Override
                    public void process(WatchedEvent watchedEvent) {
                        if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
                            logger.info("exists中监听到..." + watchedEvent.getPath() + "...删除了");
                            try {
                                zk.create("/master", masterData.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                                logger.info("{}成功创建master节点", masterData);
                                logger.info("删除节点/registry/slave/address-0000000013");
                                zk.delete("/registry/slave/address-0000000013", 0);
                            } catch (ZkNodeExistsException e) {
                                logger.info("master节点已经被创建");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (KeeperException e) {
                                e.printStackTrace();
                            } finally {
                            }
                        }
                    }
                });
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

//    private class ZkDataListener implements IZkDataListener {
//
//        @Override
//        public void handleDataChange(String s, Object o) throws Exception {
//        }
//
//        @Override
//        public void handleDataDeleted(String s) throws Exception {
//            //重新选举master
//            logger.info("重新选举master");
//            try {
//                zk.create("/master", masterData.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
//                logger.info("{}成功创建master节点", masterData);
//                zk.delete("/registry/slave/address-0000000007", 0);
//            } catch (ZkNodeExistsException e) {
//                logger.info("master节点已经被创建");
//            } finally {
//            }
//        }
//    }
}
