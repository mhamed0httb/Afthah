package com.cheersapps.aftha7beta.thread;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.cheersapps.aftha7beta.AddPostVideoActivity;
import com.cheersapps.aftha7beta.FeedActivity;
import com.cheersapps.aftha7beta.entity.Media;
import com.cheersapps.aftha7beta.entity.Post;
import com.cheersapps.aftha7beta.entity.PostCamera;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mhamed on 05-01-17.
 */

public class AddPostVideoThread extends AsyncTask<PostCamera, Integer, String> {

    Context context;
    private StorageReference mStorage;
    FirebaseAuth mAuth;

    public AddPostVideoThread(Context context) {
        this.context = context;
        Firebase.setAndroidContext(context);
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected String doInBackground(PostCamera... params) {
        uploadVideo(params[0]);
        return null;
    }


    void uploadVideo(final PostCamera pv){
        final Firebase mRef = new Firebase("https://aftha7-2a05e.firebaseio.com/");
        Firebase mmRef = mRef.child("postVideos");
        Firebase newPostRef = mmRef.push();
        //newPostRef.setValue("just a video");
        newPostRef.setValue(new Media(newPostRef.getKey().toString(),"NOTYET"));
        final String newVideoName = newPostRef.getKey();
        StorageReference filePath = mStorage.child("postVideos").child(newVideoName);
        filePath.putFile(pv.getImUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageReference pathReference = mStorage.child("postVideos/"+newVideoName);
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Toast.makeText(context, "done : " + uri.toString(), Toast.LENGTH_LONG).show();
                        //ADD POST HERE
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM,yyyy");
                        final String date = simpleDateFormat.format(new Date());
                        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
                        final String time = timeFormat.format(new Date());

                        final Firebase mPostsRef = new Firebase("https://aftha7-2a05e.firebaseio.com/posts");
                        Post p = new Post(0,pv.getPostDesc(),uri.toString(),"VIDEO",date,time,pv.getLatLocation(),pv.getLongLoction(), mAuth.getCurrentUser().getUid().toString());
                        Firebase refPostName = mPostsRef.push();
                        refPostName.setValue(p);
                        Log.e("NEW ID//",refPostName.getKey().toString());
                        String postId = refPostName.getKey().toString();
                        FirebaseDatabase.getInstance().getReference().child("postVideos").child(newVideoName).child("postId").setValue(refPostName.getKey());
                        //END ADD POST HERE
                        MyDynamicToast.successMessage(context, "Post Added Successfully :)");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        MyDynamicToast.errorMessage(context, "Something went wrong");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                MyDynamicToast.errorMessage(context, "Something went wrong");
            }
        });
    }
}
