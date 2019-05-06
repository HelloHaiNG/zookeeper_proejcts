package com.ucar.publishsubscribeconfig.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author liaohong
 * @since 2018/12/11 16:17
 */
@Component
public class WebListener implements ServletContextListener {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private String ipAddress = "192.168.202.128:2181,192.168.202.129:2181,192.168.202.130:2181";


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ConfigServer configServer = new ConfigServer(ipAddress, "/config", "/workServer", "/jdbcInfo", "/servers",
                "/command");
        configServer.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
