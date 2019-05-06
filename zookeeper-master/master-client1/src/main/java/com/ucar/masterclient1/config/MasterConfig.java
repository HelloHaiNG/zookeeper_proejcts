package com.ucar.masterclient1.config;

import com.ucar.masterclient1.server.MasterService;
import com.ucar.masterclient1.server.impl.MasterServiceImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liaohong
 * @since 2018/12/10 11:10
 */
@Configuration
@ConfigurationProperties(prefix = "registry")
public class MasterConfig {

    private String servers;

    @Bean
    public MasterService masterService() {
        return new MasterServiceImpl(servers);
    }

    public void setServers(String servers) {
        this.servers = servers;
    }
}
