package lizec.lizec.tlock.rand;

import java.util.Random;

public class RandomPassword {
    private static final String BASE = "qwertyuiopasdfghjklzxcvbnm" +
            "QWERTYUIOPASDFGHJKLZXCVBNM" + "1234567890";
	private Random random;
	public RandomPassword(){
		random = new Random();
	}

    /**
     * 获取一个长度为12字符的随机密码
     * @return 一个长度为12字符的随机密码
     */
	public String getOne(){
        return getOne(12);
	}

    /**
     * 获得指定长度的随机密码
     * @param len 密码的字符数
     * @return 指定长度的随机密码
     */
	public String getOne(int len){
	    int strLen = BASE.length();
        StringBuilder p = new StringBuilder();
        for(int i=0;i<len;i++){
            p.append(BASE.charAt(random.nextInt(strLen)));
        }

        return p.toString();
    }
	
	public static void main(String[] args){
		RandomPassword rpd = new RandomPassword();
		for(int i=0;i<10;i++){
			System.out.println(rpd.getOne());
		}
	}
	
}
