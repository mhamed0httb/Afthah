package com.cheersapps.aftha7beta.thread;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.cheersapps.aftha7beta.AddPostAlbumActivity;
import com.cheersapps.aftha7beta.FeedActivity;
import com.cheersapps.aftha7beta.entity.Media;
import com.cheersapps.aftha7beta.entity.Post;
import com.cheersapps.aftha7beta.entity.PostCamera;
import com.darsh.multipleimageselect.models.Image;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Mhamed on 06-01-17.
 */

public class AddPostAlbumThread extends AsyncTask<PostCamera, Integer, String> {

    ArrayList<String> listNewNames;
    ArrayList<Uri> listDownloadUri;
    Context context;
    private StorageReference mStorage;
    FirebaseAuth mAuth;

    public AddPostAlbumThread(Context context) {
        this.context = context;
        Firebase.setAndroidContext(context);
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        listNewNames = new ArrayList<String>();
        listDownloadUri = new ArrayList<Uri>();
    }

    private void uploadImage(final PostCamera pa, final int position){
        final Firebase mRef = new Firebase("https://aftha7-2a05e.firebaseio.com/");
        Firebase mmRef = mRef.child("postImages");
        Firebase newPostRef = mmRef.push();
        //newPostRef.setValue("just an image");
        newPostRef.setValue(new Media(newPostRef.getKey().toString(),"NOTYET"));
        final String newImageName = newPostRef.getKey();
        listNewNames.add(newImageName);
        StorageReference filePath = mStorage.child("postPhotos").child(newImageName);
        File imgFile = new File(pa.getListImages().get(position).path);
        filePath.putFile(Uri.fromFile(imgFile)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageReference pathReference = mStorage.child("postPhotos/"+newImageName);
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Toast.makeText(context, "done : " + uri.toString(), Toast.LENGTH_LONG).show();
                        listDownloadUri.add(uri);
                        if(position < pa.getListImages().size()-1){
                            uploadImage(pa,position+1);
                        }else{
                            //Toast.makeText(context, "All Done : ", Toast.LENGTH_LONG).show();

                            //ADD POST HERE
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM,yyyy");
                            final String date = simpleDateFormat.format(new Date());
                            DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
                            final String time = timeFormat.format(new Date());

                            final Firebase mPostsRef = new Firebase("https://aftha7-2a05e.firebaseio.com/posts");
                            Post p = new Post(0,pa.getPostDesc(),"ALBUM","ALBUM",date,time,pa.getLatLocation(),pa.getLongLoction(), mAuth.getCurrentUser().getUid().toString());
                            Firebase refPostName = mPostsRef.push();
                            refPostName.setValue(p);
                            Log.e("NEW ID//",refPostName.getKey().toString());
                            String postId = refPostName.getKey().toString();
                            //END ADD POST HERE

                            //ADD MEDIA HERE
                            for (Uri oneUri:listDownloadUri) {
                                final Firebase mediaRef = new Firebase("https://aftha7-2a05e.firebaseio.com/media");
                                Media media = new Media(oneUri.toString(),postId);
                                mediaRef.push().setValue(media);
                            }
                            //END ADD MEDIA HERE
                            for(String oneName:listNewNames){
                                FirebaseDatabase.getInstance().getReference().child("postImages").child(oneName).child("postId").setValue(postId);
                            }
                            MyDynamicToast.successMessage(context, "Post Added Successfully :)");

                        }
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

    @Override
    protected String doInBackground(PostCamera... params) {
        uploadImage(params[0],0);
        return null;
    }
}
