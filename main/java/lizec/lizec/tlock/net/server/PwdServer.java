package lizec.lizec.tlock.net.server;

import lizec.lizec.tlock.file.FileHelper;
import lizec.lizec.tlock.net.command.CommonCmd;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 *
 */
public class PwdServer {
    private HashMap<String, CommonCmd> cmdHashMap;
    private ServerSocket serverSocket;
    private ObjectOutputStream out;
    private Socket socket;
    private Thread ioThread;


    /**
     * 构造一个服务端程序,默认监听8848端口
     * 如果端口被占用,则会尝试监听8849端口
     * @throws IOException 如果始终无法获得合适的端口,则抛出此异常
     */
    public PwdServer() throws IOException{
        try {
            serverSocket = new ServerSocket(8848);
        } catch (IOException e) {
            serverSocket = new ServerSocket(8849);
        }

        initHashMap();
    }

    private void initHashMap() {
        cmdHashMap = new HashMap<>();
        cmdHashMap.put("exit",doExit);
        cmdHashMap.put("fileResponse",doFileReceive);
    }

    /**
     * 获得本机的IP地址
     * @return 本机的IP地址
     * @throws UnknownHostException 如果无法获得主机名
     */
    public String getHostAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    /**
     * 获得本机监听的端口
     * @return 本机监听的端口
     */
    public int getHostPort() {
        return serverSocket.getLocalPort();
    }

    /**
     * 监听并连接一个客户端socket
     * @throws IOException 如果创建过程中发生错误,抛出此异常
     */
    public void accept() throws IOException {
        socket = serverSocket.accept();
        ioThread = new Thread(ioRunnable);
        ioThread.start();
        out = new ObjectOutputStream(socket.getOutputStream());
    }

    /**
     * 向客户端发送请求文件指令, 通过此指令可以获得客户端的数据文件
     * @throws IOException 如果传输过程中发生错误,抛出此异常
     */
    public void sendFileRequest() throws IOException{
        out.writeUTF("fileRequest");
        out.flush();
    }

    /**
     * 结束socket通信并尝试结束通信线程
     * 通过此函数可以安全的结束两端的通信线程
     * @throws IOException 如果传输过程中发生错误,抛出此异常
     */
    public void finish() throws IOException {
        out.writeUTF("exit");
        out.flush();
    }


    private Runnable ioRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                while (!Thread.interrupted()){
                    String cmd = in.readUTF();
                    System.out.println("Receive Cmd->"+cmd);
                    cmdHashMap.get(cmd).doCmd(in,out);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private CommonCmd doExit = (in, out) -> {
        try {
            out.writeUTF("exit");
            out.flush();
            socket.close();
            serverSocket.close();
            ioThread.interrupt();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return false;
    };

    private CommonCmd doFileReceive = (in, out) -> {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        String filename = f.format(Calendar.getInstance().getTime())+".data";
        File outFile = new File(filename);
        try {
            int len = in.readInt();
            byte[] bytes = new byte[len];
            int off = 0;
            while (off < len){
                System.out.println("off = "+ off);
                off += in.read(bytes,off,len);
            }
            FileHelper.write(outFile,bytes);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    };
}