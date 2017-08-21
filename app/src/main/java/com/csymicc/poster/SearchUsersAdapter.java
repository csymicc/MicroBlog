package com.csymicc.poster;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Micc on 8/21/2017.
 */

public class SearchUsersAdapter extends RecyclerView.Adapter<SearchUsersAdapter.ViewHolder> implements MsgType{

    private List<SearchUserContent> UserList = null;

    private Handler handler = null;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView username = null;
        Button plus = null;

        public ViewHolder(View view) {
            super(view);
            username = (TextView) view.findViewById(R.id.user_username);
            plus = (Button) view.findViewById(R.id.user_Follow);
        }

    }

    public SearchUsersAdapter(List<SearchUserContent> userList) {
        UserList = userList;
    }

    public void setHandler(Handler contextHandler) {
        handler = contextHandler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                SearchUserContent userContent = UserList.get(position);
                Message message = new Message();
                message.what = SEND_FOLLOW;
                message.arg1 = userContent.UID;
                handler.sendMessage(message);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SearchUserContent UserContent = UserList.get(position);
        holder.username.setText(UserContent.username);
    }

    @Override
    public int getItemCount() {
        return UserList.size();
    }

}
