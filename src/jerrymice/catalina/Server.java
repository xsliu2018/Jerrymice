package jerrymice.catalina;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import cn.hutool.system.SystemUtil;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import jerrymice.http.Request;
import jerrymice.http.Response;
import jerrymice.util.Constant;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author ：xiaosong
 * @description：TODO
 * @date ：2020/8/5 14:06
 */
public class Server {
    private Service service;
    public Server(){
        this.service = new Service(this);
    }

    public void start(){
        logJvm();
        init();
    }
    private void logJvm() {
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

    @SuppressWarnings("InfiniteLoopStatement")
    private void init(){
        try {
            int port = 10086;
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                Socket socket = serverSocket.accept();
                Runnable runnable = () -> {
                    try {
                        Request request = new Request(socket, service);
                        Response response = new Response();
                        String uri = request.getUri();
                        if (null == uri){
                            return;
                        }
                        System.out.println("uri:" + uri);
                        Context context = request.getContext();
                        if ("/".equals(uri)) {
                            String html = "Hello, JerryMice";
                            response.getWriter().println(html);
                        }
                        else {
                            String fileName = StrUtil.removePrefix(uri, "/");
                            File file = FileUtil.file(context.getDocBase(), fileName);
                            if (file.exists()) {
                                String fileContent = FileUtil.readUtf8String(file);
                                response.getWriter().println(fileContent);

                                if ("timeConsume.html".equals(fileName)) {
                                    ThreadUtil.sleep(1000);
                                }
                            }
                            else {
                                // 访问文件不存在的情况下
                                handle404(socket, uri);
                                return;
                            }
                        }
                        handle200(socket, response);
                    }catch (IOException e) {
                        LogFactory.get().info(e);
                    }finally {
                        // 将socket的关闭提取到最后，因为无论是200的响应还是404的响应都需要关闭socket
                        try {
                            socket.close();
                        }catch (IOException e){
                            LogFactory.get().info(e);
                        }
                    }
                };
                jerrymice.util.ThreadUtil.run(runnable);
            }
        }catch (IOException e) {
            LogFactory.get().info(e);
        }
    }
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
    }

    private static void handle404(Socket socket, String uri) throws IOException{
        OutputStream outputStream = socket.getOutputStream();
        String responseText = StrUtil.format(Constant.textFormat404, uri, uri);
        responseText = Constant.responseHead404 + responseText;
        byte[] responseBytes = responseText.getBytes(StandardCharsets.UTF_8);
        outputStream.write(responseBytes);
    }
}
