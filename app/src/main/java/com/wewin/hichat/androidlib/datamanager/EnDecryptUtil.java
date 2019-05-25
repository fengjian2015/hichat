package com.wewin.hichat.androidlib.datamanager;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.wewin.hichat.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密解密
 * Created by Darren on 2018/7/31.
 */

public class EnDecryptUtil {

    private static String mUserWord;

    public static void init(Context context) {
        StringBuffer sb = new StringBuffer();
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        sb.append(getGBS(150, 160)).append("wans").append(context.getString(R.string.bk4));
        mUserWord = sb.toString();
    }

    private static int getGBS(int x, int y) {
        for (int i = 1; i <= x * y; i++) {
            if (i % x == 0 && i % y == 0) {
                return i;
            }
        }
        return x * y;
    }

    /**
     * AES加密
     */
    static byte[] encrypt(String content) {
        try {
            SecretKeySpec key = new SecretKeySpec(mUserWord.getBytes(), "AES/CBC/PKCS5PADDING");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(content.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES解密
     */
    static byte[] decrypt(byte[] content) {
        try {
            SecretKeySpec key = new SecretKeySpec(mUserWord.getBytes(), "AES/CBC/PKCS5PADDING");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String parseByte2HexStr(byte[] buf) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static byte[] obj2byteArr(Object obj) {
        try {
            //将Object转换成byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();
            byte[] byteArr = baos.toByteArray();
            baos.close();
            return byteArr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object byteArr2Obj(byte[] data) {
        try {
            return new ObjectInputStream(new ByteArrayInputStream(data)).readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
