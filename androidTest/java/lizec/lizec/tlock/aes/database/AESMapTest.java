package lizec.lizec.tlock.aes.database;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.security.GeneralSecurityException;

import lizec.lizec.tlock.aes.exception.SameKeyException;
import lizec.lizec.tlock.model.PwdInfo;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AESMapTest {
    private static AESMap map;
    private static final String seed = "123456";

    @Before
    public void setUp() throws Exception {
        map = new AESMap(seed.getBytes());
    }

    @Test
    public void get() {
        PwdInfo info = new PwdInfo("APP","User","Pwd");
        try {
            map.addPair(info.getAPPName(),info);
        } catch (SameKeyException e) {
            throw new RuntimeException();
        }
        PwdInfo info2 = map.get(info.getAPPName());
        assertEquals(info,info2);
    }

    @Test
    public void addPair() throws Exception {
        PwdInfo info = new PwdInfo("APP","User","Pwd");
        map.addPair(info.getAPPName(),info);
        try{
            map.addPair(info.getAPPName(),info);
            throw new RuntimeException();
        } catch (SameKeyException e){
            // should throw exception, do nothing
        }

        PwdInfo info2 = map.get(info.getAPPName());
        assertEquals(info,info2);
    }

    @Test
    public void update() {
    }

    @Test
    public void getAllKeys() {
    }

    @Test
    public void encode() throws Exception {
        PwdInfo info = new PwdInfo("APP","User","Pwd");
        map.addPair(info.getAPPName(),info);

        info = new PwdInfo("BDD","User","Pwd");
        map.addPair(info.getAPPName(),info);

        byte[] bytes = map.encode();

        AESMap newMap = new AESMap(seed.getBytes(),bytes);

        PwdInfo newInfo = newMap.get(info.getAPPName());

        assertEquals(newInfo.getAPPName(),info.getAPPName());
        assertEquals(newInfo.getPwd(),info.getPwd());
        assertEquals(newInfo.getUserName(),info.getUserName());

    }
}