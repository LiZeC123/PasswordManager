package lizec.lizec.tlock.rand;

import java.util.Random;

public class RandomPassword {
    public static final int ONLY_NUMBER = 1;
    public static final int ONLY_CHAR_AND_NUM = 2;
    public static final int ALL_TYPE = 4;

	private Random random;
	public RandomPassword(){
		random = new Random();
	}

    /**
     * 获取一个长度为12字符的随机密码
     * @return 一个长度为12字符的随机密码
     */
	public String getOne(){
        return getOne(12,ONLY_CHAR_AND_NUM);
	}

    /**
     * 获得指定长度的随机密码
     * @param len 密码的字符数
     * @return 指定长度的随机密码
     */
	public String getOne(int len){
        return getOne(len,ONLY_CHAR_AND_NUM);
    }

    /**
     * 获得指定长度,指定类型的随机密码
     * @param len 密码的字符数
     * @param TYPE 密码的类型, 可以选择 RandomPassword.ONLY_NUMBER,
     *             RandomPassword.NLY_CHAR_AND_NUM 和 RandomPassword.ALL_TYPE
     * @return 指定要求的随机密码
     */
    public String getOne(int len, int TYPE){
	    String BASE = "";
        switch (TYPE){
            case ALL_TYPE:
                BASE += "!@#$%&*";
            case ONLY_CHAR_AND_NUM:
                BASE += "qwertyuiopasdfghjklzxcvbnm" + "QWERTYUIOPASDFGHJKLZXCVBNM";
            case ONLY_NUMBER:
                BASE += "0123456789";
            default:
                break;
        }

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
