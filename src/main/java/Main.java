import com.dist.TestServer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Administrator on 2018/9/28.
 */
public class Main {
    //函数入口
    public static void main(String[] args) {

        InputStream in = Main.class.getClassLoader().getResourceAsStream("server.properties");
        Properties properties=new Properties();
        int port=0;
        try {
            properties.load(in);
            port=Integer.valueOf(properties.get("server.listen.port").toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        TestServer server = new TestServer(port);
        server.start();
    }
}
