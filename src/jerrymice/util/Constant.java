package jerrymice.util;

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
}
