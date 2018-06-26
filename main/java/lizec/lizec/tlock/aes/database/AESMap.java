package lizec.lizec.tlock.aes.database;

import lizec.lizec.tlock.aes.exception.SameKeyException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class AESMap {
    private static final String KEY_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private Key key;
    private IvParameterSpec iv;
    private HashMap<String,String> database;

    @SuppressWarnings("unchecked")
    public AESMap(byte[] seed, byte[] raw) throws GeneralSecurityException, IOException, ClassNotFoundException {
        init(seed);

        printKey();
        // 解密
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key,iv);
        byte[] result = cipher.doFinal(raw);

        // 解密数据转化为哈希表
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(result));
        database = (HashMap<String, String>) in.readObject();
    }

    public AESMap(byte[] seed) throws GeneralSecurityException{
        init(seed);
        printKey();
        database = new HashMap<>();
    }

    /**
     * 根据用户输入的参数构造Key和IV
     * @param seed 用于构造参数的种子
     * @throws GeneralSecurityException 如果创建失败
     */
    private void init(byte[] seed) throws GeneralSecurityException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", new CryptoProvider());
        random.setSeed(seed);
        KeyGenerator generator = KeyGenerator.getInstance(KEY_ALGORITHM);
        generator.init(random);
        key = generator.generateKey();
        byte[] ivByte = new byte[16];
        random.nextBytes(ivByte);
        iv = new IvParameterSpec(ivByte);
    }

    /**
     * 获得指定键对应的值
     * @param key 待查询的键
     * @return 键对应的值, 如果指定的键不存在则返回null
     */
    public String get(String key){
        return database.get(key);
    }

    /**
     * 添加一个新的记录
     * @param key 待添加记录的键
     * @param value 待添加记录的值
     * @throws SameKeyException 如果存在同名的键,则抛出此异常
     */
    public void addPair(String key, String value) throws SameKeyException {
        if(database.containsKey(key)){
            throw new SameKeyException("待添加的键已存在");
        }
        else{
            database.put(key,value);
        }
    }

    /**
     * 更新一条记录
     * @param key 待更新记录的键
     * @param value 待更新记录的值
     * @throws lizec.lizec.tlock.aes.exception.NoSuchKeyException 如果表中不存在相应的记录,则抛出此异常
     */
    public void update(String key, String value) throws lizec.lizec.tlock.aes.exception.NoSuchKeyException {
        if(!database.containsKey(key)){
            throw new lizec.lizec.tlock.aes.exception.NoSuchKeyException("待更新的键不存在");
        }
        else{
            database.put(key,value);
        }
    }

    public Set<String> getAllKeys(){
        return database.keySet();
    }

    private void printKey(){
        System.out.println("Key = " + new BigInteger(1,key.getEncoded()).toString(16));
        System.out.println("Iv  = " + new BigInteger(1,iv.getIV()).toString(16));
    }

    public byte[] encode() throws IOException,GeneralSecurityException {
        ByteArrayOutputStream baOS = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baOS);

        out.writeObject(database);
        out.flush();

        byte[] data = baOS.toByteArray();
        out.close();
        baOS.close();

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE,key,iv);

        return cipher.doFinal(data);
    }
}
