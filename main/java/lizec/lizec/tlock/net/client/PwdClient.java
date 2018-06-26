package lizec.lizec.tlock.net.client;

import lizec.lizec.tlock.file.FileHelper;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class PwdClient {
    private Thread ioThread;
    private Socket socket;
    private File dataFile;
    private HashMap<String, lizec.lizec.tlock.net.command.CommonCmd> cmdHashMap;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public PwdClient(String host,int port) throws IOException {
        socket = new Socket(host,port);
        ioThread = new Thread(ioRunnable);
        ioThread.start();
        out = new ObjectOutputStream(socket.getOutputStream());
        initHashMap();
    }

    public void setSendFile(File file){
        dataFile = file;
    }

    private void initHashMap() {
        cmdHashMap = new HashMap<>();
        cmdHashMap.put("exit",doExit);
        cmdHashMap.put("fileRequest",doFileSend);
    }

    private Runnable ioRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                in = new ObjectInputStream(socket.getInputStream());
                while (!Thread.interrupted()){
                    String cmd = in.readUTF();
                    System.out.println("Receive Cmd->"+cmd);
                    cmdHashMap.get(cmd).doCmd(in,out);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private lizec.lizec.tlock.net.command.CommonCmd doExit = (in, out) -> {
        try {
            out.writeUTF("exit");
            out.flush();
            socket.close();
            ioThread.interrupt();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    };

    private lizec.lizec.tlock.net.command.CommonCmd doFileSend = (in, out) -> {
        try {
            out.writeUTF("fileResponse");
            byte[] bytes = FileHelper.readAllBytes(dataFile);
            out.writeInt(bytes.length);
            out.write(bytes);
            out.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    };

}
