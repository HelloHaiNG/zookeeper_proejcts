package com.ucar.balancecommon.entity;

import java.io.Serializable;

/**
 * @author liaohong
 * @since 2018/12/12 10:16
 */
public class ServerData implements Serializable {

    private static final long serialVersionUID = 2965518573348700597L;
    private String serverName;
    private String serverIp;
    private Integer balanceNum;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public Integer getBalanceNum() {
        return balanceNum;
    }

    public void setBalanceNum(Integer balanceNum) {
        this.balanceNum = balanceNum;
    }
}
