package com.ucarzookeeper.core.service;

/**
 * @author liaohong
 * @since 2018/12/10 10:48
 */
public interface ServiceRegistry {

    /**
     * 注册服务信息
     *
     * @param serviceName    服务名称
     * @param workServer     服务名字
     */
    void register(String serviceName, String workServer);
}
