package cn.java.jerrymice.catalina;

import cn.hutool.log.LogFactory;
import cn.java.jerrymice.http.Request;
import cn.java.jerrymice.http.Response;
import cn.java.jerrymice.util.ThreadUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author xsl20
 */
public class Connector implements Runnable{

    int port;
    private Service service;

    public Service getService(){
        return service;
    }
    public void setPort(int port){
        this.port = port;
    }
    public Connector(Service service){
        this.service = service;
    }

    public void init(){
        LogFactory.get().info("Initializing Protocol [http-bio-{}]", port);
    }

    public void start(){
        LogFactory.get().info("Starting ProtocolHandler [http-bio-{}]",port);
        new Thread(this).start();
    }

    @Override
    public void run() {
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            while(true) {
                Socket socket = serverSocket.accept();
                Runnable r = () -> {
                    try {
                        Request request = new Request(socket, service);
                        Response response = new Response();
                        HttpServer httpServer = new HttpServer();
                        httpServer.execute(socket, request, response);
                    }catch (Exception e){
                        LogFactory.get().info(e.toString());
                    }finally {
                        // 将socket的关闭提取到最后
                        try{
                            socket.close();
                        }catch(IOException e){
                            LogFactory.get().info(e);
                        }
                    }
                };
                ThreadUtil.run(r);
            }
        }catch (IOException e) {
            LogFactory.get().info(e);
        }
    }

}
