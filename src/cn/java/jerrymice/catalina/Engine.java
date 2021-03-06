package cn.java.jerrymice.catalina;

import cn.hutool.log.LogFactory;
import cn.java.jerrymice.util.ServerXmlUtil;

import java.util.List;

/**
 * @author ：xiaosong
 * @description：TODO
 * @date ：2020/8/4 22:59
 */
public class Engine {
    private final String defaultHost;
    private List<Host> hosts;
    private Service service;

    public Engine(Service service){
        this.defaultHost = ServerXmlUtil.getEngineDefaultHost();
        this.hosts = ServerXmlUtil.getHosts(this);
        this.service = service;
        checkDefault();
    }

    /**
     * 检查时候存在默认的host，如果不存在的话抛出异常
     */
    private void checkDefault(){
        if (getDefaultHost() == null) {
            LogFactory.get().error("the default host does not exist!");
            throw new RuntimeException("the default host does not exist!");
        }
    }

    public Host getDefaultHost(){
        for (Host host : hosts) {
            if (defaultHost.equals(host.getName())){
                return host;
            }
        }
        return null;
    }

    public Service getService(){
        return service;
    }


}
