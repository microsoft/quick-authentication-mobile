package com.example.signdemo1.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

public class ByteCodeUtil {
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
}
