package jerrymice.Log4j;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author ：xiaosong
 * @description：TODO
 * @date ：2020/8/3 10:04
 */
public class Log4jDemo {
    // 基于类的名称创建日志对象,这里面要传入的参数是logger所在的类
    static Logger logger = Logger.getLogger(Log4jDemo.class);
    public static void main(String[] args) throws InterruptedException{
        // 进行默认配置
        PropertyConfigurator.configure("D:\\Java\\JavaProject\\Jerrymice\\src\\jerrymice.demo\\Log4j\\log4j.properties");
        // 设置输出日志最低级别
        logger.setLevel(Level.DEBUG);
        logger.trace("this is a trace msg");
        logger.debug("this is a debug msg");
        logger.info("this is a info msg");
        Thread.sleep(1000);
        logger.warn("this is a warn msg");
        logger.error("this is a error msg");
        logger.fatal("this is a fatal msg");
    }
}
