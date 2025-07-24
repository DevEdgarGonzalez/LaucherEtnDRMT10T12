package com.actia.mapas;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class AESUtil {
//    private byte[] ENCRYPTION_KEY;
//    private String ENCRYPTION_IV;

//    public void setENCRYPTION_IV(String ENCRYPTION_IV) {
//        this.ENCRYPTION_IV = ENCRYPTION_IV;
//    }
//
//    public void setENCRYPTION_KEY(byte[] ENCRYPTION_KEY) {
//        this.ENCRYPTION_KEY = ENCRYPTION_KEY;
//    }

//    public String encrypt(String src) {
//        try {
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            cipher.init(Cipher.ENCRYPT_MODE, makeKey(), makeIv());
//            return Base64.encodeBytes(cipher.doFinal(src.getBytes()));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

//    public String decrypt(String src) {
//        String decrypted = "";
//        try {
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            cipher.init(Cipher.DECRYPT_MODE, makeKey(), makeIv());
//            decrypted = new String(cipher.doFinal(Base64.decode(src)));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        return decrypted;
//    }

    @SuppressWarnings("UnusedAssignment")
    public static String decrypt(String src, SecretKey key, byte[] iv) {
        String decrypted = "";
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            decrypted = new String(cipher.doFinal(Base64.decode(src)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return decrypted;
    }

//    private AlgorithmParameterSpec makeIv() {
//        try {
//            byte[] iv = Utils.HexStringTobyteArray(ENCRYPTION_IV);
//            return new IvParameterSpec(iv);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    private Key makeKey() {
//        byte[] key = ENCRYPTION_KEY;
//        return new SecretKeySpec(key, "AES");
//    }
}
