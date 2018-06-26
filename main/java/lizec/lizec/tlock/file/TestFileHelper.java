package lizec.lizec.tlock.file;

import lizec.lizec.tlock.aes.database.AESMap;
import lizec.lizec.tlock.rand.RandomPassword;

import java.io.File;
import java.util.Scanner;

public class TestFileHelper {


    public static void main(String[] args) throws Exception{
        Scanner in = new Scanner(System.in);
        RandomPassword rand = new RandomPassword();
        String key = "LiZeC";
        File file = new File("a.data");

        byte[] raw = lizec.lizec.tlock.file.FileHelper.fileToByte(file);
        AESMap userMap = new AESMap(key.getBytes(),raw);

        while (true){
            String line = in.nextLine();
            switch (line) {
                case "exit":
                    lizec.lizec.tlock.file.FileHelper.byteToFile(file,userMap.encode());
                    return;
                case "new":
                    userMap.addPair(rand.getOne(4), rand.getOne());
                    break;
                case "list":
                    for (String pk : userMap.getAllKeys()) {
                        System.out.printf("Key:%s Pwd:%s\n", pk, userMap.get(pk));
                    }
                    break;
            }
        }
    }

    public static void newAndTest() throws Exception{
        Scanner in = new Scanner(System.in);
        String key = "LiZeC";

        AESMap map = new AESMap(key.getBytes());
        map.addPair("Baidu.com","NiHao");
        File file = new File("a.data");
        lizec.lizec.tlock.file.FileHelper.byteToFile(file,map.encode());

        System.out.println("请输入密码:");
        String userKey = in.nextLine();

        byte[] raw = lizec.lizec.tlock.file.FileHelper.fileToByte(file);
        AESMap userMap = new AESMap(userKey.getBytes(),raw);
        System.out.println("Baidu.com的密码是"+userMap.get("Baidu.com"));
    }

}
