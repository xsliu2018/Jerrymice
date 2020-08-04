package jerrymice.catalina;

import cn.hutool.log.LogFactory;
import jerrymice.util.Constant;
import jerrymice.util.ServerXmlUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：xiaosong
 * @description：TODO
 * @date ：2020/8/4 22:16
 */
public class Host {
    private String name;
    private Map<String, Context> contextMap;

    public Host(){
        this.name = ServerXmlUtil.getHostName();
        this.contextMap = new HashMap<>();
        // 扫描文件夹内的所有应用
        scanContextOnWebAppsFolder();
        // 通过配置文件server.xml扫描指定的应用
        scanContextsByServerXml();
    }
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    /**
     * 扫描webapp的根目录，将所有的文件夹(应用)做成Context对象保存在Map中
     */
    private  void scanContextOnWebAppsFolder(){
        LogFactory.get().info("Scanning webapps in webapps...");
        File[] files = Constant.webappsFolder.listFiles();
        if (files == null){
            // 如果应用目录下根本没有应用，那就直接再见报告错误日志
            LogFactory.get().error(new NoSuchFieldError());
            return;
        }
        for (File file : files){
            if (!file.isDirectory()) {
                continue;
            }
            loadContext(file);
        }
    }
    private void loadContext(File folder) {
        // 对文件夹中的文件进行解析, 获取文件夹名
        String path = folder.getName();
        if ("ROOT".equals(path)) {
            // 如果是根目录的话
            path = "/";
        }
        else {
            path = "/" + path;
        }
        String docBase = folder.getAbsolutePath();
        // 建立Context对象用于保存path和docBase
        Context context = new Context(path, docBase);
        // 将创建好的context放在Map中留待使用
        contextMap.put(context.getPath(), context);
    }

    /**
     * 从server.xml文件中获取配置信息
     */
    private  void scanContextsByServerXml(){
        LogFactory.get().info("Scanning webapps from server.xml...");
        List<Context> contexts = ServerXmlUtil.getContext();
        for (Context context: contexts){
            contextMap.put(context.getPath(), context);
        }
    }

    /**
     * 通过path获取其对饮的context
     */
    public Context getContext(String path){
        return contextMap.get(path);
    }
}
