package jerrymice.catalina;

import jerrymice.util.ServerXmlUtil;

/**
 * @author ：xiaosong
 * @description：TODO
 * @date ：2020/8/5 10:21
 */
public class Service {
    private final String name;
    private final Engine engine;

    public Service(){
        this.name = ServerXmlUtil.getServiceName();
        this.engine = new Engine(this);
    }

    public Engine getEngine(){
        return engine;
    }
    public String getName(){
        return name;
    }
}
