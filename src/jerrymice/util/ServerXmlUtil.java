package jerrymice.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.log.LogFactory;
import jerrymice.catalina.Context;
import jerrymice.catalina.Engine;
import jerrymice.catalina.Host;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author ：xiaosong
 * @description：用于解析配置xml文件
 * @date ：2020/8/4 20:59
 */
public class ServerXmlUtil {
    public static List<Context> getContext(){
        List<Context> result = new ArrayList<>();
        try{
            Document document = Jsoup.parse(Constant.serverXmlFile, "utf-8");
            Elements elements = document.select("Context");
            for (Element element : elements){
                // 获取指定的映射路径和绝对路径
                String path = element.attr("path");
                String docBase = element.attr("docBase");
                // 新建context对象，用于封装获取的信息
                Context context = new Context(path, docBase);
                result.add(context);
            }
        }catch (IOException e){
            LogFactory.get().info(e);
        }

        return result;
    }
    public static String getServiceName(){
        String name = "";
        try{
            Document document = Jsoup.parse(Constant.serverXmlFile, "utf-8");
            Element service = document.select("Service").first();
            name = service.attr("name");
        }catch(IOException e){
            LogFactory.get().error(e);
        }
        return name;
    }
    public static String getEngineDefaultHost(){
        String defaultHost = "";
        try{
            Document document = Jsoup.parse(Constant.serverXmlFile, "utf-8");
            Element engine = document.select("Engine").first();
            defaultHost = engine.attr("defaultHost");
        }catch(IOException e){
            LogFactory.get().info(e);
        }
        return defaultHost;
    }
    public static List<Host> getHosts(Engine engine) {
        List<Host> hosts = new ArrayList<>();
        try {
            Document document = Jsoup.parse(Constant.serverXmlFile, "utf-8");
            Elements elements = document.select("Host");
            for (Element element : elements) {
                String name = element.attr("name");
                Host host = new Host(name, engine);
                hosts.add(host);
            }
        }catch (IOException e){
            LogFactory.get().info(e);
        }
        return hosts;
    }
}
