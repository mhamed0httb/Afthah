package com.cheersapps.aftha7beta.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cheersapps.aftha7beta.R;
import com.cheersapps.aftha7beta.entity.Comment;
import com.cheersapps.aftha7beta.entity.User;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Mhamed on 13-12-16.
 */

public class CommentCustomAdapter extends ArrayAdapter<Comment> {

    private final static String TAG = "CommentAdapter";
    private int resourceId = 0;
    private LayoutInflater inflater;
    Context context;
    FirebaseAuth mAuth;

    public CommentCustomAdapter(Context context, int resourceId, List<Comment> mediaItems) {
        super(context, 0, mediaItems);
        this.resourceId = resourceId;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        Firebase.setAndroidContext(context);
        mAuth = FirebaseAuth.getInstance();

        CommentCustomAdapter.CommentHolder holder = new CommentCustomAdapter.CommentHolder();

        if(view == null){
            view = inflater.inflate(resourceId, parent, false);
            holder.commentOwnerName = (TextView)view.findViewById(R.id.comment_owner_name);
            holder.commentDesc = (TextView)view.findViewById(R.id.comment_description);
            holder.commentOwnerImage = (ImageView)view.findViewById(R.id.comment_owner_image);
            holder.commentTime = (TextView)view.findViewById(R.id.comment_time);
            view.setTag(holder);
        }else{
            holder = (CommentCustomAdapter.CommentHolder) view.getTag();
        }

        final Comment comment = getItem(position);

        //GET OWNER DATA
        String ownerUid = comment.getOwner();
        Firebase ownerRef = new Firebase("https://aftha7-2a05e.firebaseio.com/users/" + ownerUid);
        final CommentHolder finalHolder = holder;
        ownerRef.addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                User ownerUser = dataSnapshot.getValue(User.class);
                //EVERYTHING ELSE HERE
                finalHolder.commentOwnerName.setText(ownerUser.getName());
                Picasso.with(context).load(ownerUser.getImage().toString()).resize(128,128)
                        .centerCrop().into(finalHolder.commentOwnerImage);
                //END EVERYTHING ELSE HERE
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        //END GET OWNER DATA
        //holder.commentOwnerName.setText(comment.getOwner().getName());
        finalHolder.commentDesc.setText(comment.getText());
        //holder.commentOwnerImage.setImageResource(R.drawable.emo48x48);


        Calendar c = Calendar.getInstance();
        int mounth = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int year = c.get(Calendar.YEAR);
        String todayDate = day + "/" + mounth + "/" + year;
        if(comment.getDate().equals(todayDate)){
            finalHolder.commentTime.setText(comment.getTime());
        }else{
            finalHolder.commentTime.setText(comment.getDate());
        }


        final String ownerNamee = finalHolder.commentOwnerName.getText().toString();
        finalHolder.commentOwnerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ownerNamee, Toast.LENGTH_SHORT).show();
            }
        });



        return view;
    }

    private class CommentHolder
    {

        TextView commentOwnerName, commentDesc, commentTime;
        ImageView commentOwnerImage;
    }
}
