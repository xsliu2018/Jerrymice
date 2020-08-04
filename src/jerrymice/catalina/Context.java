package jerrymice.catalina;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.log.LogFactory;

/**
 * @author ：xiaosong
 * @description：
 * 代表一个应用，它有两个属性，一个是path，表示访问的路径，docBase表示在文件系统中得分位置
 * @date ：2020/8/4 16:09
 */
public class Context {
    private String path;
    private String docBase;

    public Context(String path, String docBase){
        TimeInterval timeInterval = DateUtil.timer();
        this.path = path;
        this.docBase = docBase;
        LogFactory.get().info("Deploying web application directory {}", this.docBase);
        LogFactory.get().info("Deployment of web application directory {} has finished in {} ms",
                this.docBase, timeInterval.intervalMs());

    }
    public String getPath() {
        return path;
    }

    public String getDocBase() {
        return docBase;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDocBase(String docBase) {
        this.docBase = docBase;
    }
}
