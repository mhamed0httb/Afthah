package com.cheersapps.aftha7beta.holder;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.cheersapps.aftha7beta.R;
import com.cheersapps.aftha7beta.adapter.GridAdapter;
import com.cheersapps.aftha7beta.entity.Like;
import com.cheersapps.aftha7beta.entity.Media;
import com.cheersapps.aftha7beta.entity.Post;
import com.cheersapps.aftha7beta.entity.User;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Mhamed on 13-12-16.
 */

public class PostViewHolder extends RecyclerView.ViewHolder {

    private TextView ownerName, postDesc, postTime;
    private ImageView ownerImage,postMedia;
    private ImageButton btnComment;
    private ImageButton btnLike;
    private ImageButton btnViwMap;
    private VideoView postVideo;
    private ImageButton btnStartVideo,btnStopVideo;
    LinearLayout videoHolder;

    View mView;

    FirebaseAuth mAuth;
    Context context;

    public PostViewHolder(View itemView) {
        super(itemView);

        itemView = mView;

        context = itemView.getContext();


        //Firebase.setAndroidContext(ownerName.getContext());
        mAuth = FirebaseAuth.getInstance();

        ownerName = (TextView)itemView.findViewById(R.id.post_owner_name);
        postDesc = (TextView)itemView.findViewById(R.id.post_description);
        ownerImage = (ImageView)itemView.findViewById(R.id.post_owner_image);
        btnComment = (ImageButton)itemView.findViewById(R.id.btn_comment_post);
        btnLike = (ImageButton)itemView.findViewById(R.id.btn_like_post);
        postMedia = (ImageView)itemView.findViewById(R.id.post_media);
        postTime = (TextView)itemView.findViewById(R.id.post_time);
        //btnViwMap = (ImageButton)itemView.findViewById(R.id.btn_view_post_map);
        postVideo = (VideoView)itemView.findViewById(R.id.post_video);
        btnStartVideo = (ImageButton)itemView.findViewById(R.id.btn_start_post_video);
        btnStopVideo = (ImageButton)itemView.findViewById(R.id.btn_stop_post_video);
        videoHolder = (LinearLayout)itemView.findViewById(R.id.video_holder);
    }

