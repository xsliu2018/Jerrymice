import cn.hutool.core.util.NetUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author ：xiaosong
 * @description：项目的启动类
 * @date ：2020/7/28 20:41
 */
public class Bootstrap {
    final static int PORT = 10086;
    public static void main(String[] args){
        try {
            if (!NetUtil.isUsableLocalPort(PORT)){
                //查看当前定义的端口是否已经被占用,如果NetUtil.isUsableLocalPort方法返回true表示port定义的端口号可用
                System.out.println(PORT + "端口已经被占用, 排查关闭本端口的方法请用\r\nhttps:baidu.com");
                return;
            }
            // 在port端口上新建serverSocket
            ServerSocket serverSocket = new ServerSocket(PORT);
            // 外部使用一个while循环，当处理完一个Socket的链接请求之后，再处理下一个链接请求
            while (true) {
                Socket socket = serverSocket.accept();
                // 获取输入流，这个输入流表示的是收到一个浏览器客户端的请求
                InputStream inputStream = socket.getInputStream();
                //建立1024的缓冲区，请求是有可能超过缓冲区的大小的，所以这种固定缓冲区尺寸的做法是有缺陷的
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                //接收浏览器的请求
                inputStream.read(buffer);
                String requestString = new String(buffer, StandardCharsets.UTF_8);
                System.out.println("浏览器的输入信息： \r\n" + requestString);
                // 打开输出流，准备给客户端输出信息
                OutputStream outputStream = socket.getOutputStream();
                String responseHead = "HTTP/1.1 200 OK\r\n" + "Content-Type:text/html\r\n\r\n";
                String responseString = "Hello JerryMice";
                responseString = responseHead + responseString;
                // 以字节数组的形式包装从服务器端给用户端的数据
                outputStream.write(responseString.getBytes());
                outputStream.flush();
                // 关闭socket
                socket.close();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
