package cn.java.jerrymice.http;

import cn.java.jerrymice.catalina.Context;

import java.io.File;
import java.util.*;

/**
 * @ClassName: ApplicationContext
 * @Description:
 * @author: xsliu
 * @Date: 2020/9/22 4:40 下午
 * @version: 1.0
 */
public class ApplicationContext extends BaseServletContext{
    private Map<String, Object> attributesMap;
    private Context context;

    public ApplicationContext(Context context){
        this.attributesMap = new HashMap<>();
        this.context = context;
    }

    public void removeAttribute(String name){
        attributesMap.remove(name);
    }

    public void setAttribute(String name, Object value){
        attributesMap.put(name, value);
    }

    public Object getAttribute(String name){
        return attributesMap.get(name);
    }

    public Enumeration<String> getAttributeNames(){
        Set<String> keys = attributesMap.keySet();
        return Collections.enumeration(keys);
    }

    public String getRealPath(String path){
        return new File(context.getDocBase(), path).getAbsolutePath();
    }
}
