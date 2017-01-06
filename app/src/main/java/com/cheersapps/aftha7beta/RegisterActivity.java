package com.cheersapps.aftha7beta;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.cheersapps.aftha7beta.entity.User;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

public class RegisterActivity extends AppCompatActivity {

    EditText log,pass,mail;
    Button signUp;
    FirebaseAuth mAuth;
    ImageView profile;
    private Firebase mRef;
    String chosenPic ;
    Context context = this;
    int index =0,deleteIndex=0;
    public  static final int GALLERY_INTENT = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    StorageReference mStorage;
    String picId;
    Uri myUri;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Firebase.setAndroidContext(this);
        mStorage = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        log = (EditText) findViewById(R.id.et_register);
        pass = (EditText) findViewById(R.id.et_password);
        mail = (EditText) findViewById(R.id.et_mail);
        signUp = (Button) findViewById(R.id.btn_register);
        mAuth = FirebaseAuth.getInstance();
        picId = getRandomString();
        profile = (ImageView) findViewById(R.id.pic);
        Picasso.with(this).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fadd-photo-album.png?alt=media&token=a5daf740-245e-4bec-8ad9-3a88a58e3c1e").into(profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDefaultPics();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(RegisterActivity.this, "signing up now", Toast.LENGTH_LONG).show();
                progressDialog.setMessage("signing up, Please wait");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                mAuth.createUserWithEmailAndPassword(mail.getText().toString(),pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mAuth.signInWithEmailAndPassword(mail.getText().toString(),pass.getText().toString());

                            mRef = new Firebase("https://aftha7-2a05e.firebaseio.com/users");
                            if(index==1){
                                mRef.child(mAuth.getCurrentUser().getUid().toString()).setValue(new User(log.getText().toString(),mail.getText().toString(),
                                        pass.getText().toString(),chosenPic));
                            }else {
                                mRef.child(mAuth.getCurrentUser().getUid().toString()).setValue(new User(log.getText().toString(),mail.getText().toString(),
                                        pass.getText().toString(),"https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fadd-photo-album.png?alt=media&token=a5daf740-245e-4bec-8ad9-3a88a58e3c1e"));
                            }
                            progressDialog.dismiss();


                        }else{
                            Toast.makeText(RegisterActivity.this, "signing up failed", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "something went wrong", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });


            }
        });


        if(checkCameraPermission()){
            //Toast.makeText(context, "granted", Toast.LENGTH_LONG).show();

        }else{

            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, 1);
        }

    }

    private void loadDefaultPics() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_profilepic);
        //dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // set the custom dialog components - text, image and button
        final ImageView pic1 = (ImageView) dialog.findViewById(R.id.pic1);
        final ImageView pic2 = (ImageView) dialog.findViewById(R.id.pic2);
        final ImageView pic3 = (ImageView) dialog.findViewById(R.id.pic3);
        final ImageView pic4 = (ImageView) dialog.findViewById(R.id.pic4);
        final ImageView pic5 = (ImageView) dialog.findViewById(R.id.pic5);
        final ImageView pic6 = (ImageView) dialog.findViewById(R.id.pic6);
        final ImageView pic7 = (ImageView) dialog.findViewById(R.id.gallery_pic);
        final ImageView pic8 = (ImageView) dialog.findViewById(R.id.camera_pic);
        Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fincognito.png?alt=media&token=c85b49aa-d562-483a-ad85-41fb5ff954f8").into(pic1);
        Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fman%20(3).png?alt=media&token=816a29d4-35db-44f1-96e0-4c32572d0831").into(pic2);
        Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fman%20(4).png?alt=media&token=b939dc67-98e5-4b24-af0c-9646b1a6971f").into(pic3);
        Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fuser%20(6).png?alt=media&token=71e527e7-d129-425f-987e-b679777a12d3").into(pic4);
        Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fuser%20(7).png?alt=media&token=9ea73f37-3353-49a6-b73f-e0ea007e1969").into(pic5);
        Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fuser%20(8).png?alt=media&token=cff77c3f-a7be-4709-b1e8-385e71f0d922").into(pic6);
        Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fgallery2.png?alt=media&token=f69c555b-6828-438e-a44e-c2ebecd5a4bf").into(pic7);
        Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fcamera.png?alt=media&token=8ef39da8-b4f4-4836-9525-77341db53b62").into(pic8);

        pic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 1;
                deleteIndex = 0;
                dialog.dismiss();
                chosenPic = "https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fincognito.png?alt=media&token=c85b49aa-d562-483a-ad85-41fb5ff954f8";
                Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fincognito.png?alt=media&token=c85b49aa-d562-483a-ad85-41fb5ff954f8").into(profile);
            }
        });
        pic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 1;
                deleteIndex = 0;
                dialog.dismiss();
                chosenPic = "https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fman%20(3).png?alt=media&token=816a29d4-35db-44f1-96e0-4c32572d0831";
                Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fman%20(3).png?alt=media&token=816a29d4-35db-44f1-96e0-4c32572d0831").into(profile);
            }
        });
        pic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 1;
                deleteIndex = 0;
                dialog.dismiss();
                chosenPic = "https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fman%20(4).png?alt=media&token=b939dc67-98e5-4b24-af0c-9646b1a6971f";
                Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fman%20(4).png?alt=media&token=b939dc67-98e5-4b24-af0c-9646b1a6971f").into(profile);
            }
        });
        pic4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 1;
                deleteIndex = 0;
                dialog.dismiss();
                chosenPic = "https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fuser%20(6).png?alt=media&token=71e527e7-d129-425f-987e-b679777a12d3";
                Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fuser%20(6).png?alt=media&token=71e527e7-d129-425f-987e-b679777a12d3").into(profile);
            }
        });
        pic5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 1;
                deleteIndex = 0;
                dialog.dismiss();
                chosenPic = "https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fuser%20(7).png?alt=media&token=9ea73f37-3353-49a6-b73f-e0ea007e1969";
                Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fuser%20(7).png?alt=media&token=9ea73f37-3353-49a6-b73f-e0ea007e1969").into(profile);
            }
        });
        pic6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 1;
                deleteIndex = 0;
                dialog.dismiss();
                chosenPic = "https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fuser%20(8).png?alt=media&token=cff77c3f-a7be-4709-b1e8-385e71f0d922";
                Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fuser%20(8).png?alt=media&token=cff77c3f-a7be-4709-b1e8-385e71f0d922").into(profile);
            }
        });

        pic7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 1;
                deleteIndex++;
                dialog.dismiss();
                Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
                }

            }
        });

        pic8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 1;

                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);
            }
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode ==  RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profile.setImageBitmap(imageBitmap);
            try {
                encodeBitmapAndSaveToFirebase(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }



        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            Uri uri = data.getData();
            StorageReference filepath = mStorage.child("profilepics").child(picId);
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference pathReference = mStorage.child("profilepics/"+picId);
                    pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                           /* if(deleteIndex>1){
                                deleteIndex=0;
                                Task<Void> task = FirebaseStorage.getInstance().getReferenceFromUrl(chosenPic).delete();
                            }*/
                            Picasso.with(context).load(uri.toString()).centerCrop()
                                    .resize(profile.getMeasuredWidth(),profile.getMeasuredHeight()).into(profile);
                            chosenPic = uri.toString();
                        }
                    });
                }
            });
        }
    }

    private void encodeBitmapAndSaveToFirebase(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte[] bitmapdata = bytes.toByteArray();
        File f = new File(context.getCacheDir(), "cameeeeraaaa");
        f.createNewFile();
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        StorageReference filepath = mStorage.child("profilepics").child(picId);
        filepath.putFile(Uri.fromFile(f)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageReference pathReference = mStorage.child("profilepics/"+picId);
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                       /* if(deleteIndex>1){
                            deleteIndex=0;
                            Task<Void> task = FirebaseStorage.getInstance().getReferenceFromUrl(chosenPic).delete();
                        }*/
                        Picasso.with(context).load(uri.toString()).centerCrop()
                                .resize(profile.getMeasuredWidth(),profile.getMeasuredHeight()).into(profile);
                        chosenPic = uri.toString();
                    }
                });
            }
        });

    }

    public String getRandomString() {
        return "cherni"+new BigInteger(130,new SecureRandom()).toString(32);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(in);
    }
    public boolean checkCameraPermission()
    {
        String permission = "android.permission.CAMERA";
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
