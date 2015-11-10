package com.hifivesoccer.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
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

        Bitmap b = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);

        if(storedBitmap != null){
            storedBitmap.recycle();
            storedBitmap = null;
        }
        storedBitmap = b;

        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, b.getWidth(), b.getHeight()), new RectF(0, 0, 600, 600), Matrix.ScaleToFit.CENTER);

        return Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
    }

}
