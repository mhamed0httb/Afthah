package com.cheersapps.aftha7beta;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.andexert.library.RippleView;
import com.cheersapps.aftha7beta.adapter.CommentCustomAdapter;
import com.cheersapps.aftha7beta.adapter.GridAdapter;
import com.cheersapps.aftha7beta.entity.Comment;
import com.cheersapps.aftha7beta.entity.Like;
import com.cheersapps.aftha7beta.entity.Media;
import com.cheersapps.aftha7beta.entity.Post;
import com.cheersapps.aftha7beta.entity.User;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.gitonway.lee.niftynotification.lib.Configuration;
import com.gitonway.lee.niftynotification.lib.Effects;
import com.gitonway.lee.niftynotification.lib.NiftyNotificationView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView listViewPosts;
    List<Post> listPosts = null;
    ImageView sideBarProfileImage;
    FirebaseAuth mAuth;
    TextView currentUserMail,currentUserName;
    ImageView currentUserImage;

    private DatabaseReference mDatabase;

    //PostCustomAdapter adap;


    private Firebase mRef;
    Context context;

    ProgressDialog progressDialogLoadData;

    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Firebase.setAndroidContext(FeedActivity.this);
        context = this;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("posts");


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        currentUserMail = (TextView)header.findViewById(R.id.sideBar_mail);
        currentUserName = (TextView)header.findViewById(R.id.sideBar_name);
        currentUserImage = (ImageView) header.findViewById(R.id.sideBar_profile_image);
        //SET CURRENT USER MAIL & NAME
        currentUserMail.setText(mAuth.getCurrentUser().getEmail().toString());
        //GET CURRENT USER DATA
        String ownerUid = mAuth.getCurrentUser().getUid().toString();
        Firebase ownerRef = new Firebase("https://aftha7-2a05e.firebaseio.com/users/" + ownerUid);
        ownerRef.addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                //EVERYTHING ELSE HERE
                currentUserName.setText(currentUser.getName());
                Picasso.with(context).load(currentUser.getImage().toString()).resize(128,128)
                        .centerCrop().into(currentUserImage);
                //END EVERYTHING ELSE HERE
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        //END GET CURRENT USER DATA
        //END SET CURRENT USER MAIL & NAME

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btn_make_post);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent in = new Intent(FeedActivity.this,AddPostActivity.class);
                //startActivity(in);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);




        //Floating Button
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

        ImageView itemIcon = new ImageView(this);
        itemIcon.setImageResource(R.mipmap.bubbles);
        SubActionButton btnText = itemBuilder.setContentView(itemIcon).build();

        ImageView itemIcon2 = new ImageView(this);
        itemIcon2.setImageResource(R.drawable.cam_64x64);
        SubActionButton btnCamera = itemBuilder.setContentView(itemIcon2).build();

        ImageView itemIcon3 = new ImageView(this);
        itemIcon3.setImageResource(R.drawable.album_36x36);
        SubActionButton btnAlbum = itemBuilder.setContentView(itemIcon3).build();

        ImageView itemIcon4 = new ImageView(this);
        itemIcon4.setImageResource(R.drawable.video_36x36);
        SubActionButton btnVideo = itemBuilder.setContentView(itemIcon4).build();

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(btnText)
                .addSubActionView(btnCamera)
                .addSubActionView(btnAlbum)
                .addSubActionView(btnVideo)
                .attachTo(fab)
                .build();

        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FeedActivity.this,AddPostTextActivity.class));
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FeedActivity.this,AddPostCameraActivity.class));
            }
        });

        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FeedActivity.this,AddPostAlbumActivity.class));
            }
        });

        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FeedActivity.this, AddPostVideoActivity.class));
            }
        });
        //End Floating Button


        listPosts = new ArrayList<Post>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setLayoutManager(new GridLayoutManager(this,2));


        currentUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FeedActivity.this, AccountActivity.class));
                finish();
            }
        });



    }



    public static class PoViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageButton btnLike;
        ImageButton btnComment;
        ImageButton btnPlusImgs;
        ImageView postImage;
        //ImageButton postAlbumPlus;
        RippleView postImageRipple;
        ImageButton postViewOptions;
        public PoViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            btnLike = (ImageButton)itemView.findViewById(R.id.btn_like_post);
            btnComment = (ImageButton)itemView.findViewById(R.id.btn_comment_post);
            postImage = (ImageView)mView.findViewById(R.id.post_media);
            //postAlbumPlus = (ImageButton)mView.findViewById(R.id.post_album_plus);
            postImageRipple = (RippleView)mView.findViewById(R.id.post_media_ripple);
            btnPlusImgs = (ImageButton)mView.findViewById(R.id.btn_plus_img_post);
            postViewOptions = (ImageButton)mView.findViewById(R.id.btn_view_post_options);
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
                //SET WEIGHT
                btnPlusImgs.setVisibility(View.GONE);
                btnComment.setLayoutParams(new TableLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.WRAP_CONTENT, 3f));
                btnLike.setLayoutParams(new TableLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.WRAP_CONTENT, 3f));
                btnPlusImgs.setLayoutParams(new TableLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.WRAP_CONTENT, 0f));
                //END SET WEIGHT
                //Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/white.png?alt=media&token=7c98551c-e474-430c-956e-0f78715ea30f").resize(1, 1)
                        //.centerCrop().into(postImage);
                postImage.setVisibility(View.GONE);
                postImageRipple.setVisibility(View.GONE);
                videoHolder.setVisibility(View.GONE);
                albumHolder.setVisibility(View.GONE);
            }else if(mediaType.equals("IMAGE")){
                //SET WEIGHT
                btnPlusImgs.setVisibility(View.GONE);
                btnComment.setLayoutParams(new TableLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.WRAP_CONTENT, 3f));
                btnLike.setLayoutParams(new TableLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.WRAP_CONTENT, 3f));
                btnPlusImgs.setLayoutParams(new TableLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.WRAP_CONTENT, 0f));
                //END SET WEIGHT
                postImage.setVisibility(View.VISIBLE);
                postImageRipple.setVisibility(View.VISIBLE);
                Picasso.with(context).load(image).placeholder(R.drawable.please_wait).centerCrop().fit().into(postImage);
                videoHolder.setVisibility(View.GONE);
                albumHolder.setVisibility(View.GONE);
            }else if(mediaType.equals("VIDEO")){
                //SET WEIGHT
                btnPlusImgs.setVisibility(View.GONE);
                btnComment.setLayoutParams(new TableLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.WRAP_CONTENT, 3f));
                btnLike.setLayoutParams(new TableLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.WRAP_CONTENT, 3f));
                btnPlusImgs.setLayoutParams(new TableLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.WRAP_CONTENT, 0f));
                //END SET WEIGHT
                postImage.setVisibility(View.VISIBLE);
                postImageRipple.setVisibility(View.VISIBLE);
                videoHolder.setVisibility(View.GONE);
                albumHolder.setVisibility(View.GONE);
                /*String pathVideo = image;
                Uri uriVideo = Uri.parse(pathVideo);
                postVideo.setVideoURI(uriVideo);
                postVideo.start();*/
                //Picasso.with(context).load(R.drawable.video_player_256x256).centerCrop().fit().into(postImage);
                Picasso.with(context).load(R.drawable.video_play).centerCrop().fit().into(postImage);
            }else if(mediaType.equals("ALBUM")){
                //SET WEIGHT
                btnPlusImgs.setVisibility(View.VISIBLE);
                btnComment.setLayoutParams(new TableLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.WRAP_CONTENT, 2f));
                btnLike.setLayoutParams(new TableLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.WRAP_CONTENT, 2f));
                btnPlusImgs.setLayoutParams(new TableLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.WRAP_CONTENT, 2f));
                //END SET WEIGHT
                postImage.setVisibility(View.GONE);
                postImageRipple.setVisibility(View.GONE);
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
                        //post.addToListMedia(media.getDownloadURL().toString());
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
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialogLoadData = new ProgressDialog(context);
        progressDialogLoadData.setMessage(" Loading Data");
        progressDialogLoadData.setCanceledOnTouchOutside(false);
        if(isNetworkAvailable()){
            //Toast.makeText(context, "yess internet", Toast.LENGTH_LONG).show();
            progressDialogLoadData.show();
        }else{
            //Toast.makeText(context, "nooo internet", Toast.LENGTH_LONG).show();
            MyDynamicToast.warningMessage(FeedActivity.this, "No Internet Connection");
        }

        FirebaseRecyclerAdapter<Post, PoViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PoViewHolder>(
                Post.class,
                R.layout.one_post,
                PoViewHolder.class,
                mDatabase.orderByChild("date")

        ) {
            @Override
            protected void populateViewHolder(final PoViewHolder viewHolder, Post model, int position) {
                progressDialogLoadData.dismiss();
                String postKey = getRef(position).getKey();
                model.setId(postKey);
                final Post p = model;
                final PoViewHolder holder = viewHolder;
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
                /*viewHolder.postImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(p.getMediaType().equals("IMAGE")){
                            loadDialogViewPostOneImage(p);
                        }else if(p.getMediaType().equals("VIDEO")){
                            loadDialogViewPostVideo(p);
                        }

                    }
                });*/
                viewHolder.postImageRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        if(p.getMediaType().equals("IMAGE")){
                            loadDialogViewPostOneImage(p);
                        }else if(p.getMediaType().equals("VIDEO")){
                            loadDialogViewPostVideo(p);
                        }
                    }
                });
                viewHolder.btnPlusImgs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadDialogViewPostAlbum(p);
                    }
                });
                viewHolder.postViewOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadPostOptions(p);
                    }
                });

            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        //progressDialogLoadData.dismiss();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //Intent in = new Intent(FeedActivity.this,MainActivity.class);
            //startActivity(in);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.action_refresh){
            finish();
            startActivity(getIntent());
            return true;
        }*/
        if(id == R.id.action_refresh){
            finish();
            startActivity(getIntent());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_posts) {
            // Handle the my posts action
            startActivity(new Intent(FeedActivity.this,MyPostsActivity.class));
        }


        else if (id == R.id.nav_chat) {
            Intent in = new Intent(FeedActivity.this,ChatActivity.class);
            startActivity(in);
        }
        //else if (id == R.id.nav_filter) {
            //final Dialog dialog = new Dialog(context,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
            //final Dialog dialog = new Dialog(context);
            //dialog.setContentView(R.layout.dialog_feed_filter);
            //dialog.setTitle("");
            //dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

           /* EditText text = (EditText) dialog.findViewById(R.id.input_add_comment_dialog);
            text.setHint("your comment yoo...");

            Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog_add_comment);
            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    //dialog.dismiss();
                }
            });*/

            //dialog.show();

        //}

        /*else if (id == R.id.nav_manage) {

        } */
        else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent in = new Intent(FeedActivity.this,MainActivity.class);
            startActivity(in);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    public void loadAddCommentDialog(final Post p){

        final ArrayList<Comment> listComments = new ArrayList<Comment>();

        final Dialog dialog = new Dialog(context,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_add_comment);
        dialog.setTitle("Title...");
        //dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

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
                Log.e("COMMENTSSSSS==",com.toString());
                Log.e("COMMENTPOSTSSS==",p.toString());
                /*if(com.getPostId().equals(p.getId())){
                    listComments.add(com);
                }*/
                listComments.add(com);
                commentAdap.notifyDataSetChanged();

                //progressDialogLoadComments.dismiss();
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

                Calendar c = Calendar.getInstance();
                int seconds = c.get(Calendar.SECOND);
                int minutes = c.get(Calendar.MINUTE);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int mounth = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                int year = c.get(Calendar.YEAR);

                final String date = day + "/" + mounth + "/" + year;
                final String time = hour + ":" + minutes;

                Comment com = new Comment(commentInput.getText().toString(),p.getId(), mAuth.getCurrentUser().getUid().toString(),date,time);
                mDatabase.push().setValue(com);

                //GET OWNER DATA
                /*String ownerUid = mAuth.getCurrentUser().getUid().toString();
                Firebase ownerRef = new Firebase("https://aftha7-2a05e.firebaseio.com/users/" + ownerUid);
                ownerRef.addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
                    @Override
                    public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                        User ownerUser = dataSnapshot.getValue(User.class);
                        //EVERYTHING ELSE HERE
                        Comment com = new Comment(commentInput.getText().toString(),p.getId(), mAuth.getCurrentUser().getUid().toString(),date,time);
                        mDatabase.push().setValue(com);
                        //END EVERYTHING ELSE HERE
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });*/
                //END GET OWNER DATA

                //dialog.dismiss();
            }
        });


        dialog.show();
    }

    void loadDialogViewPostAlbum(Post p){

        final Dialog dialog = new Dialog(context,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_view_post_album);
        //dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // set the custom dialog components - text, image and button
        //ImageView imgCloseDialog = (ImageView) dialog.findViewById(R.id.btn_close_dialog_view_post_image);
        //final ImageView imgPost = (ImageView) dialog.findViewById(R.id.img_dialog_view_post_image);
        //final LinearLayout horizontalLayout = (LinearLayout) dialog.findViewById(R.id.linear_layout_dialog_view_post_images);

        final GridView grid = (GridView) dialog.findViewById(R.id.grid_view_dialog_view_post_images);
        grid.setColumnWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        final ArrayList<String> images = new ArrayList<String>();
        GridAdapter gridAdapter = new GridAdapter(context, images);
        grid.setAdapter(gridAdapter);

        /*imgCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });*/

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

    void loadDialogViewPostVideo(Post p){
        final Dialog dialog = new Dialog(context,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_view_post_video);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // set the custom dialog components - text, image and button
        ImageView imgCloseDialog = (ImageView) dialog.findViewById(R.id.btn_close_dialog_view_post_image);
        final VideoView vidDisplay = (VideoView) dialog.findViewById(R.id.video_view_post_video_show);
        ImageButton startVid = (ImageButton) dialog.findViewById(R.id.btn_dialog_start_post_video);
        ImageButton stopVid = (ImageButton) dialog.findViewById(R.id.btn_dialog_stop_post_video);
        ImageButton pauseVid = (ImageButton) dialog.findViewById(R.id.btn_dialog_pause_post_video);
        String pathVideo = p.getMedia();
        final Uri uriVideo = Uri.parse(pathVideo);
        vidDisplay.setVideoURI(uriVideo);
        vidDisplay.start();

        startVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(vidDisplay.isActivated()){
                    vidDisplay.resume();
                }else{
                    vidDisplay.setVideoURI(uriVideo);
                    vidDisplay.start();
                }*/
                vidDisplay.stopPlayback();
                vidDisplay.setVideoURI(uriVideo);
                vidDisplay.start();
            }
        });
        stopVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vidDisplay.stopPlayback();
            }
        });
        pauseVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vidDisplay.pause();
            }
        });
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
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // set the custom dialog components - text, image and button
        ImageView imgCloseDialog = (ImageView) dialog.findViewById(R.id.btn_close_dialog_view_post_image);
        VideoView vidDisplay = (VideoView) dialog.findViewById(R.id.video_view_post_video_show);
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

    void loadPostOptions(final Post p){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(p.getOwner().equals(mAuth.getCurrentUser().getUid())){
            builder.setItems(R.array.post_options_owner, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            switch (which){
                                case 0:
                                    loadDialogViewPostMap(p);
                                    break;
                                case 1:
                                    loadDialogUpdatePost(p);
                                    break;
                                case 2:
                                    deletePostDialog(p);
                                    break;
                            }
                        }
                    });
        }else{
            builder.setItems(R.array.post_options, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            switch (which){
                                case 0:
                                    loadDialogViewPostMap(p);
                                    break;
                            }
                        }
                    });
        }
        builder.create().show();
    }

    void deletePostDialog(final Post p){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to delete this post ?")
                .setTitle("Delete");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                //Toast.makeText(context, "Deleting ...", Toast.LENGTH_SHORT).show();
                if(p.getMediaType().equals("NOFILE")){
                    FirebaseDatabase.getInstance().getReference().child("posts").child(p.getId()).removeValue();
                    //Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    MyDynamicToast.successMessage(FeedActivity.this, "Post Deleted");
                }else if(p.getMediaType().equals("IMAGE")){
                    Firebase MediaRef = new Firebase("https://aftha7-2a05e.firebaseio.com/postImages");
                    MediaRef.orderByChild("postId").equalTo(p.getId()).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                            final Media m = dataSnapshot.getValue(Media.class);
                            Log.e("POSTIDD//",p.getId());
                            Log.e("IMGIDD//", m.getDownloadURL());
                            StorageReference mStorage = FirebaseStorage.getInstance().getReference();
                            StorageReference desertRef = mStorage.child("postPhotos/"+m.getDownloadURL().toString());
                            desertRef.delete().addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    FirebaseDatabase.getInstance().getReference().child("posts").child(p.getId()).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("postImages").child(m.getDownloadURL()).removeValue();
                                    //Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                                    MyDynamicToast.successMessage(FeedActivity.this, "Post Deleted");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                    //Toast.makeText(context, "error on delete", Toast.LENGTH_SHORT).show();
                                    MyDynamicToast.errorMessage(FeedActivity.this, "Something went wrong !!");
                                }
                            });
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
                    /*DatabaseReference dR = FirebaseDatabase.getInstance().getReference().child("postImages");
                    dR.orderByChild("postId").equalTo(p.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Media m = dataSnapshot.getValue(Media.class);
                            Log.e("POSTIDD//",p.getId());
                            Log.e("IMGIDD//", m.getDownloadURL());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });*/
                }else if(p.getMediaType().equals("VIDEO")){
                    Firebase MediaRef = new Firebase("https://aftha7-2a05e.firebaseio.com/postVideos");
                    MediaRef.orderByChild("postId").equalTo(p.getId()).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                            final Media m = dataSnapshot.getValue(Media.class);
                            Log.e("POSTIDD//",p.getId());
                            Log.e("IMGIDD//", m.getDownloadURL());
                            StorageReference mStorage = FirebaseStorage.getInstance().getReference();
                            StorageReference desertRef = mStorage.child("postVideos/"+m.getDownloadURL().toString());
                            desertRef.delete().addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    FirebaseDatabase.getInstance().getReference().child("posts").child(p.getId()).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("postVideos").child(m.getDownloadURL()).removeValue();
                                    //Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                                    MyDynamicToast.successMessage(FeedActivity.this, "Post Deleted");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                    //Toast.makeText(context, "error on delete", Toast.LENGTH_SHORT).show();
                                    MyDynamicToast.errorMessage(FeedActivity.this, "Something went wrong !!");
                                }
                            });
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
                    /*DatabaseReference dR = FirebaseDatabase.getInstance().getReference().child("postImages");
                    dR.orderByChild("postId").equalTo(p.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Media m = dataSnapshot.getValue(Media.class);
                            Log.e("POSTIDD//",p.getId());
                            Log.e("IMGIDD//", m.getDownloadURL());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });*/
                }else if(p.getMediaType().equals("ALBUM")){
                    Firebase MediaRef = new Firebase("https://aftha7-2a05e.firebaseio.com/postImages");
                    MediaRef.orderByChild("postId").equalTo(p.getId()).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                            final Media m = dataSnapshot.getValue(Media.class);
                            Log.e("POSTIDD//",p.getId());
                            Log.e("IMGIDD//", m.getDownloadURL());
                            StorageReference mStorage = FirebaseStorage.getInstance().getReference();
                            StorageReference desertRef = mStorage.child("postPhotos/"+m.getDownloadURL().toString());
                            desertRef.delete().addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    FirebaseDatabase.getInstance().getReference().child("posts").child(p.getId()).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("postImages").child(m.getDownloadURL()).removeValue();
                                    //Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                                    MyDynamicToast.successMessage(FeedActivity.this, "Post Deleted");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                    //Toast.makeText(context, "error on delete", Toast.LENGTH_SHORT).show();
                                    MyDynamicToast.errorMessage(FeedActivity.this, "Something went wrong !!");
                                }
                            });
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
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("posts").child(p.getId()).removeValue();
                    //Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    MyDynamicToast.successMessage(FeedActivity.this, "Post Deleted");
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                                      @Override
                                      public void onShow(DialogInterface arg0) {
                                          dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                                          dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                                      }
                                  });
        dialog.show();
    }

    void loadDialogViewPostMap(final Post p){
        if(p.getLatLocation() == 0 && p.getLongLocation() == 0){
            //Toast.makeText(context, "Location not specified", Toast.LENGTH_SHORT).show();
            MyDynamicToast.informationMessage(FeedActivity.this, "Location not specifiedf");
        }else{
            Dialog dialog = new Dialog(context,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_view_post_map);
            dialog.show();

            // set the custom dialog components - text, image and button
            GoogleMap gMap;


            MapView mMapView = (MapView) dialog.findViewById(R.id.map_feed);
            MapsInitializer.initialize(context);

            mMapView = (MapView) dialog.findViewById(R.id.map_feed);
            mMapView.onCreate(dialog.onSaveInstanceState());
            mMapView.onResume();// needed to get the map to display immediately
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    LatLng location = new LatLng(p.getLatLocation(),p.getLongLocation());
                    googleMap.addMarker(new MarkerOptions().position(location).title(p.getDescription()));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,18.0f));
                }
            });
        }
    }

    void loadDialogUpdatePost(final Post p){

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_update_post);
        dialog.setCanceledOnTouchOutside(false);

        // set the custom dialog components - text, image and button
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel_update_post);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm_update_post);
        final EditText postText = (EditText) dialog.findViewById(R.id.edit_text_update_post);

        postText.setText(p.getDescription().toString());

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(p.getId()).child("description").setValue(postText.getText().toString());
                dialog.dismiss();
                MyDynamicToast.successMessage(FeedActivity.this, "Post updated");
            }
        });

        dialog.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


}
