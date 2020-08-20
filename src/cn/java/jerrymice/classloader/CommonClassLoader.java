package cn.java.jerrymice.classloader;

import cn.hutool.log.LogFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author : xsliu
 * @project : Jerrymice
 * @description : 扫描lib文件夹下的所有jar文件，加载这些类的类对象
 * @date : 2020-08-20 16:04
 */
public class CommonClassLoader extends URLClassLoader {
    // 构造方法
    public CommonClassLoader() {
        super(new URL[]{});

        try {
            // 获取应用根目录
            File workingFolder = new File(System.getProperty("user.dir"));
            // 获取lib目录
            File libFolder = new File(workingFolder, "lib");
            // 获取lib目录下的文件
            File[] jarFiles = libFolder.listFiles();
            // 判断数组非空
            if (jarFiles != null) {
                // 遍历文件数组
                for (File file: jarFiles) {
                    // 判断是否是jar文件
                    if (file.getName().endsWith("jar")) {
                        URL url = new URL("file:" + file.getAbsolutePath());
                        this.addURL(url);
                    }
                }
            }
        }catch (MalformedURLException e){
            LogFactory.get().info(e.toString());
        }
    }
}