    public void bind(final Post myObject){
        //GET OWNER DATA
        String ownerUid = myObject.getOwner();
        Firebase ownerRef = new Firebase("https://aftha7-2a05e.firebaseio.com/users/" + ownerUid);
        ownerRef.addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                User ow = dataSnapshot.getValue(User.class);
                ownerName.setText(ow.getName());
                Picasso.with(context).load(ow.getImage().toString()).resize(128,128)
                        .centerCrop().into(ownerImage);
                //EVERYTHING ELSE HERE
                //END EVERYTHING ELSE HERE
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        //END GET OWNER DATA
        //int layoutWidth = postMedia.getWidth();
        postDesc.setText(myObject.getDescription());
        if(myObject.getMediaType().equals("NOFILE")){
            Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/white.png?alt=media&token=7c98551c-e474-430c-956e-0f78715ea30f").resize(1, 1)
                    .centerCrop().into(postMedia);
            videoHolder.setVisibility(View.GONE);
        }else if(myObject.getMediaType().equals("IMAGE")){
            Picasso.with(context).load(myObject.getMedia()).placeholder(R.drawable.please_wait).centerCrop().fit().into(postMedia);
            videoHolder.setVisibility(View.GONE);
        }else if(myObject.getMediaType().equals("VIDEO")){
            videoHolder.setVisibility(View.VISIBLE);
            String pathVideo = myObject.getMedia();
            Uri uriVideo = Uri.parse(pathVideo);
            postVideo.setVideoURI(uriVideo);
            postVideo.start();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM,yyyy");
        final String todayDate = simpleDateFormat.format(new Date());

        if(myObject.getDate().equals(todayDate)){
            postTime.setText(myObject.getTime());
        }else{
            postTime.setText(myObject.getDate());
        }
        //GET LIKES
        Firebase getLikeRef = new Firebase("https://aftha7-2a05e.firebaseio.com/likes/");
        getLikeRef.orderByChild("postId").equalTo(myObject.getId()).addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {

                boolean likeExist = false;
                Like currentUserLike = new Like();
                for (com.firebase.client.DataSnapshot oneSnapshot: dataSnapshot.getChildren()) {
                    Like lk = oneSnapshot.getValue(Like.class);
                    lk.setId(oneSnapshot.getKey());
                    if(lk.getOwnerId().equals(mAuth.getCurrentUser().getUid().toString())){
                        likeExist = true;
                        currentUserLike = lk;
                    }
                }
                if(likeExist){
                    //finalHolder.btnLike.setText("Liked");
                    btnLike.setImageResource(R.drawable.like_24x24);
                    //finalHolder.btnLike.setBackgroundColor(Color.YELLOW);
                }else{
                    //finalHolder.btnLike.setText("Not Yet");
                    btnLike.setImageResource(R.drawable.like_empty_24x24);
                    //finalHolder.btnLike.setBackgroundColor(Color.WHITE);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        //END GET LIKES

        ownerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ownerName.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        btnStartVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postVideo.start();
            }
        });

        btnStopVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postVideo.pause();
            }
        });

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Like " + myObject.getDescription(), Toast.LENGTH_SHORT).show();
                likePost(myObject);
            }
        });


        /*textViewView.setText(myObject.getDescription());
        Picasso.with(imageView.getContext()).load(myObject.getMedia()).centerCrop().fit().into(imageView);*/
    }

    public void likePost(final Post p){

        final Like currentUserLike = new Like();
        Firebase getLikeRef = new Firebase("https://aftha7-2a05e.firebaseio.com/likes/");
        getLikeRef.orderByChild("postId").equalTo(p.getId()).addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {

                boolean likeExist = false;
                Like currentUserLike = new Like();
                for (com.firebase.client.DataSnapshot oneSnapshot: dataSnapshot.getChildren()) {
                    Like lk = oneSnapshot.getValue(Like.class);
                    lk.setId(oneSnapshot.getKey());
                    if(lk.getOwnerId().equals(mAuth.getCurrentUser().getUid().toString())){
                        likeExist = true;
                        currentUserLike = lk;
                    }
                }
                if(likeExist){
                    Firebase removeLikeRef = new Firebase("https://aftha7-2a05e.firebaseio.com/likes/"+currentUserLike.getId());
                    removeLikeRef.removeValue();
                    //finalHolder.btnLike.setText("Not Yet");
                    btnLike.setImageResource(R.drawable.like_empty_24x24);
                    //finalHolder.btnLike.setBackgroundColor(Color.WHITE);
                }else{
                    //Add Like
                    Firebase likeRef = new Firebase("https://aftha7-2a05e.firebaseio.com/likes");
                    Like like = new Like(p.getId(),mAuth.getCurrentUser().getUid().toString());
                    likeRef.push().setValue(like);
                    //End Add Like
                    //finalHolder.btnLike.setText("Liked");
                    btnLike.setImageResource(R.drawable.like_24x24);
                    //finalHolder.btnLike.setBackgroundColor(Color.YELLOW);
                }

                Log.e("LIKEEEE//",dataSnapshot.toString());

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        /*final DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("posts").child(p.getId()).child("numberLikes");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int numLikes = dataSnapshot.getValue(Integer.class);
                mDatabase.setValue(numLikes + 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

    }

    void loadDialogViewPostImage(Post p){

        final Dialog dialog = new Dialog(context,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_view_post_album);
        //dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // set the custom dialog components - text, image and button
        ImageView imgCloseDialog = (ImageView) dialog.findViewById(R.id.btn_close_dialog_view_post_image);
        //final ImageView imgPost = (ImageView) dialog.findViewById(R.id.img_dialog_view_post_image);
        //final LinearLayout horizontalLayout = (LinearLayout) dialog.findViewById(R.id.linear_layout_dialog_view_post_images);

        final GridView grid = (GridView) dialog.findViewById(R.id.grid_view_dialog_view_post_images);
        final ArrayList<String> images = new ArrayList<String>();
        GridAdapter gridAdapter = new GridAdapter(context, images);
        grid.setAdapter(gridAdapter);

        imgCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //Toast.makeText(context, listMedia.size(), Toast.LENGTH_LONG).show();
        Log.e("SIZEEEE//", String.valueOf(p.getListMediaUrl().size()));
        /*for (String oneUrl:p.getListMediaUrl()) {
            ImageView imgView = new ImageView(context);
            horizontalLayout.addView(imgView);
            Picasso.with(context).load(oneUrl).into(imgView);
        }*/


        //GET MEDIA DATA
        Firebase MediaRef = new Firebase("https://aftha7-2a05e.firebaseio.com/media");
        MediaRef.orderByChild("postId").equalTo(p.getId()).addChildEventListener(new com.firebase.client.ChildEventListener() {
            @Override
            public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                Media media = dataSnapshot.getValue(Media.class);
                ImageView imgView = new ImageView(context);
                //horizontalLayout.addView(imgView);
                images.add(media.getDownloadURL());
                //Picasso.with(context).load(media.getDownloadURL()).into(imgView);
            }

            @Override
            public void onChildChanged(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.firebase.client.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        //END GET MEDIA DATA




        dialog.show();
    }


}
