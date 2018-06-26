package lizec.lizec.tlock.net.test;

import lizec.lizec.tlock.net.client.PwdClient;

import java.io.File;

public class TestClient {
    public static void main(String[] args) throws Exception{
        String host = "192.168.1.105";
        int port = 8848;
        System.out.println("创建Client");
        PwdClient client = new PwdClient(host,port);
        System.out.println("设置文件名");
        client.setSendFile(new File("a.data"));
    }
}
