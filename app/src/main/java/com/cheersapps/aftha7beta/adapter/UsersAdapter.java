package com.cheersapps.aftha7beta.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.cheersapps.aftha7beta.R;
import com.cheersapps.aftha7beta.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Mohamed on 12/5/2016.
 */

public class UsersAdapter extends ArrayAdapter<User> {

    private final static String TAG = "UserAdapter";
    private int resourceId = 0;
    private LayoutInflater inflater;
    Context context;

    FirebaseAuth mAuth;
    public UsersAdapter(Context context, int resourceId, List<User> mediaItems) {

        super(context, 0, mediaItems);

        this.resourceId = resourceId;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;


        UsersAdapter.UserHolder holder = new UsersAdapter.UserHolder();

        if(view == null){
            view = inflater.inflate(resourceId, parent, false);
            holder.msgName = (TextView)view.findViewById(R.id.msgName);
            holder.msgPics = (ImageView) view.findViewById(R.id.msgPic);


            view.setTag(holder);
        }else{
            holder = (UsersAdapter.UserHolder) view.getTag();
        }

        User usr = getItem(position);
        holder.msgName.setText(usr.getName());
        Picasso.with(context).load(usr.getImage().toString()).transform(new CircleTransform()).centerCrop()
                .resize(120,120).into(holder.msgPics);




        return view;
    }



    private class UserHolder
    {

        TextView msgName;
        ImageView msgPics;


    }
}
