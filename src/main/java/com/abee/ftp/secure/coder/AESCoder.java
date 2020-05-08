package com.abee.ftp.secure.coder;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;



public class AESCoder {
    public static final String KEY_ALGORITHM = "AES";

    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    private byte[] byteKey;

    public AESCoder() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
            keyGenerator.init(new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            this.byteKey = secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            this.byteKey = null;
        }

    }

    public static byte[] encrypt(byte[] data, byte[] byteKey) {
        try {
            Key key = new SecretKeySpec(byteKey, KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] decrypt(byte[] data, byte[] byteKey) {
        try {
            Key key = new SecretKeySpec(byteKey, KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    public static void main(String[] args) {
        jdkAES();
    }

    public static void jdkAES() {
        byte[] byteKey = new AESCoder().getByteKey();
        System.out.println("公钥长度：" + byteKey.length);
        System.out.println("公钥：" + Arrays.toString(byteKey));

        byte[] buffer = {-62, -127, -55, 82, -50, -11, -105, -123, 58,  112, 40, 12, 90, 41, -54, 84};

        byte[] t = AESCoder.encrypt(buffer, byteKey);
        System.out.println("加密后：" + Hex.encodeHexString(t) +
                "长度：" + t.length);

        byte[] result = AESCoder.decrypt(t, byteKey);
        System.out.println("解密后：" + new String(result));

    }

    public byte[] getByteKey() {
        return byteKey;
    }

    public void setByteKey(byte[] byteKey) {
        this.byteKey = byteKey;
    }
}

