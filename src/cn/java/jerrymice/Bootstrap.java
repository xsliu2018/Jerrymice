package cn.java.jerrymice;

import cn.java.jerrymice.classloader.CommonClassLoader;

import java.lang.reflect.Method;

/**
 * @author ：xiaosong
 * @description：项目的启动类
 * @date ：2020/7/28 20:41
 */
public class Bootstrap {
    public static void main(String[] args) throws Exception{
        CommonClassLoader commonClassLoader = new CommonClassLoader();
        Thread.currentThread().setContextClassLoader(commonClassLoader);
        String serverClassName = "cn.java.jerrymice.catalina.Server";
        Class<?> serverClazz = commonClassLoader.loadClass(serverClassName);
        Object serverObj = serverClazz.newInstance();
        Method method = serverClazz.getMethod("start");
        method.invoke(serverObj);
        System.out.println(serverClazz.getClassLoader());
    }
}

