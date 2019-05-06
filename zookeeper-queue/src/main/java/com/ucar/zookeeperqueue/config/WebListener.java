package com.ucar.zookeeperqueue.config;

import com.ucar.zookeeperqueue.queue.ZookeeperQueue;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author liaohong
 * @since 2018/12/13 10:23
 */
@Component
public class WebListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ZookeeperQueue zookeeperQueue = new ZookeeperQueue("192.168.202.128:2181");
        zookeeperQueue.start();
        String nodaData1 = "nodaData1";
        String nodaData2 = "nodaData2";
        String nodaData3 = "nodaData3";
        zookeeperQueue.start();
        //生产者入队
        zookeeperQueue.offer(nodaData1);
        zookeeperQueue.offer(nodaData2);
        zookeeperQueue.offer(nodaData3);
        try{
            Thread.sleep(10000);
        }catch (Exception e) {
            e.printStackTrace();
        }
        //消费者出队
        zookeeperQueue.poll();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
