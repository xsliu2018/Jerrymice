package jerrymice.Junit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;

/**
 * @author ：xiaosong
 * @description：TODO
 * @date ：2020/8/3 16:29
 */
public class JuintDemo {
    @Test
    public void testSum() {
        int result = SumUtil.sum(1, 2);
        Assert.assertEquals(result, 3);
    }
    @Test
    public void testSum1(){
        int result = SumUtil.sum(1,2);
        Assert.assertEquals(result, 10);
    }
    @After
    public void after(){
        System.out.println("测试结束后的工作，比如关闭文件，关闭数据库连接等等");
    }
    @Before
    public void before(){
        System.out.println("测试钱的准备工作，比如打开文件，连接数据库等等");
    }

}
