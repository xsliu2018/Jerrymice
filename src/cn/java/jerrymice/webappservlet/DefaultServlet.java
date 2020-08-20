package cn.java.jerrymice.webappservlet;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.java.jerrymice.catalina.Context;
import cn.java.jerrymice.http.Request;
import cn.java.jerrymice.http.Response;
import cn.java.jerrymice.util.Constant;
import cn.java.jerrymice.util.WebXmlUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * @author : xsliu
 * @project : Jerrymice
 * @description : 用于处理及静态页面
 * @date : 2020-08-20 10:13
 */
public class DefaultServlet extends HttpServlet {
    // 单例模式
    private static final DefaultServlet instance = new DefaultServlet();

    public static synchronized DefaultServlet getInstance(){
        return instance;
    }

    private DefaultServlet(){

    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        Request request = (Request) httpServletRequest;
        Response response = (Response) httpServletResponse;
        Context context = request.getContext();

        String uri = request.getUri();

        if ("/500.html".equals(uri)) {
            throw new RuntimeException("this is a deliberately created exception");
        }

        if ("/".equals(uri)) {
            uri = WebXmlUtil.getWelcomeFile(context);
        }
        String fileName = StrUtil.removePrefix(uri, "/");
        File file = FileUtil.file(context.getDocBase(), fileName);
        if (file.exists()){
            String extName = FileUtil.extName(file);
            String mimeType = WebXmlUtil.getMimeType(extName);
            response.setContentType(mimeType);
            byte[] body = FileUtil.readBytes(file);
            response.setBody(body);

            if (fileName.equals("timeConsume.html")){
                ThreadUtil.sleep(1000);
            }
            response.setStatus(Constant.CODE_200);
        }
        else{
            response.setStatus(Constant.CODE_404);
        }
    }
}
