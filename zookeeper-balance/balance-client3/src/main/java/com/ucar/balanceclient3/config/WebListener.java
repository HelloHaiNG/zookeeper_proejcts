package com.ucar.balanceclient3.config;

import com.ucar.balanceclient3.balance.Client3;
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
        Client3 client3 = new Client3(ipAddress, "/servers");
        client3.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
