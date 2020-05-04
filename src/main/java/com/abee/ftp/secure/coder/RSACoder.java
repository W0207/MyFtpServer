package com.abee.ftp.secure.coder;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


/**
 * @author xincong yao
 */
public class RSACoder {
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * Unit: bit
     */
    private static final int KEY_SIZE = 512;

    private byte[] publicKey;

    private byte[] privateKey;

    public RSACoder() {
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            return;
        }

        /**
         * initialize
         */
        keyPairGenerator.initialize(KEY_SIZE);
        /**
         * generate
         */
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        this.privateKey = privateKey.getEncoded();
        this.publicKey = publicKey.getEncoded();
    }

    /**
     * @param data    raw data
     * @param key     private key
     * @return byte[] encrypted data
     */
    public static byte[] encryptByPrivateKey(byte[] data, byte[] key) throws Exception {

        //取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        //数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * @param data    raw data
     * @param key     public key
     * @return byte[] encrypted data
     */
    public static byte[] encryptByPublicKey(byte[] data, byte[] key) throws Exception {

        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //初始化公钥
        //密钥材料转换
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
        //产生公钥
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);

        //数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return cipher.doFinal(data);
    }

    /**
     * @param data    encrypted data
     * @param key     private key
     * @return byte[] raw data
     */
    public static byte[] decryptByPrivateKey(byte[] data, byte[] key) throws Exception {
        //取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        //数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * @param data    encrypted data
     * @param key     public key
     * @return byte[] raw data
     */
    public static byte[] decryptByPublicKey(byte[] data, byte[] key) throws Exception {

        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //初始化公钥
        //密钥材料转换
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
        //产生公钥
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        //数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, pubKey);
        return cipher.doFinal(data);
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        RSACoder rsaCoder = new RSACoder();

        //公钥
        byte[] publicKey = rsaCoder.getPublicKey();

        //私钥
        byte[] privateKey = rsaCoder.getPrivateKey();

        System.out.println(publicKey.length + " " + privateKey.length);

        System.out.println("公钥：\n" + Base64.encodeBase64String(publicKey));
        System.out.println("私钥：\n" + Base64.encodeBase64String(privateKey));

        System.out.println("================密钥对构造完毕,甲方将公钥公布给乙方，开始进行加密数据的传输=============");
        String str = "RSA密码交换算法";
        System.out.println("\n===========甲方向乙方发送加密数据==============");
        System.out.println("原文:" + str);
        //甲方进行数据的加密
        byte[] code1 = RSACoder.encryptByPrivateKey(str.getBytes(), privateKey);
        System.out.println("加密后数据长度(B):" + code1.length);
        System.out.println("加密后的数据：" + Base64.encodeBase64String(code1));
        System.out.println("===========乙方使用甲方提供的公钥对数据进行解密==============");
        //乙方进行数据的解密
        byte[] decode1 = RSACoder.decryptByPublicKey(code1, publicKey);
        System.out.println("乙方解密后的数据：" + new String(decode1) + "\n\n");

        System.out.println("===========反向进行操作，乙方向甲方发送数据==============\n\n");

        //SIZE2048b 长度不能超过245bytes
        str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        System.out.println("原文:" + str + "， 长度：" + str.getBytes().length);

        //乙方使用公钥对数据进行加密
        byte[] code2 = RSACoder.encryptByPublicKey(str.getBytes(), publicKey);
        System.out.println("===========乙方使用公钥对数据进行加密==============");
        System.out.println("加密后的数据：" + Base64.encodeBase64String(code2) + " 长度：" + code2.length);

        System.out.println("=============乙方将数据传送给甲方======================");
        System.out.println("===========甲方使用私钥对数据进行解密==============");

        //甲方使用私钥对数据进行解密
        byte[] decode2 = RSACoder.decryptByPrivateKey(code2, privateKey);

        System.out.println("甲方解密后的数据：" + new String(decode2));
    }
}