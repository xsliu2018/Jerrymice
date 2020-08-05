package jerrymice.http;

import jerrymice.Bootstrap;
import jerrymice.catalina.Context;
import cn.hutool.core.util.StrUtil;
import jerrymice.catalina.Engine;
import jerrymice.catalina.Host;
import jerrymice.util.MiniBrowser;

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
    private Context context;
    private Engine engine;

    public Context getContext() {
        return context;
    }

    /**
     * 根据request的uri来解析成Context
     */
    public void parseContext(){

        String path = StrUtil.subBetween(uri, "/", "/");
        if (null == path) {
            // 如果uri = /timeConsume.html，那么path = null， 经过此处之后path=/
            path = "/";
        }
        else {
            // uri = /dir1/1.html, 那么path= dir1， 经过此处之后path=/dir1
            path = "/" + path;
        }
        // 根据获取到的path去扫描得到的映射中去寻找这个文件夹
        context = engine.getDefaultHost().getContext(path);
        if (context == null) {
            // 如果没有获取到这个context对象，那么说明目录中根本就没有这个应用,或者本身就在根目录下
            context = engine.getDefaultHost().getContext("/");
        }
    }


    /**
     * 构造方法
     */
    public Request(Socket socket, Engine engine) throws IOException {
        this.socket = socket;
        this.engine = engine;
        parseHttpRequest();
        if (StrUtil.isEmpty(requestString)){
            return;
        }
        parseUri();
        parseContext();
        // 比如 uri 是 /a/index.html， 获取出来的 Context路径不是 "/”， 那么要修正 uri 为 /index.html
        if (!"/".equals(context.getPath())){
            uri = StrUtil.removePrefix(uri, context.getPath());
        }
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
        if (!StrUtil.contains(temp, '?')){
            uri = temp;
            return;
        }
        this.uri = StrUtil.subBefore(temp, "?", false);
    }
    public String getUri(){
        return uri;
    }

    public String getRequestString() {
        return requestString;
    }
}
