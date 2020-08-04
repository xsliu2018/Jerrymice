import cn.hutool.core.util.StrUtil;

import java.sql.Struct;

/**
 * @author ：xiaosong
 * @description：TODO
 * @date ：2020/8/4 18:42
 */
public class DemoStrUtil {
    public static void main(String[] args) {
        String str1 = "/dir1/1.html";
        System.out.println(StrUtil.removePrefix(str1, "/"));

    }
}
