import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import cn.hutool.system.SystemUtil;
import com.sun.org.apache.xpath.internal.objects.XString;
import http.Request;
import http.Response;
import util.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author ：xiaosong
 * @description：项目的启动类
 * @date ：2020/7/28 20:41
 */
public class Bootstrap {
    /**
    定义服务器的端口号
     */
    final static int PORT = 10086;
    public static void main(String[] args){
        logJvm();
        try {
//            if (!NetUtil.isUsableLocalPort(PORT)){
//                //查看当前定义的端口是否已经被占用,如果NetUtil.isUsableLocalPort方法返回true表示port定义的端口号可用
//                System.out.println(PORT + "端口已经被占用, 排查关闭本端口的方法请用\r\nhttps:baidu.com");
//                return;
//            }
            // 在port端口上新建serverSocket
            ServerSocket serverSocket = new ServerSocket(PORT);
            // 外部使用一个while循环，当处理完一个Socket的链接请求之后，再处理下一个链接请求
            while (true) {
                Socket socket = serverSocket.accept();
                // 获取输入流，这个输入流表示的是收到一个浏览器客户端的请求
                Request request = new Request(socket);

                System.out.println("浏览器的输入信息： \r\n" +request.getRequestString());
                System.out.println("uri:" + request.getUri());
                // 打开输出流，准备给客户端输出信息
                Response response = new Response();
                // 先将html信息写入到response的Writer的StringWriter中
                String html = "Hello JerryMice";
                response.getWriter().println(html);
                handle200(socket, response);

            }
        }catch (IOException e) {
           LogFactory.get().error(e);
        }
    }
    private static void logJvm(){
        // 创建一个Map用于保存各种信息
        Map<String, String> infoMap = new LinkedHashMap<>();
        infoMap.put("Server version", "JerryMice 1.0.0");
        infoMap.put("Server build", "2020-08-03");
        infoMap.put("OS:\t", SystemUtil.get("os.name"));
        infoMap.put("OS version", SystemUtil.get("os.version"));
        infoMap.put("Architecture", SystemUtil.get("os.arch"));
        infoMap.put("Java Home", SystemUtil.get("java.home"));
        infoMap.put("JSM Version",SystemUtil.get("java.runtime.version"));
        infoMap.put("JVM Vendor", SystemUtil.get("java.vm.specification.vendor"));
        Set<String> keys = infoMap.keySet();
        for (String key: keys){
            // 调用hutool的LogFactory工厂函数获取logger，logger会自动根据log4j.properties来对Log4j的Logger进行配置
            LogFactory.get().info(key + ":\t\t" + infoMap.get(key));
        }
    }

    /**
     *
     * @param socket:
     * @param response:Response对象，服务器对浏览器请求的响应，可以通过response的getBody()获取存储在其中的html文本
     * @throws IOException
     */
    private static void handle200(Socket socket, Response response) throws IOException{
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
