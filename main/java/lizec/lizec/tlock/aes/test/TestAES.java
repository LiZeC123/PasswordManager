package lizec.lizec.tlock.aes.test;

import lizec.lizec.tlock.aes.database.AESMap;
import lizec.lizec.tlock.aes.gen.KeyGen;
import lizec.lizec.tlock.file.FileHelper;

import java.io.File;
import java.util.ArrayList;

public class TestAES {
    public static void main(String[] args) throws  Exception{
        ArrayList<byte[]> list = new ArrayList<>();
        list.add("LiZeC".getBytes());
        list.add("Tomcat".getBytes());
        list.add("Java".getBytes());

        byte[] pwd = KeyGen.genPassword(list);
        File datafile = new File("idata.data");

        AESMap map = new AESMap(pwd);

        FileHelper.write(datafile,map.encode());

        byte[] data = FileHelper.readAllBytes(datafile);

        AESMap map2 = new AESMap(pwd,data);
    }
}
