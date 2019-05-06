package com.ucarzookeeper.core.service.impl;

import com.ucarzookeeper.core.service.ServiceRegistry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

/**
 * @author liaohong
 * @since 2018/12/10 10:51
 */
@Service
public class ServiceRegistryImpl implements ServiceRegistry, Watcher {

    private static Logger logger = LoggerFactory.getLogger(ServiceRegistryImpl.class);
    private static CountDownLatch latch = new CountDownLatch(1);
    private ZooKeeper zk;
    private static final int SESSION_TIMEOUT = 15000;

    public ServiceRegistryImpl() {
    }

    public ServiceRegistryImpl(String zkServers) {
        try {
            zk = new ZooKeeper(zkServers, SESSION_TIMEOUT, this);
            latch.await();
            logger.debug("connected to zookeeper");
        } catch (Exception ex) {
            logger.error("create zookeeper client failure", ex);
        }
    }

    private static final String REGISTRY_PATH = "/registry";

    @Override
    public void register(String serviceName, String workServer) {
        try {
            if (serviceName.equals("master")) {
                logger.info("创建master节点");
                if (zk.exists("/master", false) == null) {
                    zk.create("/master", workServer.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                }
            } else {
                logger.info("创建slave节点");
                String registryPath = REGISTRY_PATH;
                if (zk.exists(registryPath, false) == null) {
                    zk.create(registryPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                    logger.debug("create registry node:{}", registryPath);
                }
                //创建服务节点（临时性节点）
                String servicePath = registryPath + "/" + serviceName;
                if (zk.exists(servicePath, false) == null) {
                    zk.create(servicePath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                    logger.debug("create service node:{}", servicePath);
                }

                //创建地址节点
                String addressPath = servicePath + "/address-";
                String addressNode = zk.create(addressPath, workServer.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
                logger.debug("create address node:{} => {}", addressNode, workServer);
            }
        } catch (Exception e) {
            logger.error("create node failure", e);
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected)
            latch.countDown();
    }
}
