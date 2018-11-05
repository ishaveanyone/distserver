package com.dist;



import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

/**
 * Created by Administrator on 2018/9/27.
 */
public class TestServer extends Thread {
    ServerSocket server = null;
    Socket socket = null;

    public TestServer(int port) {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run(){
        try{
            System.out.println("wait client connect...");
//            启动
            while(true){
//              此方法会阻塞，一直等待被连接，成功之后将实例化一个Socket
                socket=server.accept();
                System.out.println(socket.getInetAddress()+"connected");
//                开启事件的处理
                new ServerDealThread(socket).start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    //函数入口
    public static void main(String[] args) {

        System.out.println("11:".substring("11:".indexOf(":")+1));
//        Properties properties=new Properties();
//        int port=0;
//        try {
//            BufferedInputStream bufferedInputStream=new BufferedInputStream(new FileInputStream(new File("c:/config.properties")));
//            properties.load(bufferedInputStream);
//            port=Integer.valueOf(properties.getProperty("server.port"));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        TestServer server = new TestServer(port);
//        server.start();
    }
}
