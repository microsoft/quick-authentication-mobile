package com.microsoft.quick.auth.signin.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteCodeUtil {
    private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
    private static final int BUFFER_SIZE = 16384;

    /**
     * Convert image byte to base64 string
     *
     * @param bytes Byte data of image
     * @return Base64 String
     */
    public static String byte2Base64(byte[] bytes) {
        if (null == bytes) return null;
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    /**
     * Convert Base64 string to bitmap
     *
     * @param base64 base64 string
     * @return Bitmap
     */
    public static Bitmap base642Bitmap(String base64) {
        if (TextUtils.isEmpty(base64)) return null;
        try {
            byte[] decode = Base64.decode(base64, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decode, 0, decode.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int numRead = 0;
        byte[] data = new byte[BUFFER_SIZE];
        while ((numRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, numRead);
        }
        return buffer.toByteArray();
    }
}
