package com.cheersapps.aftha7beta;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.cheersapps.aftha7beta.adapter.CommentCustomAdapter;
import com.cheersapps.aftha7beta.adapter.GridAdapter;
import com.cheersapps.aftha7beta.entity.Comment;
import com.cheersapps.aftha7beta.entity.Like;
import com.cheersapps.aftha7beta.entity.Media;
import com.cheersapps.aftha7beta.entity.Post;
import com.cheersapps.aftha7beta.entity.User;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyPostsActivity extends AppCompatActivity {

    List<Post> listPosts = null;
    FirebaseAuth mAuth;

    private DatabaseReference mDatabase;

    private Firebase mRef;
    Context context;

    ProgressDialog progressDialogLoadData;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        Firebase.setAndroidContext(MyPostsActivity.this);
        context = this;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("posts");

        listPosts = new ArrayList<Post>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_my_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public static class PoViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageButton btnLike;
        ImageButton btnComment;
        ImageView postImage;
        ImageButton postAlbumPlus;
        ImageButton postRemove;
        public PoViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            btnLike = (ImageButton)itemView.findViewById(R.id.btn_like_post);
            btnComment = (ImageButton)itemView.findViewById(R.id.btn_comment_post);
            postImage = (ImageView)mView.findViewById(R.id.post_media);
            postAlbumPlus = (ImageButton)mView.findViewById(R.id.post_album_plus);
            postRemove = (ImageButton)mView.findViewById(R.id.btn_view_my_post_trash);
        }
        public void setDescription(String desc){
            TextView post_desc = (TextView) mView.findViewById(R.id.post_description);
            post_desc.setText(desc);
        }
        public void setMedia(final Context context, String image, String mediaType, final Post post){
            LinearLayout videoHolder = (LinearLayout)mView.findViewById(R.id.video_holder);
            LinearLayout albumHolder = (LinearLayout)mView.findViewById(R.id.post_album_holder);
            VideoView postVideo = (VideoView)mView.findViewById(R.id.post_video);
            final ImageView postAlbum = (ImageView)mView.findViewById(R.id.post_album);
            if(mediaType.equals("NOFILE")){
                postImage.setVisibility(View.GONE);
                videoHolder.setVisibility(View.GONE);
                albumHolder.setVisibility(View.GONE);
            }else if(mediaType.equals("IMAGE")){
                postImage.setVisibility(View.VISIBLE);
                Picasso.with(context).load(image).placeholder(R.drawable.please_wait).centerCrop().fit().into(postImage);
                videoHolder.setVisibility(View.GONE);
                albumHolder.setVisibility(View.GONE);
            }else if(mediaType.equals("VIDEO")){
                postImage.setVisibility(View.VISIBLE);
                videoHolder.setVisibility(View.GONE);
                albumHolder.setVisibility(View.GONE);
                Picasso.with(context).load(R.drawable.video_player_512x512).centerCrop().fit().into(postImage);
            }else if(mediaType.equals("ALBUM")){
                postImage.setVisibility(View.GONE);
                videoHolder.setVisibility(View.GONE);
                albumHolder.setVisibility(View.VISIBLE);
                //GET MEDIA DATA
                post.getListMediaUrl().clear();
                Firebase MediaRef = new Firebase("https://aftha7-2a05e.firebaseio.com/media");
                MediaRef.orderByChild("postId").equalTo(post.getId()).addChildEventListener(new com.firebase.client.ChildEventListener() {
                    @Override
                    public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                        Media media = dataSnapshot.getValue(Media.class);
                        media.setId(dataSnapshot.getKey());
                        post.getListMediaUrl().add(media.getDownloadURL().toString());
                        Picasso.with(context).load(media.getDownloadURL().toString()).placeholder(R.drawable.please_wait).centerCrop().fit().into(postAlbum);
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
            }

        }
        public void setOwner(final Context context,String ownerUid){
            final TextView ownerName = (TextView)itemView.findViewById(R.id.post_owner_name);
            final ImageView ownerImage = (ImageView)itemView.findViewById(R.id.post_owner_image);
            //GET OWNER DATA
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
        }
        public void setPostTime(String date, String time){
            TextView postTime = (TextView)itemView.findViewById(R.id.post_time);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM,yyyy");
            final String todayDate = simpleDateFormat.format(new Date());
            if(date.equals(todayDate)){
                postTime.setText(time);
            }else{
                postTime.setText(date);
            }
        }
        public void setLike(String postKey){
            //GET LIKES
            Firebase getLikeRef = new Firebase("https://aftha7-2a05e.firebaseio.com/likes/");
            getLikeRef.orderByChild("postId").equalTo(postKey).addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
                @Override
                public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {

                    boolean likeExist = false;
                    Like currentUserLike = new Like();
                    for (com.firebase.client.DataSnapshot oneSnapshot: dataSnapshot.getChildren()) {
                        Like lk = oneSnapshot.getValue(Like.class);
                        lk.setId(oneSnapshot.getKey());
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        if(lk.getOwnerId().equals(mAuth.getCurrentUser().getUid().toString())){
                            likeExist = true;
                            currentUserLike = lk;
                        }
                    }
                    if(likeExist){
                        btnLike.setImageResource(R.drawable.like_24x24);
                    }else{
                        btnLike.setImageResource(R.drawable.like_empty_24x24);
                    }

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
            //END GET LIKES
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Post, MyPostsActivity.PoViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, MyPostsActivity.PoViewHolder>(
                Post.class,
                R.layout.one_my_post,
                MyPostsActivity.PoViewHolder.class,
                mDatabase.orderByChild("owner").equalTo(mAuth.getCurrentUser().getUid())

        ) {
            @Override
            protected void populateViewHolder(MyPostsActivity.PoViewHolder viewHolder, Post model, int position) {
                final String postKey = getRef(position).getKey();
                model.setId(postKey);
                final Post p = model;
                final MyPostsActivity.PoViewHolder holder = viewHolder;
                viewHolder.setDescription(model.getDescription());
                viewHolder.setMedia(getApplicationContext(), model.getMedia(), model.getMediaType(), model);
                viewHolder.setOwner(getApplicationContext(), model.getOwner());
                viewHolder.setPostTime(model.getDate(), model.getTime());
                viewHolder.setLike(postKey);
                viewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likePost(p, holder.btnLike);
                    }
                });
                viewHolder.btnComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadAddCommentDialog(p);
                    }
                });
                viewHolder.postImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(p.getMediaType().equals("IMAGE")){
                            loadDialogViewPostOneImage(p);
                        }else if(p.getMediaType().equals("VIDEO")){
                            loadDialogViewPostVideo(p);
                        }

                    }
                });
                viewHolder.postAlbumPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadDialogViewPostAlbum(p);
                    }
                });
                viewHolder.postRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogRemovePost(postKey);
                    }
                });

            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(MyPostsActivity.this,FeedActivity.class);
        startActivity(in);
    }

    public void likePost(final Post p, final ImageButton btnLike){

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
                    btnLike.setImageResource(R.drawable.like_empty_24x24);
                }else{
                    //Add Like
                    Firebase likeRef = new Firebase("https://aftha7-2a05e.firebaseio.com/likes");
                    Like like = new Like(p.getId(),mAuth.getCurrentUser().getUid().toString());
                    likeRef.push().setValue(like);
                    //End Add Like
                    btnLike.setImageResource(R.drawable.like_24x24);
                }

                Log.e("LIKEEEE//",dataSnapshot.toString());

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void loadAddCommentDialog(final Post p){

        final ArrayList<Comment> listComments = new ArrayList<Comment>();

        final Dialog dialog = new Dialog(context,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_add_comment);
        dialog.setTitle("Title...");

        // set the custom dialog components - text, image and button
        final EditText commentInput = (EditText) dialog.findViewById(R.id.input_add_comment_dialog);
        final ListView listViewComments = (ListView) dialog.findViewById(R.id.list_comments);
        Button btnAddComment = (Button) dialog.findViewById(R.id.btn_dialog_add_comment);

        final CommentCustomAdapter commentAdap = new CommentCustomAdapter(context,R.layout.one_comment,listComments);
        listViewComments.setAdapter(commentAdap);

        final DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("comments");
        mDatabase.orderByChild("postId").equalTo(p.getId()).addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                Comment com = dataSnapshot.getValue(Comment.class);
                listComments.add(com);
                commentAdap.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                Comment com = dataSnapshot.getValue(Comment.class);
                com.setId(dataSnapshot.getKey().toString());
                for(int i=0; i< listComments.size();i++){
                    if(listComments.get(i).getId().equals(dataSnapshot.getKey().toString())){
                        listComments.set(i,com);
                    }
                }
                commentAdap.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, commentInput.getText().toString(), Toast.LENGTH_LONG).show();

                final DatabaseReference mDatabase;
                mDatabase = FirebaseDatabase.getInstance().getReference().child("comments");

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM,yyyy");
                final String date = simpleDateFormat.format(new Date());

                DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
                final String time = timeFormat.format(new Date());

                Comment com = new Comment(commentInput.getText().toString(),p.getId(), mAuth.getCurrentUser().getUid().toString(),date,time);
                mDatabase.push().setValue(com);

            }
        });


        dialog.show();
    }

    void loadDialogViewPostAlbum(Post p){

        final Dialog dialog = new Dialog(context,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_view_post_album);

        // set the custom dialog components - text, image and button
        ImageView imgCloseDialog = (ImageView) dialog.findViewById(R.id.btn_close_dialog_view_post_image);

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

        //GET MEDIA DATA
        Firebase MediaRef = new Firebase("https://aftha7-2a05e.firebaseio.com/media");
        MediaRef.orderByChild("postId").equalTo(p.getId()).addChildEventListener(new com.firebase.client.ChildEventListener() {
            @Override
            public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                Media media = dataSnapshot.getValue(Media.class);
                ImageView imgView = new ImageView(context);
                images.add(media.getDownloadURL());
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

    void loadDialogViewPostVideo(Post p){
        final Dialog dialog = new Dialog(context,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_view_post_video);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // set the custom dialog components - text, image and button
        ImageView imgCloseDialog = (ImageView) dialog.findViewById(R.id.btn_close_dialog_view_post_image);
        VideoView vidDisplay = (VideoView) dialog.findViewById(R.id.video_view_post_video_show);
        String pathVideo = p.getMedia();
        Uri uriVideo = Uri.parse(pathVideo);
        vidDisplay.setVideoURI(uriVideo);
        vidDisplay.start();
        imgCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }



    void loadDialogViewPostOneImage(Post p){

        final Dialog dialog = new Dialog(context,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_view_post_one_image);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // set the custom dialog components - text, image and button
        ImageView imgCloseDialog = (ImageView) dialog.findViewById(R.id.btn_close_dialog_view_post_image);
        ImageView imgPost = (ImageView) dialog.findViewById(R.id.img_dialog_view_post_one_image);
        Picasso.with(context).load(p.getMedia()).placeholder(R.drawable.please_wait).centerCrop().fit().into(imgPost);

        imgCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    void alertDialogRemovePost(final String postKey){
        AlertDialog.Builder builder = new AlertDialog.Builder(MyPostsActivity.this);
        builder.setTitle("Are you sure you want to delete this post !?");
        builder.setMessage("Choose carefully.");

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        FirebaseDatabase.getInstance().getReference().child("posts").child(postKey).removeValue();
                        dialog.dismiss();
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        // display dialog
        dialog.show();
    }
}
