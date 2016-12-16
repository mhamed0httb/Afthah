package com.cheersapps.aftha7beta;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.afollestad.materialcamera.MaterialCamera;
import com.cheersapps.aftha7beta.entity.Post;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPostCameraActivity extends AppCompatActivity {

    private final static int CAMERA_RQ = 6969;
    private StorageReference mStorage;
    FirebaseAuth mAuth;
    private ProgressDialog progressDialogGetFile;

    ImageView displayImageCamera;
    EditText addPostInputTextCamera;
    Button btnAddPostCamera,btnChangePhoto;

    Context context;

    double latLocation,longLoction = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post_camera);

        context = this;

        Firebase.setAndroidContext(context);
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        displayImageCamera = (ImageView) findViewById(R.id.display_image_take_post_camera);
        addPostInputTextCamera = (EditText) findViewById(R.id.add_post_input_text_camera);
        btnAddPostCamera = (Button) findViewById(R.id.btn_add_post_camera);
        btnChangePhoto = (Button) findViewById(R.id.btn_change_photo);
        progressDialogGetFile = new ProgressDialog(context);

        btnChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMaterialCamera();
            }
        });

        startMaterialCamera();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_RQ) {

            if (resultCode == RESULT_OK) {
                Toast.makeText(context, "Saved to: " + data.getDataString(), Toast.LENGTH_LONG).show();
                final Uri imUri = data.getData();

                displayImageCamera.setImageURI(imUri);
                btnAddPostCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uploadImage(imUri);
                    }
                });
            } else if(data != null) {
                Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
                e.printStackTrace();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private  void startMaterialCamera(){
        new MaterialCamera(this)
                /** all the previous methods can be called, but video ones would be ignored */
                .stillShot() // launches the Camera in stillshot mode
                .start(CAMERA_RQ);
    }

    private void uploadImage(Uri u){
        final ProgressDialog progressDialogUploading = new ProgressDialog(context);
        final Firebase mRef = new Firebase("https://aftha7-2a05e.firebaseio.com/");
        Firebase mmRef = mRef.child("postImages");
        Firebase newPostRef = mmRef.push();
        newPostRef.setValue("just an image");
        final String newImageName = newPostRef.getKey();
        progressDialogUploading.setMessage("Uploading...");
        progressDialogUploading.setCanceledOnTouchOutside(false);
        progressDialogUploading.show();
        StorageReference filePath = mStorage.child("postPhotos").child(newImageName);
        filePath.putFile(u).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                StorageReference pathReference = mStorage.child("postPhotos/"+newImageName);
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Toast.makeText(context, "done : " + uri.toString(), Toast.LENGTH_LONG).show();
                        //ADD POST HERE
                        addPost(uri);
                        //END ADD POST HERE
                        startActivity(new Intent(AddPostCameraActivity.this,FeedActivity.class));
                    }
                });
            }
        });
    }

    private void addPost(Uri uriImage){
        String input = addPostInputTextCamera.getText().toString();
        if(input.equals("")){
            Toast.makeText(context, "Say something !!", Toast.LENGTH_SHORT).show();
        }else{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM,yyyy");
            final String date = simpleDateFormat.format(new Date());
            DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
            final String time = timeFormat.format(new Date());

            final Firebase mPostsRef = new Firebase("https://aftha7-2a05e.firebaseio.com/posts");
            Post p = new Post(0,addPostInputTextCamera.getText().toString(),uriImage.toString(),"IMAGE",date,time,latLocation,longLoction, mAuth.getCurrentUser().getUid().toString());
            Firebase refPostName = mPostsRef.push();
            refPostName.setValue(p);
            Log.e("NEW ID//",refPostName.getKey().toString());
        }
    }
}
