package com.csymicc.poster;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Micc on 7/31/2017.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List<PostContent> PostList = null;

    private SimpleDateFormat Dateformat = new SimpleDateFormat("yyyy.MM.dd.hh.mm.ss");

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView portrait = null;
        TextView username = null;
        TextView time = null;
        TextView text = null;
        RecyclerView images = null;

        public ViewHolder(View view) {
            super(view);
            portrait = (ImageView) view.findViewById(R.id.main_user_portrait);
            username = (TextView) view.findViewById(R.id.main_username);
            time = (TextView) view.findViewById(R.id.main_time);
            text = (TextView) view.findViewById(R.id.main_text);
            images = (RecyclerView) view.findViewById(R.id.main_image_list);
        }

    }

    public PostAdapter(List<PostContent> postList) {
        PostList = postList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_post, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PostContent postContent = PostList.get(position);
        holder.portrait.setImageResource(postContent.portrait);
        holder.username.setText(postContent.username);
        String currentTime = Dateformat.format(Calendar.getInstance().getTime());
        String timeDistance = "0 seconds before";
        if(currentTime.compareTo(postContent.time) > 0) {
            String[] pastTimepiece = postContent.time.split("\\.");
            String[] curTimepiece = currentTime.split("\\.");
            for (int i = 0; i != pastTimepiece.length; ++i) {
                Log.d("PostAdapter: ", curTimepiece[i]);
                Log.d("PostAdapter: ", pastTimepiece[i]);
                int diff = Integer.valueOf(curTimepiece[i]) - Integer.valueOf(pastTimepiece[i]);
                if (diff > 0) {
                    timeDistance = Integer.toString(diff);
                    switch (i) {
                        case 0:
                            timeDistance += diff == 1 ? " year ago" : " years ago";
                            break;
                        case 1:
                            timeDistance += diff == 1 ? " month ago" : " months ago";
                            break;
                        case 2:
                            timeDistance += diff == 1 ? " day ago" : " days ago";
                            break;
                        case 3:
                            timeDistance += diff == 1 ? " hour ago" : " hours ago";
                            break;
                        case 4:
                            timeDistance += diff == 1 ? " minute ago" : " minutes ago";
                            break;
                        case 5:
                            timeDistance += diff == 1 ? " second ago" : " seconds ago";
                            break;
                        default:
                            break;
                    }
                    break;
                }
            }
        }
        holder.time.setText(timeDistance);
        holder.text.setText(postContent.text);
        if(postContent.images.isEmpty()) {
            holder.images.setVisibility(View.GONE);
        } else {
            holder.images.setVisibility(View.VISIBLE);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            holder.images.setLayoutManager(layoutManager);
            PostImagesAdapter postImagesAdapter = new PostImagesAdapter(postContent.images, postContent.UID);
            holder.images.setAdapter(postImagesAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return PostList.size();
    }

}
