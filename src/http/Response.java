package http;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

/**
 * @author ：xiaosong
 * @description：TODO
 * @date ：2020/8/3 21:43
 */
public class Response {
    /**
     * 用于存放返回的 html 文本
     */
    private StringWriter stringWriter;
    /**
     * writer可以直接调用write方法向页面中写html内容
     */
    private PrintWriter writer;
    /**
     *  Content-type ，默认是 "text/html"
     */
    private String contentType;
    public Response(){
        this.stringWriter = new StringWriter();
        this.writer = new PrintWriter(stringWriter);
        this.contentType = "text/html";
    }
    public String getContentType() {
        return contentType;
    }
    public PrintWriter getWriter(){
        return writer;
    }

    /**
     * 返回html的字符数组
     * @return
     * @throws UnsupportedEncodingException
     */
    public byte[] getBody() throws UnsupportedEncodingException {

        // 将stringWriter字符流转换成string，然后再转换成字符数组
        String content = stringWriter.toString();
        return content.getBytes();
    }
    public void setContentType(String type){
        this.contentType = type;
    }
}
