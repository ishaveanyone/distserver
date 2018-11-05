package com.dist;



import com.alibaba.fastjson.JSON;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/9/27.
 */
public class ServerDealThread extends  Thread {
    private Socket socket;
    private PrintWriter writer;//输出流
    private BufferedReader reader;

    public ServerDealThread(Socket socket) {
        this.socket = socket;
        try {
            writer=new PrintWriter(new OutputStreamWriter( socket.getOutputStream(),"UTF-8"));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //
    @Override
    public void run(){
        String message=null;
        try {
            while((message = reader.readLine())!=null){
                System.out.println(message);
                deal(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void deal( String message) throws IOException {

        if(message.startsWith("open:")){
            writer.println("连接成功");
            writer.flush();
            return;
        }else if (message.startsWith("close:")){
            socket.close();
            writer.close();
            reader.close();
            return;
        }else if(message.startsWith("findByName:")) {
            System.out.println("名称处理");
            Map<Integer, String> smallMap = new HashMap<Integer, String>();

            String connent = message.substring(message.indexOf(":")+1);

            String command = new StringBuilder().append("cmd /c tasklist /v /fo csv | findstr /i ").append(connent).toString();
            String line = null;
//            StringBuilder sb = new StringBuilder();
            Runtime runtime = Runtime.getRuntime();

            Process process = runtime.exec(command);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK"));
            int index=0;
            while ((line = bufferedReader.readLine()) != null) {
//                sb.append(new StringBuilder().append(line).append("\n").toString());
//                System.out.println(line);
                smallMap.put(index,line);
                index++;
            }
           String jsonStr = JSON.toJSONString(smallMap);
            System.out.println(jsonStr);
            writer.println(jsonStr);
            writer.flush();
           return;
        }else if(message.startsWith("findByPort:")){
            System.out.println("端口处理");
            Map<Integer, String> smallMap = new HashMap<Integer, String>();
            List<String> list =new ArrayList<String>();
            String connent = message.substring(message.indexOf(":")+1);

            String command = new StringBuilder().append("cmd /c netstat -ano|findstr ").append(connent).toString();

            String line = null;
//            StringBuilder sb = new StringBuilder();
            Runtime runtime = Runtime.getRuntime();

            Process process = runtime.exec(command);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK"));
            int index=0;
            while ((line = bufferedReader.readLine()) != null) {
                line=line.trim();
                String regEx = "[' ']+";

                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(line);
                line= m.replaceAll(",").trim();
                if(isPort(line,connent)){
                    list.add(line.split(",")[4]);
                }
            }

            for(String str:list){
                String command2 = new StringBuilder().append("cmd /c tasklist /v /fo csv | findstr /i ").append(str).toString();
                String line2 = null;
                Runtime runtime2 = Runtime.getRuntime();
                Process process2 = runtime2.exec(command2);
                BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(process2.getInputStream(),"GBK"));
                while ((line2 = bufferedReader2.readLine()) != null) {
                    smallMap.put(index,line2);
                    index++;
                }
            }

            String jsonStr = JSON.toJSONString(smallMap);
            System.out.println(jsonStr);
            writer.println(jsonStr);
            writer.flush();
            return;
        }else if(message.startsWith("findByNameAndPort:")){
            System.out.println("端口名称处理");
            Map<Integer, String> smallMap = new HashMap<Integer, String>();
            List<String> list =new ArrayList<String>();
            String connent = message.substring(message.indexOf(":")+1);//获取数据
            String name = message.substring(0,connent.indexOf(":"));//获取名称
            String port = message.substring(connent.indexOf(":")+1);//获取端口
            String command = new StringBuilder().append("cmd /c netstat -ano|findstr ").append(port).toString();
            String line = null;
//            StringBuilder sb = new StringBuilder();
            Runtime runtime = Runtime.getRuntime();

            Process process = runtime.exec(command);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK"));
            int index=0;
            while ((line = bufferedReader.readLine()) != null) {
                line=line.trim();
                String regEx = "[' ']+";

                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(line);
                line= m.replaceAll(",").trim();
                if(isPort(line,connent)){
                    list.add(line.split(",")[4]);
                }
            }
            for(String str:list){
                String command2 = new StringBuilder().append("cmd /c tasklist /v /fo csv | findstr /i ").append(str).toString();
                String line2 = null;
                Runtime runtime2 = Runtime.getRuntime();
                Process process2 = runtime2.exec(command2);
                BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(process2.getInputStream(),"GBK"));
                while ((line2 = bufferedReader2.readLine()) != null) {
                    if(line2.contains(name)){
                        smallMap.put(index,line2);
                        index++;
                    }
                }
            }
            String jsonStr = JSON.toJSONString(smallMap);
            System.out.println(jsonStr);
            writer.println(jsonStr);
            writer.flush();
            return;
        }else if(message.startsWith("findProcess:")){
//            搜索线程
            Map<String,String> resultMap=new HashMap<String,String>();
            String keyword= message.substring(message.indexOf(":")+1);
            Properties properties = new Properties();
//            使用InPutStream流读取properties文件
            BufferedReader bufferedReader = new BufferedReader(new FileReader("c:/configexepath.properties"));
            properties.load(bufferedReader);
//            获取key对应的value值
            Set<String> keys= properties.stringPropertyNames();
            for(String key:keys){
                if(keyword==""&&!keyword.equals(key)){
                    continue;
                }
                resultMap.put(key,properties.getProperty(key));
            }
            String jsonStr = JSON.toJSONString(resultMap);
            System.out.println(jsonStr);
            writer.println(jsonStr);
            writer.flush();
            return;
        }else if(message.startsWith("restartExe:")){
            //通过进程的路径重启进程
            String exePath=message.substring(message.indexOf(":")+1);

            String command ="cmd /c start "+ new StringBuilder().append(exePath).toString();
            Runtime runtime = Runtime.getRuntime();
            try {
                Process process = runtime.exec(command);
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }else if(message.startsWith("findPathByExeName:")){
            //通过进程名称进程的安装路径
            try {
//                对数据封装成map回传
                Map<String,String> resultMap=new HashMap<String,String>();
                String exeName=message.substring(message.indexOf(":")+1);

                exeName="'"+exeName+"'";
                String command = new StringBuilder().append("cmd /c wmic process where name="+ exeName+" get executablepath").toString();
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec(command);
                String line = null;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK"));
                int index=0;
                while ((line = bufferedReader.readLine()) != null) {
                    if(line.contains("ExecutablePath")){
                        continue;
                    }else{
                        resultMap.put(String.valueOf(index),line);
                        index++;
                    }
                }
                String jsonStr = JSON.toJSONString(resultMap);
                System.out.println(jsonStr);
                writer.println(jsonStr);
                writer.flush();
                return;
            }catch (IOException e1)
            {
                e1.printStackTrace();
            }

        }

        else if(message.startsWith("kill:")){
            System.out.println("杀死进程");
            String connent = message.substring(message.indexOf(":")+1);
            String command = new StringBuilder().append("cmd /c taskkill /pid ").append(connent).append(" -t -f").toString();

            StringBuilder sb = new StringBuilder();
            Runtime runtime = Runtime.getRuntime();
            try {
                Process process = runtime.exec(command);
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
            return;
        }else{
            System.out.println("无效操作");
            return;
        }
    }



//    处理端口程序
    public  boolean isPort(String str,String vport){
        String address=str.split(",")[1];
        String port=address.substring(address.indexOf(":")+1);
        if(vport.equals(port)){
            return true;
        }else {
            return false;
        }
    }
}
