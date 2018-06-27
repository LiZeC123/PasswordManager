package lizec.lizec.tlock.rand;

import org.junit.Test;

import static org.junit.Assert.*;


public class RandomPasswordTest {

    @Test
    public void getOneNum()  {
        String BASE = "1234567890";
        RandomPassword rand = new RandomPassword();
        int len = 12;
        String pwd = rand.getOne(len,RandomPassword.ONLY_NUMBER);
        assertEquals(len,pwd.length());
        for(int i=0;i<pwd.length();i++){
            if(!BASE.contains(String.valueOf(pwd.charAt(i)))){
                throw new RuntimeException();
            }
        }
    }

    @Test
    public void getOneChar() {
        String BASE = "1234567890"+ "qwertyuiopasdfghjklzxcvbnm" + "QWERTYUIOPASDFGHJKLZXCVBNM";
        RandomPassword rand = new RandomPassword();
        int len = 24;
        String pwd = rand.getOne(len,RandomPassword.ONLY_CHAR_AND_NUM);
        assertEquals(len,pwd.length());
        for(int i=0;i<pwd.length();i++){
            if(!BASE.contains(String.valueOf(pwd.charAt(i)))){
                throw new RuntimeException();

            }
        }
    }


    @Test
    public void getOneAll() {
        String BASE = "!@#$%&*" + "1234567890"+ "qwertyuiopasdfghjklzxcvbnm" + "QWERTYUIOPASDFGHJKLZXCVBNM";
        RandomPassword rand = new RandomPassword();
        int len = 36;
        String pwd = rand.getOne(len,RandomPassword.ALL_TYPE);
        assertEquals(len,pwd.length());
        for(int i=0;i<pwd.length();i++){
            if(!BASE.contains(String.valueOf(pwd.charAt(i)))){
                throw new RuntimeException();

            }
        }
    }


}