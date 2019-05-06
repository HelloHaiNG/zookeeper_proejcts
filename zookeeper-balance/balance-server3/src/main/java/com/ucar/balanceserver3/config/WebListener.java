package com.ucar.balanceserver3.config;

import com.ucar.balancecommon.entity.ServerData;
import com.ucar.balanceserver3.server.WorkServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author liaohong
 * @since 2018/12/12 10:27
 */
@Component
public class WebListener implements ServletContextListener {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private String ipAddress = "192.168.202.128:2181,192.168.202.129:2181,192.168.202.130:2181";


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServerData serverData = new ServerData();
        serverData.setBalanceNum(0);
        serverData.setServerIp("192.168.202.130");
        serverData.setServerName("/WorkServer3");
        WorkServer workServer = new WorkServer(ipAddress, "/servers", serverData);
        workServer.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
