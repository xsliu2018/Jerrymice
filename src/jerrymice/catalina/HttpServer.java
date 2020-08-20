package jerrymice.catalina;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import jerrymice.http.Request;
import jerrymice.http.Response;
import jerrymice.util.Constant;
import jerrymice.util.WebXmlUtil;
import jerrymice.webappservlet.DefaultServlet;
import jerrymice.webappservlet.HelloServlet;
import jerrymice.webappservlet.InvokeServlet;
import sun.awt.windows.WPrinterJob;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author ：xsliu
 * @date ：Created in 2020/8/18 17:18
 * @description：接管Connector类中对请求的处理部分
 * @modified By：
 */
public class HttpServer {
    public void execute(Socket socket, Request request, Response response){
        // 处理请求和响应
        try{
            String uri = request.getUri();
            System.out.println(uri);
            if (uri == null) {
                // 说明此时没有请求过来
                return;
            }
            // 获取request的context,context的path是访问的文件夹路径，docBase是实际在系统中的绝对路径
            Context context = request.getContext();
            // 根据context和uri来获取className
            String servletClassName = context.getServletClassName(uri);
            if (null != servletClassName) {
                InvokeServlet.getInstance().service(request, response);
            }
            else{
                DefaultServlet.getInstance().service(request, response);
            }
            if (response.getStatus() == Constant.CODE_200){
                handle200(socket, response);
                return;
            }
            if (response.getStatus() == Constant.CODE_404){
                handle404(socket, uri);
            }
        }catch (Exception e) {
            LogFactory.get().info(e.toString());
            handle500(socket, e);
        }finally {
            try{
                if (!socket.isClosed()){
                    socket.close();
                }
            }catch (IOException e){
                LogFactory.get().info(e.toString());
            }
        }
    }

    /**
     * 处理文件不存在异常
     */
    private void handle404(Socket socket, String uri) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        String responseText = StrUtil.format(Constant.textFormat404, uri, uri);
        responseText = Constant.responseHead404 + responseText;
        byte[] responseBytes = responseText.getBytes("utf-8");
        outputStream.write(responseBytes);
    }

    /**
     * 处理正常响应
     */

    private void handle200(Socket socket, Response response) throws IOException{
        String contentType = response.getContentType();
        String headText = Constant.responseHead200;
        headText = StrUtil.format(headText, contentType);
        byte[] head = headText.getBytes();
        byte[] body = response.getBody();
        byte[] responseBytes = new byte[head.length + body.length];
        ArrayUtil.copy(head, 0, responseBytes, 0, head.length);
        ArrayUtil.copy(body, 0, responseBytes, head.length, body.length);

        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(responseBytes);
    }

    /**
     * 处理访问时出现的问题
     * @param socket: 访问通信过程中所使用的socket
     * @param e: 异常
     */
    private void handle500(Socket socket, Exception e){
        try{
            OutputStream outputStream = socket.getOutputStream();
            StackTraceElement[]  stackTraceElements = e.getStackTrace();
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(e.toString());
            stringBuffer.append("\r\n");
            for (StackTraceElement element : stackTraceElements) {
                stringBuffer.append("\t");
                stringBuffer.append(element.toString());
                stringBuffer.append("\r\n");
            }
            String msg = e.getMessage();
            if (null != msg && msg.length() > 20){
                msg = msg.substring(0, 19);
            }

            String text = StrUtil.format(Constant.textFormat500, msg, e.toString(), stringBuffer.toString());
            text = Constant.responseHead500 + text;
            byte[] responseBytes = text.getBytes("utf-8");
            outputStream.write(responseBytes);
        }catch(IOException e1){
            LogFactory.get().info(e1.toString());
        }
    }
}
