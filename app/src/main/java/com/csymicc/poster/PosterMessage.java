package com.csymicc.poster;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Micc on 7/26/2017.
 */

public class PosterMessage implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int type = -1;

    private String time = null;

    private String text = null;
    private int textnum = 0;

    private List<byte[]> images = null;
    private List<Integer> imagesNo = null;

    public PosterMessage(int Type) {
        type = Type;
    }

    public PosterMessage(int Type, List<Bitmap> Images) {
        type = Type;
        if(Images.isEmpty()) return;
        images = new ArrayList<>();
        for(Bitmap bmp : Images) {
            Log.d("BitMap: ", "size: " + bmp.getByteCount());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            images.add(byteArray);
        }
    }

    public PosterMessage(int Type, String Time, String Text, List<String> filenames, List<Integer> ImagesNo) {
        type = Type;
        time = Time;
        text = Text;
        if(filenames == null) return;

        images = new ArrayList<>();
        imagesNo = ImagesNo;

        try {
            InputStream in = null;
            for(String file : filenames) {
                if(file != null && !file.isEmpty()) {
                    in = new FileInputStream(file);
                    byte[] image = new byte[in.available()];
                    in.read(image);
                    images.add(image);
                    in.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getType() {
        return type;
    }

    public void serializeText(String[] data) {
        if(data == null) return;
        StringBuilder str = new StringBuilder();
        for(String s : data) {
            if(s != null) {
                str.append(s.length()).append('/').append(s);
                ++textnum;
            }
        }
        text = str.toString();
    }

    public String[] deserializeText() {
        String[] data = new String[textnum];
        if(text == null) return data;
        int start = 0;
        for(int i = 0; i != textnum; ++i) {
            int slash = text.indexOf('/', start);
            int lens = Integer.valueOf(text.substring(start, slash));
            data[i] = text.substring(slash + 1, slash + lens + 1);
            start = slash + lens + 1;
        }
        return data;
    }

    public String getText() {
        return text;
    }

    public List<byte[]> getImage() {
        return images;
    }

    public List<Integer> getImageNo() {
        return imagesNo;
    }

    public void setTime(String Time) {
        time = Time;
    }

    public void show(String TAG) {
        Log.d(TAG, "mess type " + type);
        String[] data = deserializeText();
        Log.d(TAG, "mess size " + textnum);
        for (int i = 0; i != textnum; ++i) {
            Log.d(TAG, "mess data " + i + ' ' + data[i]);
        }
        if (images != null) {
            Log.d(TAG, "mess images numbers " + images.size());
        }
    }

}
