package cn.java.jerrymice.classloader;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * @ClassName: WebappClassLoader
 * @Description:
 * 1. 扫描Context对应的docBase下的class和lib
 * 2. 把jar通过addURL方法添加
 * 3. 把classes目录通过addURL加进去，注意因为是目录，所以添加时要在后面加上/，这样URLClassLoader才能将其作为目录来处理
 * @author: xsliu
 * @Date: 2020/9/22 1:54 下午
 * @version: 1.0
 */
public class WebappClassLoader extends URLClassLoader {
    public WebappClassLoader(String docBase, ClassLoader commonClassLoader){
        super(new URL[]{}, commonClassLoader);

        try{
            // web文件夹下的INF文件夹
            File webInFolder = new File(docBase, "WEB-INF");
            // 保存编译生成的classes文件夹
            File classFolder = new File(webInFolder, "classes");
            // lib文件夹
            File libFolder = new File(docBase, "lib");
            URL url = new URL("file:" + classFolder.getAbsolutePath() + "/");
            System.out.println(url);
            this.addURL(url);
            // 将所有的jar文件添加到URL
            List<File> jarFiles = FileUtil.loopFiles(libFolder);
            for (File file : jarFiles){
                url = new URL("file:" + file.getAbsolutePath());
                System.out.println(url);
                this.addURL(url);
            }
        }catch(MalformedURLException exception){
            exception.printStackTrace();
        }
    }

    public void stop() {
        try{
            close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
