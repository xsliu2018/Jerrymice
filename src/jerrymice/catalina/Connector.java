package jerrymice.catalina;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import com.sun.org.apache.bcel.internal.ExceptionConst;
import jerrymice.http.Request;
import jerrymice.http.Response;
import jerrymice.util.Constant;
import jerrymice.util.ThreadUtil;
import jerrymice.util.WebXmlUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author xsl20
 */
public class Connector implements Runnable{

    int port;
    private Service service;

    public Service getService(){
        return service;
    }
    public void setPort(int port){
        this.port = port;
    }
    public Connector(Service service){
        this.service = service;
    }

    public void init(){
        LogFactory.get().info("Initializing Protocol [http-bio-{}], port");
    }

    public void start(){
        LogFactory.get().info("Starting ProtocolHandler [http-bio-{}],port");
        new Thread(this).start();
    }

    @Override
    public void run() {
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            while(true) {
                Socket socket = serverSocket.accept();
                Runnable r = () -> {
                    try {
                        Request request = new Request(socket, service);
                        Response response = new Response();
                        HttpServer httpServer = new HttpServer();
                        httpServer.execute(socket, request, response);
                    }catch (Exception e){
                        handle500(socket, e);
                    }finally {
                        // 将socket的关闭提取到最后
                        try{
                            socket.close();
                        }catch(IOException e){
                            LogFactory.get().info(e);
                        }
                    }
                };
                ThreadUtil.run(r);
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
    protected void handle500(Socket socket, Exception exception){
        try {
            OutputStream outputStream = socket.getOutputStream();
            //当发生异常时，首先Exception的异常堆栈，比如做一些平常的任务时，当异常发生时，会出错的位置和依次调用的信息，这些信息就是放在
            //异常堆栈中的,异常堆栈并不包含这个异常本身
            StackTraceElement[] stackTraceElements = exception.getStackTrace();
            // 准备一个StringBuilder来将这些信息装起来
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(exception.toString());
            stringBuilder.append("\r\n");
            for (StackTraceElement element : stackTraceElements) {
                stringBuilder.append("\t");
                stringBuilder.append(element.toString());
                stringBuilder.append("\r\n");
            }

            String msg = exception.getMessage();

            if (null != msg && msg.length() > 20) {
                msg = msg.substring(0, 19);
            }

            String text = StrUtil.format(Constant.textFormat500, msg, exception.toString(), stringBuilder.toString());
            text = Constant.responseHead500 + text;
            byte[] responseBytes = text.getBytes();
            outputStream.write(responseBytes);
        }catch(IOException e) {
            LogFactory.get().info(e);
        }
    }

}
