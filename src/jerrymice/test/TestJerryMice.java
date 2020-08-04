package jerrymice.test;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import jerrymice.util.MiniBrowser;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ：xiaosong
 * @description：TODO
 * @date ：2020/8/3 17:04
 */
public class TestJerryMice {
    /**
     *   预先定义端口和ip，方便后续进行修改
     */
    private static int port = 10086;
    private static String ip = "127.0.0.1";
    @BeforeClass
    public static void beforeClass(){
        // 测试之前先看服务是否已经启动了
        if (NetUtil.isUsableLocalPort(port)){
            //如果端口被占用，以为这服务已经被启动，否则说明服务还没有启动
            System.out.println("please start up JerryMice first, then run jerrymice.test");
            System.exit(1);
        }
        else {
            System.out.println("Detected JerryMice already start, now begin jerrymice.test");
        }
    }
    /**
     * 准备一个工具方法，用来获取网页的返回
     */
    private String getContentString(String uri){
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
        return MiniBrowser.getContentString(url);
    }
    @Test
    public void testHelloJerryMice(){
        String html = getContentString("/");
        Assert.assertEquals(html, "Hello JerryMice");
    }
    @Test
    public void testTimeConsume() throws InterruptedException {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 20, 60,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(10));
        // 计时开始
        TimeInterval timeInterval = DateUtil.timer();
        for (int i = 0; i < 3; i++) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    getContentString("/timeConsume.html");
                }
            });
        }
        // 终止线程池
        threadPool.shutdown();
        // 等待所有进程结束
        threadPool.awaitTermination(1, TimeUnit.HOURS);

        // 获取消耗时长，单位为ms
        long duration = timeInterval.intervalMs();
        // 虽然使用多线程对同一个网页进行访问，但由于现在的bootstrap是串行的，所以三个线程缩小喊的时间必然是大于3000ms的
        Assert.assertTrue(duration < 3000);
    }

    @Test
    public void testContext(){
        String html = getContentString("/dir1/1.html");
        Assert.assertEquals(html, "this file in webapps/dir1/");
    }

}
