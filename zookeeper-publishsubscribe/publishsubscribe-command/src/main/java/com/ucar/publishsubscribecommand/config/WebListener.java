package com.ucar.publishsubscribecommand.config;

import com.ucar.publishsubscribecommand.command.CommandServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author liaohong
 * @since 2018/12/11 15:45
 */
@Component
public class WebListener implements ServletContextListener {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private String ipAddress = "192.168.202.128:2181,192.168.202.129:2181,192.168.202.130:2181";


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        CommandServer commandServer = new CommandServer(ipAddress,"/command");
        commandServer.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

}
