package com.cheersapps.aftha7beta.thread;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.cheersapps.aftha7beta.AddPostCameraActivity;
import com.cheersapps.aftha7beta.FeedActivity;
import com.cheersapps.aftha7beta.R;
import com.cheersapps.aftha7beta.entity.Media;
import com.cheersapps.aftha7beta.entity.Post;
import com.cheersapps.aftha7beta.entity.PostCamera;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.firebase.client.Firebase;
import com.gitonway.lee.niftynotification.lib.Configuration;
import com.gitonway.lee.niftynotification.lib.Effects;
import com.gitonway.lee.niftynotification.lib.NiftyNotificationView;
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
import java.util.List;

/**
 * Created by Mhamed on 05-01-17.
 */

public class AddPostCameraThread extends AsyncTask<PostCamera, Integer, String> {

    Context context;
    private StorageReference mStorage;
    FirebaseAuth mAuth;


    public AddPostCameraThread(Context context) {
        this.context = context;
        Firebase.setAndroidContext(context);
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Toast.makeText(context, "Début du traitement asynchrone", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        // Mise à jour de la ProgressBar
        //mProgressBar.setProgress(values[0]);
    }

    @Override
    protected String doInBackground(PostCamera... params) {
        Log.e("URIIIIIII//", params.toString());
        uploadImage(params[0]);
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, "Le traitement asynchrone est terminé", Toast.LENGTH_LONG).show();
    }


    private void uploadImage(final PostCamera pc) {
        final Firebase mRef = new Firebase("https://aftha7-2a05e.firebaseio.com/");
        Firebase mmRef = mRef.child("postImages");
        Firebase newPostRef = mmRef.push();
        //newPostRef.setValue("just an image");
        newPostRef.setValue(new Media(newPostRef.getKey().toString(), "NOTYET"));
        final String newImageName = newPostRef.getKey();
        StorageReference filePath = mStorage.child("postPhotos").child(newImageName);
        filePath.putFile(pc.getImUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                StorageReference pathReference = mStorage.child("postPhotos/" + newImageName);
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Toast.makeText(context, "done : " + uri.toString(), Toast.LENGTH_LONG).show();
                        //ADD POST HERE
                        addPost(uri, newImageName,pc);
                        //MyDynamicToast.successMessage(context,"Image uploaded");
                        //END ADD POST HERE
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

    private void addPost(Uri uriImage, String imgKey, PostCamera pc){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM,yyyy");
        final String date = simpleDateFormat.format(new Date());
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
        final String time = timeFormat.format(new Date());

        final Firebase mPostsRef = new Firebase("https://aftha7-2a05e.firebaseio.com/posts");
        Post p = new Post(0,pc.getPostDesc(),uriImage.toString(),"IMAGE",date,time,pc.getLatLocation(),pc.getLongLoction(), mAuth.getCurrentUser().getUid().toString());
        Firebase refPostName = mPostsRef.push();
        refPostName.setValue(p);

        //FirebaseDatabase.getInstance().getReference().child("postImages").child(imgKey).setValue(refPostName.getKey());
        FirebaseDatabase.getInstance().getReference().child("postImages").child(imgKey).child("postId").setValue(refPostName.getKey());
        Log.e("NEW ID//",refPostName.getKey().toString());
        MyDynamicToast.successMessage(context,"Image uploaded");

        Configuration cfg=new Configuration.Builder()
                .setAnimDuration(700)
                .setDispalyDuration(10000)
                .setBackgroundColor("#FFBDC3C7")
                .setTextColor("#FF444444")
                .setIconBackgroundColor("#FFFFFFFF")
                .setTextPadding(5)                      //dp
                .setViewHeight(48)                      //dp
                .setTextLines(2)                        //You had better use setViewHeight and setTextLines together
                .setTextGravity(Gravity.CENTER)         //only text def  Gravity.CENTER,contain icon Gravity.CENTER_VERTICAL
                .build();

        NiftyNotificationView.build((Activity) context,"Post Added successfully", Effects.flip,R.id.mLyout,cfg)
                .setIcon(R.drawable.add_image)               //remove this line ,only text
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //add your code
                        Toast.makeText(context, "alert clicked", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }



}
