package com.ucar.balanceclient2.config;

import com.ucar.balanceclient2.balance.Client2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author liaohong
 * @since 2018/12/12 11:20
 */
@Component
public class WebListener implements ServletContextListener {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private String ipAddress = "192.168.202.128:2181,192.168.202.129:2181,192.168.202.130:2181";


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Client2 client2 = new Client2(ipAddress, "/servers");
        client2.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
