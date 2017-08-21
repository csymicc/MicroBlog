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
 * Created by Micc on 8/3/2017.
 */

public class CameraImageAdapter extends RecyclerView.Adapter<PostImagesAdapter.ViewHolder> {

    private List<Bitmap> ImageList = null;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image = null;

        public ViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.main_images_post);
        }

    }

    public CameraImageAdapter(List<Bitmap> imageList) {
        ImageList = imageList;
    }

    @Override
    public PostImagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.images_post, parent, false);
        PostImagesAdapter.ViewHolder holder = new PostImagesAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PostImagesAdapter.ViewHolder holder, int position) {
        holder.image.setImageBitmap(ImageList.get(position));
    }

    @Override
    public int getItemCount() {
        return ImageList.size();
    }

}
