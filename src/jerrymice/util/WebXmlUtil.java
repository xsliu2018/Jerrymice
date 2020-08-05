package jerrymice.util;

import cn.hutool.core.io.FileUtil;
import jerrymice.catalina.Context;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;

/**
 * @author xsl20
 */
public class WebXmlUtil {
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
}
