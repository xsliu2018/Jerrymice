package cn.java.jerrymice.http;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: StandardServletConfig
 * @Description:
 * @author: xsliu
 * @Date: 2020/9/22 5:00 下午
 * @version: 1.0
 */
public class StandardServletConfig implements ServletConfig {
    private ServletContext servletContext;
    private Map<String, String> initParameters;
    private String servletName;

    // 构造方法
    public StandardServletConfig(ServletContext servletContext, String servletName
        , Map<String, String> initParameters){
        this.servletContext = servletContext;
        this.servletName = servletName;
        this.initParameters = initParameters;

        if (this.initParameters == null){
            this.initParameters = new HashMap<>();
        }
    }
    @Override
    public String getServletName() {
        return null;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public String getInitParameter(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return null;
    }
}
