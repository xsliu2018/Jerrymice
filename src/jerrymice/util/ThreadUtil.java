package jerrymice.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ：xiaosong
 * @description：TODO
 * @date ：2020/8/4 15:04
 */
public class ThreadUtil {
    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            20, 100, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(10)
    );

    public static void run(Runnable r){
        threadPool.execute(r);
    }
}
