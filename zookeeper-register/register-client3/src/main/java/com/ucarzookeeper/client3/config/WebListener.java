package com.ucarzookeeper.client3.config;

import com.ucarzookeeper.core.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Map;

/**
 * @author liaohong
 * @since 2018/12/10 11:14
 */
@Component
public class WebListener implements ServletContextListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${registry.workserver}")
    private String workserver;

    @Autowired
    public ServiceRegistry serviceRegistry;


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("注册服务：{}", workserver);
        serviceRegistry.register("master", workserver);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
