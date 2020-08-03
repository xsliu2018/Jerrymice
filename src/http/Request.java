package http;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import util.MiniBrowser;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author ：xiaosong
 * @description：TODO
 * @date ：2020/8/3 20:44
 */
public class Request {
    private String requestString;
    private String uri;
    private Socket socket;

    /**
     * 构造方法
     */
    public Request(Socket socket) throws IOException {
        this.socket = socket;
        parseHttpRequest();
        if (StrUtil.isEmpty(requestString)){
            return;
        }
        parseUri();
    }
    private void parseHttpRequest() throws IOException {
        // 解析Request,服务器端获取浏览器端传过来的请求
        InputStream inputStream = this.socket.getInputStream();
        byte[] bytes = MiniBrowser.readBytes(inputStream);
        this.requestString = new String(bytes, StandardCharsets.UTF_8);
    }
    private void parseUri() {
        // 解析uri，定位服务器上的文件
        String temp;
        /*
        StrUtil.subBetween方法返回before和after之间的子串，不包含before和after
        此处就是获取两个空格之间的内容,如果地址是 http://127.0.0.1:18080/index.html?name=gareen
        那么http请求就会是
        GET /index.html?name=gareen HTTP/1.1
        Host: 127.0.0.1:18080
        Connection: keep-alive
        。。。。
        只需要获取两个空格之间的部分就可以获得请求的uri
         */
        temp = StrUtil.subBetween(requestString, " ", " ");
        this.uri = StrUtil.subBefore(temp, "?", false);
    }
    public String getUri(){
        return uri;
    }

    public String getRequestString() {
        return requestString;
    }
}
