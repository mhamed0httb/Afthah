package com.cheersapps.aftha7beta;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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





    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Post, FeedActivity.PoViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, FeedActivity.PoViewHolder>(
                Post.class,
                R.layout.one_post,
                FeedActivity.PoViewHolder.class,
                mDatabase.orderByChild("owner").equalTo(mAuth.getCurrentUser().getUid())

        ) {
            @Override
            protected void populateViewHolder(FeedActivity.PoViewHolder viewHolder, Post model, int position) {
                String postKey = getRef(position).getKey();
                model.setId(postKey);
                final Post p = model;
                final FeedActivity.PoViewHolder holder = viewHolder;
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

    void loadDialogViewPostMap(final Post p){
        if(p.getLatLocation() == 0 && p.getLongLocation() == 0){
            //Toast.makeText(context, "Location not specified", Toast.LENGTH_SHORT).show();
            MyDynamicToast.informationMessage(MyPostsActivity.this, "Location not specifiedf");
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
                MyDynamicToast.successMessage(MyPostsActivity.this, "Post updated");
            }
        });

        dialog.show();
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
                    MyDynamicToast.successMessage(MyPostsActivity.this, "Post Deleted");
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
                                    MyDynamicToast.successMessage(MyPostsActivity.this, "Post Deleted");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                    //Toast.makeText(context, "error on delete", Toast.LENGTH_SHORT).show();
                                    MyDynamicToast.errorMessage(MyPostsActivity.this, "Something went wrong !!");
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
                                    MyDynamicToast.successMessage(MyPostsActivity.this, "Post Deleted");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                    //Toast.makeText(context, "error on delete", Toast.LENGTH_SHORT).show();
                                    MyDynamicToast.errorMessage(MyPostsActivity.this, "Something went wrong !!");
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
                                    MyDynamicToast.successMessage(MyPostsActivity.this, "Post Deleted");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                    //Toast.makeText(context, "error on delete", Toast.LENGTH_SHORT).show();
                                    MyDynamicToast.errorMessage(MyPostsActivity.this, "Something went wrong !!");
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
                    MyDynamicToast.successMessage(MyPostsActivity.this, "Post Deleted");
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



}
