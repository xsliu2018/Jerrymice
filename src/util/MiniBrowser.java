package util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author ：xiaosong
 * @description：模拟实现一个小型的浏览器，通过这个浏览器与服务器进行通信
 * @date ：2020/7/28 21:22
 */
public class MiniBrowser {
    public static void main(String[] args){
        String url = "http://static.how2j.cn/diytomcat.html";
        String contentString = getContentString(url);
        System.out.println(contentString);
        String httpString = getHttpString(url);
        System.out.println(httpString);
    }

    public static String getContentString(String url){
        return getContentString(url, false);
    }
    public static String getContentString(String url, boolean gzip){
        byte[] result = getContentBytes(url, gzip);
        if (result == null) {
            return null;
        }
        return new String(result, StandardCharsets.UTF_8).trim();
    }

    /**
     *
     * @param url:访问的链接地址
     * @return byte[]:一个字节数组，是二进制的http的响应内容，可以简单理解为去掉头的html部分
     */
    public static byte[] getContentBytes(String url){
        return getContentBytes(url, false);
    }
    public static byte[] getContentBytes(String url, boolean gzip){
        byte[] response = getHttpBytes(url, gzip);
        byte[] doubleReturn = "\r\n\r\n".getBytes();

        int pos = -1;
        for (int i = 0; i< response.length - doubleReturn.length; i++) {
            byte[] temp = Arrays.copyOfRange(response, i, i + doubleReturn.length);
            if (Arrays.equals(temp, doubleReturn)) {
                pos = i;
                break;
            }
        }
        if (pos == -1){
            return null;
        }
        pos += doubleReturn.length;

        return Arrays.copyOfRange(response, pos, response.length);

    }
    public static String getHttpString(String url, boolean gzip) {
        byte[] bytes = getHttpBytes(url, gzip);
        return new String(bytes).trim();
    }
    public static String getHttpString(String url) {
        return getHttpString(url, false);
    }
    public static byte[] getHttpBytes(String url, boolean gzip){
        byte[] result;
        try {
            URL u = new URL(url);
            Socket clientSocket = new Socket();
            int port = u.getPort();
            if (port == -1) {
                port = 80;
            }
            InetSocketAddress inetSocketAddress = new InetSocketAddress(u.getHost(), port);
            clientSocket.connect(inetSocketAddress, 1000);
            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("Host", u.getHost() + ":" + port);
            requestHeaders.put("Accept","text/html");
            requestHeaders.put("Connection", "close");
            requestHeaders.put("User-Agent", "how2j mini browser / jdk1.8.0_251");
            if (gzip) {
                requestHeaders.put("Accept-Encoding", "gzip");
            }
            String path = u.getPath();
            if (path.length() == 0 ){
                path = "/";
            }
            String firstLine = "GET " + path + " HTTP/1.1\r\n";
            StringBuffer httpRequestString = new StringBuffer();
            httpRequestString.append(firstLine);
            Set<String> headers = requestHeaders.keySet();
            for (String header: headers) {
                String headerLine = header + ":" + requestHeaders.get(header) + "\r\n";
                httpRequestString.append(headerLine);
            }
            PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            printWriter.println(httpRequestString);
            InputStream inputStream = clientSocket.getInputStream();
            int bufferSize = 1024;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[bufferSize];
            while(true) {
                int length = inputStream.read(buffer);
                if (length == -1) {
                    break;
                }
                byteArrayOutputStream.write(buffer, 0, length);
                if (length != bufferSize) {
                    //说明已经到了结尾
                    break;
                }
            }
            result = byteArrayOutputStream.toByteArray();
            clientSocket.close();
        }catch(Exception e) {
            e.printStackTrace();
            result = e.toString().getBytes(StandardCharsets.UTF_8);
        }
        return result;
    }
}
