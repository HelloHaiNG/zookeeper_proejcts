package com.ucarzookeeper.client3.config;

import com.ucarzookeeper.core.service.ServiceRegistry;
import com.ucarzookeeper.core.service.impl.ServiceRegistryImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liaohong
 * @since 2018/12/10 11:10
 */
@Configuration
@ConfigurationProperties(prefix = "registry")
public class RegistryConfig {

    private String servers;

    @Bean
    public ServiceRegistry serviceRegistry() {
        return new ServiceRegistryImpl(servers);
    }

    public void setServers(String servers) {
        this.servers = servers;
    }
}
