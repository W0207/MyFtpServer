package com.abee.ftp.secure;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xincong yao
 */
public class KeyStore {

    private static Map<String, byte[]> keys = new HashMap<>(128);

    public static boolean add(String ip, byte[] key) {
        keys.put(ip, key);
        return true;
    }

    public static Map<String, byte[]> getKeys() {
        return keys;
    }

    public static void setKeys(Map<String, byte[]> keys) {
        KeyStore.keys = keys;
    }
}
