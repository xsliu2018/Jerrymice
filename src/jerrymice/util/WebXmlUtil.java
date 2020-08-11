package jerrymice.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import jerrymice.catalina.Connector;
import jerrymice.catalina.Context;
import jerrymice.catalina.Service;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xsl20
 */
public class WebXmlUtil {

    private static Map<String, String>mimeTypeMapping = new HashMap<>();
    public static synchronized String getMimeType(String extname){
        // 根据文件本身的拓展名来匹配浏览器解析文件的格式
        if (mimeTypeMapping.isEmpty()){
            initMimeType();
        }
        String mimeType = mimeTypeMapping.get(extname);

        if (mimeType == null) {
            mimeType = "text/html";
        }

        return mimeType;
    }
    public static String getWelcomeFile(Context context) {
        String xml = FileUtil.readUtf8String(Constant.webXmlFile);
        Document document
                 = Jsoup.parse(xml);
        Elements elements = document.select("welcome-file");

        for (Element element : elements) {
            File tmpFile = new File(context.getDocBase(), element.text());
            if (tmpFile.exists()) {
                // 如果找到了配置文件中指定的文件
                return tmpFile.getName();
            }
        }
        return "index.html";
    }

    private static void initMimeType(){
        String xml = FileUtil.readUtf8String(Constant.webXmlFile);
        Document document = Jsoup.parse(xml);
        Elements elements = document.select("mime-mapping");
        for (Element element : elements) {
            String extName = element.select("extension").first().text();
            String mimeType = element.selectFirst("mime-type").text();
            mimeTypeMapping.put(extName, mimeType);
        }
    }

    public static List<Connector> getConnectors(Service service){
        //一个service对应着多个connector
        List<Connector> connectors = new ArrayList<>();
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document document = Jsoup.parse(xml);
        Elements elements = document.select("Connector");
        for (Element element : elements){
            int port = Convert.toInt(element.attr("port"));
            Connector connector = new Connector(service);
            connector.setPort(port);
            connectors.add(connector);
        }
        return connectors;
    }
}
