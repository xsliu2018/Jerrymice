package jerrymice.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.log.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ContextXmlUtil {
    public static String getWatchedResource(){
        try{
            String xml = FileUtil.readUtf8String(Constant.contextFile);
            Document d = Jsoup.parse(xml);
            Element element = d.selectFirst("WatchedResource");
            return element.text();
        }catch(Exception e){
            LogFactory.get().info(e.toString());
            return "WEB-INF/web.xml";
        }
    }
}
