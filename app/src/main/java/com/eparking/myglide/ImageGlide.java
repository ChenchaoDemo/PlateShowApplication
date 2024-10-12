package com.eparking.myglide;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageGlide {
    private Context context;
    private static ImageGlide imageGlide;
    private ImageGlide(Context context){
        this.context=context;
    }
    public static ImageGlide getInstance(Context context){
        if(imageGlide==null){
            synchronized (ImageGlide.class){
                if(imageGlide==null)imageGlide=new ImageGlide(context);
            }
        }
        return imageGlide;
    }

    public void load(String path, ImageView view) {
        Glide.with(context).load(path).into(view);
    }
    public void load(byte[] data, ImageView view) {
        Glide.with(context).load(data).into(view);
    }
    public void clearAllMemoryCaches() {
        Glide.get(context).clearMemory();
    }
    public void trimMemory(int level){
        Glide.get(context).trimMemory(level);
    }
}
