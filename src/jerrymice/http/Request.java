package jerrymice.http;

import jerrymice.Bootstrap;
import jerrymice.catalina.Context;
import cn.hutool.core.util.StrUtil;
import jerrymice.catalina.Engine;
import jerrymice.catalina.Host;
import jerrymice.catalina.Service;
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
public class Request extends BaseRequest{
    private String requestString;
    private String uri;
    private Socket socket;
    private Context context;
    private Service service;

    public Context getContext() {
        return context;
    }

    /**
     * 根据request的uri来解析成Context
     */
    public void parseContext(){
        Engine engine = service.getEngine();
        // 根据获取到的uri去扫描得到的映射中去寻找这个文件夹,这样/b就能够获取匹配而不一定是/b/
        context = engine.getDefaultHost().getContext(uri);
        if (context != null) {
            return;
        }
        String path = StrUtil.subBetween(uri, "/", "/");
        if (null == path) {
            // 如果uri = /timeConsume.html，那么path = null， 不对uri进行改动
          path = "/";
        }
        else {
            // uri = /dir1/1.html, 那么path= dir1， 经过此处之后path=/dir1
            // uri = /b/, path = /b
            path = "/" + path;
        }
        context = engine.getDefaultHost().getContext(path);
        if (null == context){
            context = engine.getDefaultHost().getContext("/");
        }
    }


    /**
     * 构造方法
     */
    public Request(Socket socket, Service service) throws IOException {
        this.socket = socket;
        this.service = service;
        parseHttpRequest();
        if (StrUtil.isEmpty(requestString)){
            return;
        }
        parseUri();
        parseContext();
        // 比如 uri 是 /a/index.html， 获取出来的 Context路径不是 "/”， 那么要修正 uri 为 /index.html
        if (!"/".equals(context.getPath())){
            // 如果访问的地址是/a，那么经过remove之后uri就变为“”了，考虑到这种情况，让uri等于"/"
            uri = StrUtil.removePrefix(uri, context.getPath());
            if (StrUtil.isEmpty(uri)){
                uri = "/";
            }
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
