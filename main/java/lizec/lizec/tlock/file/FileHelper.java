package lizec.lizec.tlock.file;


import java.io.*;

public class FileHelper {
    public static byte[] fileToByte(File file) throws IOException{
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        byte[] b = new byte[1024];
        int len;
        while((len = fis.read(b)) != -1) {
            bos.write(b, 0, len);
        }

        return bos.toByteArray();
    }

    public static void byteToFile(File file, byte[] bytes) throws IOException {
        FileOutputStream fout = new FileOutputStream(file);
        fout.write(bytes);
    }

    public static byte[] readAllBytes(File file) throws IOException{
        return fileToByte(file);
    }

    public static void write(File file, byte[] bytes) throws IOException {
        byteToFile(file,bytes);
    }
}
