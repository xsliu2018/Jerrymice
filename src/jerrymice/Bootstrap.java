package jerrymice;

import jerrymice.catalina.Context;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import cn.hutool.system.SystemUtil;
import jerrymice.catalina.Engine;
import jerrymice.catalina.Host;
import jerrymice.catalina.Service;
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
    public static void main(String[] args) {
        try {
            // 打印jvm信息
            logJvm();
            // 在port端口上新建serverSocket
            ServerSocket serverSocket = new ServerSocket(PORT);
            // 创建host对象，host对象对于一个配置文件来说是唯一的，所以在循环外创建
            Service service = new Service();
            // 外部使用一个while循环，当处理完一个Socket的链接请求之后，再处理下一个链接请求
            while (true) {
                Socket socket = serverSocket.accept();
                // 使用lambda表达式代替Runnable
                Runnable runnable = () -> {
                    try {
                        // 获取输入流，这个输入流表示的是收到一个浏览器客户端的请求
                        Request request = new Request(socket, service);

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
}