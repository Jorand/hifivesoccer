package com.hifivesoccer.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class base64ToBitmap {

    private static Bitmap storedBitmap = null;

    private base64ToBitmap(){

    }

    public static Bitmap getBitmap(String base64image) {
        return getBitmap(base64image, 8);
    }

    public static Bitmap getBitmap(String base64image, int inSampleSize) {

        byte[] decodedString = Base64.decode(base64image.getBytes(), Base64.DEFAULT);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        Bitmap bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);

        if(storedBitmap != null){
            storedBitmap.recycle();
            storedBitmap = null;
        }
        storedBitmap = bm;

        return bm;
    }

}
