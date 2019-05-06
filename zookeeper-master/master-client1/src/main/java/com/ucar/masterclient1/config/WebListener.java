package com.ucar.masterclient1.config;

import com.ucar.masterclient1.server.MasterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author liaohong
 * @since 2018/12/10 11:14
 */
@Component
public class WebListener implements ServletContextListener {

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    public MasterService masterService;


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        masterService.master();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
