package com.ucar.zookeeperlock.config;

import com.ucar.zookeeperlock.lock.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author liaohong
 * @since 2018/12/12 17:01
 */
@Component
public class WebListener implements ServletContextListener {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private String ipAddress = "192.168.202.128:2181,192.168.202.129:2181,192.168.202.130:2181";


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Lock lock = new Lock("192.168.202.128:2181");
        lock.start();
        String client1 = "client1";
        String client2 = "client2";
        String client3 = "client3";
        String nodePath1 = lock.create(client1);
        String nodePath2 = lock.create(client2);
        String nodePath3 = lock.create(client3);
        lock.lock(nodePath1);
        lock.lock(nodePath2);
        lock.lock(nodePath3);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
