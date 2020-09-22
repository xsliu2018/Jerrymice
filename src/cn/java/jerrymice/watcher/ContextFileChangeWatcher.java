package cn.java.jerrymice.watcher;

import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.WatchUtil;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.log.LogFactory;
import cn.java.jerrymice.catalina.Context;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * @ClassName: ContextFileChangeWatcher
 * @Description:
 * 用于监听Context对应的文件的改变，比如Context对应的xml文件内容，Context对应的docBase的路径和路径下的文件
 * @author: xsliu
 * @Date: 2020/9/22 3:41 下午
 * @version: 1.0
 */
public class ContextFileChangeWatcher {
    // 真正监听变化的监听器
    private WatchMonitor monitor;
    // 用来标志监听器的运作状态是否停止
    private boolean stop = false;

    public ContextFileChangeWatcher(Context context){
        this.monitor = WatchUtil.createAll(context.getDocBase(), Integer.MAX_VALUE, new Watcher() {
            @Override
            public void onCreate(WatchEvent<?> watchEvent, Path path) {

            }

            @Override
            public void onModify(WatchEvent<?> watchEvent, Path path) {

            }

            @Override
            public void onDelete(WatchEvent<?> watchEvent, Path path) {

            }

            @Override
            public void onOverflow(WatchEvent<?> watchEvent, Path path) {

            }
            private void dealWith(WatchEvent<?> event) {
                synchronized (ContextFileChangeWatcher.class) {
                    String fileName = event.context().toString();
                    if(stop){
                        return;
                    }
                    if (fileName.endsWith(".jar") || fileName.endsWith(".class") || fileName.endsWith(".xml")){
                        // 以上三种文件形式的改变，都会导致监听器的停止，并重新建立一个Context来代替现在这个Context
                        stop = true;
                        LogFactory.get().info(ContextFileChangeWatcher.this + "检测到了重要文件发生变化{}", fileName);
                        context.reload();
                    }
                }
            }
        });

    }
    public void start(){
        monitor.start();
    }

    public void stop(){
        monitor.close();
    }
}
