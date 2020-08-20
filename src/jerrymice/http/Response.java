package jerrymice.http;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @author ：xiaosong
 * @description：TODO
 * @date ：2020/8/3 21:43
 */
public class Response extends BaseResponse{
    private int status;
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
    private byte[] body;
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
        if (body == null) {
           String content = stringWriter.toString();
           body = content.getBytes("utf-8");

        }
        return body;
    }
    public void setContentType(String type){
        this.contentType = type;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public void setStatus(int status){
        this.status = status;
    }

    @Override
    public int getStatus(){
        return status;
    }


}
