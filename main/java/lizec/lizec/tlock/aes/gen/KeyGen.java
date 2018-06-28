package lizec.lizec.tlock.aes.gen;

import java.security.GeneralSecurityException;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class KeyGen {
    public static byte[] genPassword(List<byte[]> list) throws GeneralSecurityException {

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

        return hashPassword(pwd,salt);
    }


    private static byte[] hashPassword(String password, String salt) throws GeneralSecurityException {
        // 即使用SHA1作为散列函数的PBKDF2算法
        // http://www.rfc-editor.org/rfc/rfc2898.txt
        // https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#SecretKeyFactory
        // 可以通过调节迭代次数和长度来改变运算时间和破解难度
        final String algorithm = "PBKDF2WithHmacSHA1";
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 4096, 128);
        SecretKeyFactory kFactory=SecretKeyFactory.getInstance(algorithm);
        SecretKey secretKey = kFactory.generateSecret(pbeKeySpec);
        return secretKey.getEncoded();
    }
}
