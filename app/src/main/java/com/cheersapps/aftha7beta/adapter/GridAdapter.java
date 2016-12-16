package com.cheersapps.aftha7beta.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cheersapps.aftha7beta.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Mhamed on 13-12-16.
 */

public class GridAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<String> images;

    public GridAdapter(Context context,ArrayList<String> images) {
        this.mContext = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //TextView dummyTextView = new TextView(mContext);
        //dummyTextView.setText(String.valueOf(position));
        ImageView imgV = new ImageView(mContext);
        Picasso.with(mContext).load(images.get(position)).placeholder(R.drawable.please_wait).centerCrop().fit().into(imgV);
        return imgV;
    }
}
