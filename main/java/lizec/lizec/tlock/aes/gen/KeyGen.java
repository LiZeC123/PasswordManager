package lizec.lizec.tlock.aes.gen;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.util.List;

public class KeyGen {

    public static  byte[] genPassword(List<byte[]> list) throws GeneralSecurityException {
        //String s = new String(list.get(1));
        byte[] b0 = list.get(0);
        int len0 = b0.length > 1024? 1024:b0.length;

        final String salt = new String(b0,0,len0);

        int count = list.size();
        StringBuilder p = new StringBuilder();
        for(int i=1;i<count;i++){
            byte[] bytes = list.get(i);
            int len = bytes.length > 1024? 1024:bytes.length;

            p.append(new String(bytes, 0, len));
        }
        String pwd = p.toString();
        return hashPassword(pwd,salt,4096,128);
    }


    private static byte[] hashPassword(String password, String salt, int iterations, int len) throws GeneralSecurityException {
        // 即使用SHA1作为散列函数的PBKDF2算法
        // http://www.rfc-editor.org/rfc/rfc2898.txt
        // https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#SecretKeyFactory
        final String algorithm = "PBKDF2WithHmacSHA1";
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), iterations, len);
        SecretKeyFactory kFactory=SecretKeyFactory.getInstance(algorithm);
        SecretKey secretKey = kFactory.generateSecret(pbeKeySpec);
        return secretKey.getEncoded();
    }
}
