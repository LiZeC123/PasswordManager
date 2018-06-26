package lizec.lizec.tlock.net.test;

public class TestServer {
    public static void main(String[] args) throws Exception{

        lizec.lizec.tlock.net.server.PwdServer server = new lizec.lizec.tlock.net.server.PwdServer();
        System.out.println("请在手机端输入以下信息:");
        System.out.println("IP地址:"+server.getHostAddress());
        System.out.println("端口:"+server.getHostPort());

        server.accept();
        System.out.println("Accept!");

        System.out.println("开始休眠5s");
        Thread.sleep(3000);
        System.out.println("休眠完成");

        server.sendFileRequest();
        System.out.println("Finish Send File Request");

        server.finish();
        System.out.println("total Finish");


    }
}
