package com.ucar.balanceclient2.balance;

import com.ucar.balancecommon.entity.ServerData;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author liaohong
 * @since 2018/12/12 10:41
 */
public class Client2 {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ZkClient zkClient = null;
    private String serverPath;
    private int SESSIONTIMEOUT = 15000;
    private int CONNECTIONTIMEOUT = 15000;

    public Client2() {
    }

    public Client2(String ipAddress, String serverPath) {
        this.zkClient = new ZkClient(ipAddress, SESSIONTIMEOUT, CONNECTIONTIMEOUT, new SerializableSerializer());
        this.serverPath = serverPath;
    }

    public void start() {
        logger.info("client2 服务启动，准备初始化...");

        init();
    }

    public void init() {
        logger.info("client2 开始初始化...");
        logger.info("获取{}节点的子节点...", serverPath);
        List<String> children = getChildren();
        logger.info("{}子节点有{}：", serverPath, children.toString());
        logger.info("client2 使用负载均衡调用工作服务器进行工作....");
        String node = balance(children);
//        release(node);
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取serverPath的子节点列表
     *
     * @return
     */
    public List<String> getChildren() {
        List<String> list = zkClient.getChildren(serverPath);
        List<String> list1 = new ArrayList<>();
        for (String string : list) {
            string = "/servers/".concat(string);
            list1.add(string);
        }
        return list1;
    }

    /**
     * 判断哪个工作服务器负载最小
     *
     * @param children
     */
    public String balance(List<String> children) {
        List<Integer> integerList = new ArrayList<>();
        for (String string : children) {
            ServerData serverData = zkClient.readData(string);
            Integer balanceNum = serverData.getBalanceNum();
            integerList.add(balanceNum);
        }
        Collections.sort(integerList);
        for (String string : children) {
            ServerData serverData = zkClient.readData(string);
            Integer balanceNum = serverData.getBalanceNum();
            if (balanceNum.equals(integerList.get(0))) {
                logger.info("调用{}节点对应的服务器进行服务", string);
                balanceNum++;
                serverData.setBalanceNum(balanceNum);
                logger.info("更新{}节点负载情况", string);
                zkClient.writeData(string, serverData);
                return string;
            }
        }
        return null;
    }

    /**
     * 调用完毕，释放负载
     *
     * @param string
     */
    public void release(String string) {
        logger.info("释放{}节点的负载", string);
        ServerData serverData = (ServerData) zkClient.readData(string);
        serverData.setBalanceNum(serverData.getBalanceNum() - 1);
        zkClient.writeData(string, serverData);
    }

}
