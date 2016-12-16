package com.cheersapps.aftha7beta.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cheersapps.aftha7beta.R;
import com.cheersapps.aftha7beta.entity.Post;
import com.cheersapps.aftha7beta.holder.PostViewHolder;

import java.util.List;

/**
 * Created by Mhamed on 13-12-16.
 */

public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {

    List<Post> list;

    public PostAdapter(List<Post> list) {
        this.list = list;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_post,parent,false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post myObject = list.get(position);
        holder.bind(myObject);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
