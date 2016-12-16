package com.cheersapps.aftha7beta.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cheersapps.aftha7beta.R;
import com.darsh.multipleimageselect.models.Image;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Mhamed on 13-12-16.
 */

public class GridAlbumAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<Image> images;

    public GridAlbumAdapter(Context context, ArrayList<Image> images) {
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
        ImageView imgV = new ImageView(mContext);
        imgV.setImageURI(Uri.parse(images.get(position).path));
        //Picasso.with(mContext).load(Uri.parse(images.get(position).path)).placeholder(R.drawable.please_wait).centerCrop().fit().into(imgV);
        return imgV;
    }
}
