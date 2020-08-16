package jerrymice.catalina;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.log.LogFactory;
import jerrymice.util.ServerXmlUtil;
import jerrymice.util.WebXmlUtil;
import org.jsoup.helper.DataUtil;

import java.util.List;

/**
 * @author ：xiaosong
 * @description：TODO
 * @date ：2020/8/5 10:21
 */
public class Service {
    private final String name;
    private final Engine engine;
    private Server server;
    private List<Connector> connectors;

    public Service(Server server){
        this.server = server;
        this.name = ServerXmlUtil.getServiceName();
        this.engine = new Engine(this);
        this.connectors = ServerXmlUtil.getConnectors(this);
    }

    public Engine getEngine(){
        return engine;
    }
    public String getName(){
        return name;
    }

    public Server getServer(){
        return server;
    }

    public void start(){
        init();
    }

    public void init(){
        System.out.println("length of connector" + connectors.size());
        TimeInterval timeInterval = DateUtil.timer();
        for (Connector connector : connectors){
            connector.init();
        }
        LogFactory.get().info("Initialization processed in {} ms",timeInterval.intervalMs());
        for (Connector connector : connectors){
            connector.start();
        }
    }
}
