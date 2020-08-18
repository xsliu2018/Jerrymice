package jerrymice.catalina;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import jerrymice.http.Request;
import jerrymice.http.Response;
import jerrymice.util.Constant;
import jerrymice.util.WebXmlUtil;

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
            if (uri == null) {
                // 说明此时没有请求过来
                return;
            }
            Context context = request.getContext();
            if ("/500.html".equals(uri)) {
                throw new Exception("this is a deliberately created ");
            }
            if ("/".equals(uri)){
                //如果访问根目录
                uri = WebXmlUtil.getWelcomeFile(request.getContext());
            }
            String fileName = StrUtil.removePrefix(uri, "/");
            // 获取文件名
            File file = FileUtil.file(context.getDocBase(), fileName);
            // 通过context获取文件夹，然后通过文件夹和文件名来获取文件
            if (file.exists()){
                // 如果访问的文件存在，就根据这个获取后缀名
                String extName = FileUtil.extName(file);
                // 根据后缀名获取浏览器解析该文件时使用的mimeType
                String mimeType = WebXmlUtil.getMimeType(extName);
                response.setContentType(mimeType);
            }
            else{
                //文件不存在，返回404错误
                handle404(socket, uri);
               return;
            }
            handle200(socket, response);
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
