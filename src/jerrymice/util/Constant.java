package jerrymice.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.system.SystemUtil;
import java.io.File;

/**
 * @author ：xiaosong
 * @description：TODO
 * @date ：2020/8/3 21:43
 */
public class Constant {
    public final static String responseHead200 = "HTTP/1.1 200 OK\r\n" +
            "Content-Type: {}\r\n\r\n";
    public final static File webappsFolder = new File(SystemUtil.get("user.dir"),"webapps");
    public final static File rootFolder = new File(webappsFolder,"ROOT");
    /**
     * 增加两个常量用于定位配置文件server.xml
     */
    public final static File confFolder = new File(SystemUtil.get("user.dir"), "conf");
    public final static File serverXmlFile = new File(confFolder, "server.xml");

    /**
     * 404错误的头部信息
     */
    public final static String responseHead404 = "HTTP/1.1 404 Not Found\r\n" + "Content-Type: {}\r\n\r\n";
    /**
     * 将一些特殊响应的文件都放在这个文件夹中
     */
    public final static File special_folder = new File(SystemUtil.get("user.dir"), "special");
    /**
     * 404错误的指定页面
     */
    public final static File not_exist_html = new File(special_folder, "404.html");
    /**
     * 将这个页面解析成字符串
     */
    public final static String textFormat404 = FileUtil.readUtf8String(not_exist_html);
    /**
     *  500错误的头部信息
     */
    public final static String responseHead500 = "HTTP/1.1 500 Internal Server Error\r\n"
            + "Content-Type: text/html\r\n\r\n";
    /**
     * 500错误的指定页面
     */
    public final static File expected_html = new File(special_folder, "500.html");
    /**
     * 将500页面解析成字符串
     */
    public final static String textFormat500 = FileUtil.readUtf8String(expected_html);

    /**
     * 当context没有指定访问哪个文件时，就遍历这个配置文件中指定的文件，发现哪个，就访问哪个
     */
    public final static File webXmlFile = new File(confFolder, "web.xml");

}
