package com.csymicc.poster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by Micc on 7/31/2017.
 */

public class PostImagesAdapter extends RecyclerView.Adapter<PostImagesAdapter.ViewHolder> {

    private List<Integer> ImageList = null;

    private String imgdir = null;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image = null;

        public ViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.main_images_post);
        }

    }

    public PostImagesAdapter(List<Integer> imageList, int UID) {
        imgdir = new StringBuilder("/data/data/com.csymicc.poster/imagePost/").append(UID).append('/').toString();
        ImageList = imageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.images_post, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Integer ImageNo = ImageList.get(position);
        String imgPath = new StringBuilder(imgdir).append(ImageList.get(position)).append(".png").toString();
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(imgPath));
            holder.image.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return ImageList.size();
    }

}
