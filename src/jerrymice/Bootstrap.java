package jerrymice;

import jerrymice.catalina.Context;
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
    /**
     * 定义服务器的端口号
     */
    final static int PORT = 10086;
    public static Map<String, Context> contextMap = new HashMap<>();
    public static void main(String[] args) {
        try {
            // 打印jvm信息
            logJvm();
            // 扫描文件夹内的所有应用
            scanContextOnWebAppsFolder();
            // 通过配置文件server.xml扫描指定的应用
            scanContextsByServerXml();
            // 在port端口上新建serverSocket
            ServerSocket serverSocket = new ServerSocket(PORT);
            // 外部使用一个while循环，当处理完一个Socket的链接请求之后，再处理下一个链接请求
            while (true) {
                Socket socket = serverSocket.accept();
                // 使用lambda表达式代替Runnable
                Runnable runnable = () -> {
                    try {
                        // 获取输入流，这个输入流表示的是收到一个浏览器客户端的请求
                        Request request = new Request(socket);

                        System.out.println("浏览器的输入信息： \r\n" + request.getRequestString());
                        Response response = new Response();
                        // 先将html信息写入到response的Writer的StringWriter中
                        String uri;
                        uri = request.getUri();
                        if (uri == null) {
                            return;
                        }
                        Context context = request.getContext();
                        if ("/".equals(uri)) {
                            String html = "Hello JerryMice";
                            response.getWriter().println(html);
                        } else {
                            // removePrefix()方法可以去掉字符串指定的前缀
                            String fileName = StrUtil.removePrefix(uri, "/");
                            File file = FileUtil.file(context.getDocBase(), fileName);
                            if (file.exists()) {
                                //如果文件存在，那就去试图访问
                                String fileContent = FileUtil.readUtf8String(file);
                                // 写入到response中
                                response.getWriter().println(fileContent);
                                // 判断是否是模拟的耗时任务
                                if ("timeConsume.html".equals(fileName)) {
                                    ThreadUtil.sleep(1000);
                                }
                            } else {
                                System.out.println("File not found!");
                            }
                        }
                        System.out.println(uri);
                        // 打开输出流，准备给客户端输出信息
                        handle200(socket, response);
                    } catch (IOException e) {
                        LogFactory.get().error(e);
                    }
                };
                jerrymice.util.ThreadUtil.run(runnable);
            }
        } catch (IOException e) {
            LogFactory.get().error(e);
        }
    }

    private static void logJvm() {
        // 创建一个Map用于保存各种信息
        Map<String, String> infoMap = new LinkedHashMap<>();
        infoMap.put("Server version", "JerryMice 1.0.0");
        infoMap.put("Server build", "2020-08-03");
        infoMap.put("OS:\t", SystemUtil.get("os.name"));
        infoMap.put("OS version", SystemUtil.get("os.version"));
        infoMap.put("Architecture", SystemUtil.get("os.arch"));
        infoMap.put("Java Home", SystemUtil.get("java.home"));
        infoMap.put("JSM Version", SystemUtil.get("java.runtime.version"));
        infoMap.put("JVM Vendor", SystemUtil.get("java.vm.specification.vendor"));
        Set<String> keys = infoMap.keySet();
        for (String key : keys) {
            // 调用hutool的LogFactory工厂函数获取logger，logger会自动根据log4j.properties来对Log4j的Logger进行配置
            LogFactory.get().info(key + ":\t\t" + infoMap.get(key));
        }
    }

    /**
     * @param socket:
     * @param response:Response对象，服务器对浏览器请求的响应，可以通过response的getBody()获取存储在其中的html文本
     * @throws IOException
     */
    private static void handle200(Socket socket, Response response) throws IOException {
        // 获取类型
        String contentType = response.getContentType();
        String headText = Constant.responseHead200;
        headText = StrUtil.format(headText, contentType);
        byte[] head = headText.getBytes();
        // 获取response中的html文本，这个html文本是通过writer写到stringWriter字符流上的
        byte[] body = response.getBody();
        byte[] responseBytes = new byte[head.length + body.length];
        ArrayUtil.copy(head, 0, responseBytes, 0, head.length);
        ArrayUtil.copy(body, 0, responseBytes, head.length, body.length);

        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(responseBytes);
        socket.close();
    }

    /**
     *
     * 扫描webapp的根目录，将所有的文件夹(应用)做成Context对象保存在Map中
     */
    private static void scanContextOnWebAppsFolder(){
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
    private static void loadContext(File folder) {
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

    private static void scanContextsByServerXml(){
        LogFactory.get().info("Scanning webapps from server.xml...");
        List<Context> contexts = ServerXmlUtil.getContext();
        for (Context context: contexts){
            contextMap.put(context.getPath(), context);
        }
    }
}
