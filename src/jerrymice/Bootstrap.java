package jerrymice;

import jerrymice.catalina.*;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import cn.hutool.system.SystemUtil;
import jerrymice.http.Request;
import jerrymice.http.Response;
import jerrymice.util.Constant;
import jerrymice.util.ServerXmlUtil;
import sun.awt.windows.WPrinterJob;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * @author ：xiaosong
 * @description：项目的启动类
 * @date ：2020/7/28 20:41
 */
public class Bootstrap {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}

