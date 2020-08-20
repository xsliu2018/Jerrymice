package jerrymice.webappservlet;

import cn.hutool.core.util.ReflectUtil;
import jerrymice.catalina.Context;
import jerrymice.http.Request;
import jerrymice.http.Response;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author : xsliu
 * @project : Jerrymice
 * @description : 继承HttpServlet，用于处理Servlet访问
 * @date : 2020-08-19 22:52
 */
public class InvokeServlet {
    private static InvokeServlet instance = new InvokeServlet();

    public static synchronized InvokeServlet getInstance(){
        return instance;
    }
    private InvokeServlet(){

    }

    /**
     * 提供service方法，根据请求中的uri获取ServletClassName，然后通过反射实例化，接着调用其service方法，因为所有的servlet都继承了
     * HttpServlet，所以必然提供了service方法，这个Service方法，这个service方法会根据请求的方式时GET还是POST来执行类中doGet
     * 或者doPost方法。
     * @param httpServletRequest 请求
     * @param httpServletResponse 响应
     */
    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        // 强转
        Request request = (Request)httpServletRequest;
        Response response = (Response) httpServletResponse;
        // 获取uri
        String uri = request.getUri();
        // 获取context
        Context context = request.getContext();
        // 获取类名
        String servletName = context.getServletName(uri);
        // 获取对应的实例
        Object servletObj = ReflectUtil.newInstance(servletName);
        // 调用service方法
        cn.hutool.core.util.ReflectUtil.invoke(servletObj, "service", request, response);
    }

    public void service(Request request, Response response, String servletClassName){
        // 获取对应的实例
        Object servletObj = ReflectUtil.newInstance(servletClassName);
        // 调用service方法
        cn.hutool.core.util.ReflectUtil.invoke(servletObj, "service", request, response);
    }
}
