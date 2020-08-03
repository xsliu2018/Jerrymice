package test;

import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.MiniBrowser;

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
            System.out.println("please start up JerryMice first, then run test");
            System.exit(1);
        }
        else {
            System.out.println("Detected JerryMice already start, now begin test");
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

}
