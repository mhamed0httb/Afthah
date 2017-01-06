package com.cheersapps.aftha7beta;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cheersapps.aftha7beta.entity.Media;
import com.cheersapps.aftha7beta.entity.MyNotif;
import com.cheersapps.aftha7beta.entity.Post;
import com.cheersapps.aftha7beta.entity.User;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AccountActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText log,pass,mail;
    Button signUp;
    ImageView iv,galleryPic;
    Context context;
    private DatabaseReference mDatabase;
    String chosenPic,picName;
    public  static final int GALLERY_INTENT = 2;
    StorageReference mStorage;

    ImageButton btnUpdateName,btnUpdateMail,btnUpdatePass,btnConfirmUpdate,btnRefusUpdate;
    LinearLayout confRufHolder;

    String whatNow = "";
    String currentName,currentMail,currentPass,currentPic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(AccountActivity.this);
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_account);
        log = (EditText) findViewById(R.id.profile_username);
        pass = (EditText) findViewById(R.id.profile_pass);
        mail = (EditText) findViewById(R.id.profile_mail);
        mail.setEnabled(false);
        galleryPic = (ImageView) findViewById(R.id.gallery_pic);
        signUp = (Button) findViewById(R.id.btn_deactivate_account);
        iv = (ImageView) findViewById(R.id.profile_pic) ;

        btnUpdateName = (ImageButton)findViewById(R.id.btn_update_name);
        btnUpdateMail = (ImageButton)findViewById(R.id.btn_update_mail);
        btnUpdatePass = (ImageButton)findViewById(R.id.btn_update_pass);
        btnConfirmUpdate = (ImageButton)findViewById(R.id.btn_confirm_update_profile);
        btnRefusUpdate = (ImageButton)findViewById(R.id.btn_refus_update_profile);
        confRufHolder = (LinearLayout)findViewById(R.id.conf_ref_holder);

        mStorage = FirebaseStorage.getInstance().getReference();
        picName = mAuth.getCurrentUser().getUid();
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDefaultPics();
                whatNow = "PIC";
                confRufHolder.setVisibility(View.VISIBLE);

                btnUpdateName.setClickable(false);
                btnUpdateMail.setClickable(false);
                btnUpdatePass.setClickable(false);
            }
        });
        context = this;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //Toolbar toolbar = (Toolbar) findViewById(R.id.product_toolBar_title);
        //setSupportActionBar(toolbar);
       // toolbar.setNavigationIcon(R.drawable.back);
       /* toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/
        Firebase ownerRef = new Firebase("https://aftha7-2a05e.firebaseio.com/users/" + mAuth.getCurrentUser().getUid().toString());
        ownerRef.addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                //EVERYTHING ELSE HERE
                log.setText(currentUser.getName());
                mail.setText(currentUser.getMail());
                pass.setText(currentUser.getPass());

                currentName = currentUser.getName();
                currentMail = currentUser.getMail();
                currentPass = currentUser.getPass();
                currentPic = currentUser.getImage();
              /*  if(currentUser.getImage().contains("cherni")){
                    StorageReference pathReference = mStorage.child("profilepics/"+currentUser.getImage());
                    pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.with(context).load(uri.toString()).centerCrop()
                                    .resize(iv.getMeasuredWidth(),iv.getMeasuredHeight()).into(iv);
                            chosenPic = uri.toString();
                        }
                    });
                }else{*/
                Picasso.with(context).load(currentUser.getImage()).centerCrop()
                        .resize(iv.getMeasuredWidth(),iv.getMeasuredHeight()).into(iv);
                //}

                //END EVERYTHING ELSE HERE
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                user.updatePassword(pass.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AccountActivity.this, "Password is updated!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AccountActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });



                user.updateEmail(mail.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AccountActivity.this, "Email address is updated.", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(AccountActivity.this, "Failed to update email!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).setValue(new User(log.getText().toString(),mail.getText().toString(),
                        pass.getText().toString(),chosenPic));

                startActivity(new Intent(AccountActivity.this,FeedActivity.class));
                Toast.makeText(AccountActivity.this,"User updated",Toast.LENGTH_SHORT).show();*/

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to deactivate your account ?")
                        .setTitle("Delete");

                builder.setPositiveButton("Deactivate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                        final String myUid = mAuth.getCurrentUser().getUid().toString();

                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseDatabase.getInstance().getReference().child("users").child(myUid).removeValue();
                                            DatabaseReference mDataba;
                                            mDataba = FirebaseDatabase.getInstance().getReference().child("posts");
                                            mDataba.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
                                                @Override
                                                public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                                                    Post po = dataSnapshot.getValue(Post.class);
                                                    po.setId(dataSnapshot.getKey().toString());
                                                    if(po.getOwner().equals(myUid)){
                                                        deleteOnePost(po);
                                                    }
                                                }

                                                @Override
                                                public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

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
                                            mAuth.signOut();
                                            Intent in = new Intent(AccountActivity.this,MainActivity.class);
                                            startActivity(in);

                                        }
                                    }
                                });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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
        });


        btnUpdateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whatNow = "NAME";
                log.setEnabled(true);
                log.setFocusable(true);
                confRufHolder.setVisibility(View.VISIBLE);

                btnUpdateMail.setClickable(false);
                btnUpdatePass.setClickable(false);
            }
        });

        btnUpdateMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whatNow = "MAIL";
                mail.setEnabled(true);
                mail.setFocusable(true);
                confRufHolder.setVisibility(View.VISIBLE);

                btnUpdateName.setClickable(false);
                btnUpdatePass.setClickable(false);
            }
        });

        btnUpdatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whatNow = "PASS";
                pass.setEnabled(true);
                pass.setFocusable(true);
                confRufHolder.setVisibility(View.VISIBLE);

                btnUpdateMail.setClickable(false);
                btnUpdateName.setClickable(false);
            }
        });

        btnConfirmUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(whatNow.equals("NAME")){
                    DatabaseReference mData = FirebaseDatabase.getInstance().getReference().child("users");
                    mData.child(mAuth.getCurrentUser().getUid()).child("name").setValue(log.getText().toString());
                    currentName = log.getText().toString();
                    log.setText(currentName);
                    log.setEnabled(false);
                    log.setFocusable(false);
                    confRufHolder.setVisibility(View.GONE);

                    btnUpdateMail.setClickable(true);
                    btnUpdatePass.setClickable(true);

                    Toast.makeText(context, "Username Updated successfully", Toast.LENGTH_LONG).show();
                }
                else if(whatNow.equals("MAIL")){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    AuthCredential credential = EmailAuthProvider
                            .getCredential(currentMail, currentPass);

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    FirebaseUser userr = FirebaseAuth.getInstance().getCurrentUser();

                                    userr.updateEmail(mail.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        DatabaseReference mData = FirebaseDatabase.getInstance().getReference().child("users");
                                                        mData.child(mAuth.getCurrentUser().getUid()).child("mail").setValue(mail.getText().toString());
                                                        currentMail = mail.getText().toString();
                                                        mail.setText(currentMail);
                                                        mail.setEnabled(false);
                                                        mail.setFocusable(false);
                                                        Toast.makeText(context, "E-mail Updated successfully", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                }
                            });
                    confRufHolder.setVisibility(View.GONE);

                    btnUpdateName.setClickable(true);
                    btnUpdatePass.setClickable(true);
                }
                else if(whatNow.equals("PASS")){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    AuthCredential credential = EmailAuthProvider
                            .getCredential(currentMail, currentPass);

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    FirebaseUser userr = FirebaseAuth.getInstance().getCurrentUser();

                                    userr.updatePassword(pass.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        DatabaseReference mData = FirebaseDatabase.getInstance().getReference().child("users");
                                                        mData.child(mAuth.getCurrentUser().getUid()).child("pass").setValue(pass.getText().toString());
                                                        currentPass = pass.getText().toString();
                                                        pass.setText(currentPass);
                                                        pass.setEnabled(false);
                                                        pass.setFocusable(false);
                                                        Toast.makeText(context, "Password Updated successfully", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                }
                            });
                    confRufHolder.setVisibility(View.GONE);

                    btnUpdateMail.setClickable(true);
                    btnUpdateName.setClickable(true);
                }
                else if(whatNow.equals("PIC")){

                    DatabaseReference mData = FirebaseDatabase.getInstance().getReference().child("users");
                    mData.child(mAuth.getCurrentUser().getUid()).child("image").setValue(chosenPic);

                    currentPic = chosenPic;
                    confRufHolder.setVisibility(View.GONE);

                    btnUpdateName.setClickable(true);
                    btnUpdateMail.setClickable(true);
                    btnUpdatePass.setClickable(true);

                    Toast.makeText(context, "Picture Updated successfully", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnRefusUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(whatNow.equals("NAME")){
                    log.setText(currentName);
                    log.setEnabled(false);
                    log.setFocusable(false);
                    confRufHolder.setVisibility(View.GONE);

                    btnUpdateMail.setClickable(true);
                    btnUpdatePass.setClickable(true);
                }
                else if(whatNow.equals("MAIL")){
                    mail.setText(currentMail);
                    mail.setEnabled(false);
                    mail.setFocusable(false);
                    confRufHolder.setVisibility(View.GONE);

                    btnUpdateName.setClickable(true);
                    btnUpdatePass.setClickable(true);
                }
                else if(whatNow.equals("PASS")){
                    pass.setText(currentPass);
                    pass.setEnabled(false);
                    pass.setFocusable(false);
                    confRufHolder.setVisibility(View.GONE);

                    btnUpdateMail.setClickable(true);
                    btnUpdateName.setClickable(true);
                }
                else if(whatNow.equals("PIC")){
                    confRufHolder.setVisibility(View.GONE);
                    Picasso.with(context).load(currentPic).centerCrop()
                            .resize(iv.getMeasuredWidth(),iv.getMeasuredHeight()).into(iv);
                    btnUpdateMail.setClickable(true);
                    btnUpdateName.setClickable(true);
                    btnUpdatePass.setClickable(true);
                }
            }
        });

        confRufHolder.setVisibility(View.GONE);
        log.setEnabled(false);
        mail.setEnabled(false);
        pass.setEnabled(false);






    }


    public void loadDefaultPics(){


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
        Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fincognito.png?alt=media&token=c85b49aa-d562-483a-ad85-41fb5ff954f8").into(pic1);
        Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fman%20(3).png?alt=media&token=816a29d4-35db-44f1-96e0-4c32572d0831").into(pic2);
        Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fman%20(4).png?alt=media&token=b939dc67-98e5-4b24-af0c-9646b1a6971f").into(pic3);
        Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fuser%20(6).png?alt=media&token=71e527e7-d129-425f-987e-b679777a12d3").into(pic4);
        Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fuser%20(7).png?alt=media&token=9ea73f37-3353-49a6-b73f-e0ea007e1969").into(pic5);
        Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fuser%20(8).png?alt=media&token=cff77c3f-a7be-4709-b1e8-385e71f0d922").into(pic6);
        Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fadd-photo-album.png?alt=media&token=a5daf740-245e-4bec-8ad9-3a88a58e3c1e").into(pic7);
        pic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                chosenPic = "https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fincognito.png?alt=media&token=c85b49aa-d562-483a-ad85-41fb5ff954f8";
                Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fincognito.png?alt=media&token=c85b49aa-d562-483a-ad85-41fb5ff954f8").into(iv);
            }
        });
        pic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                chosenPic = "https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fman%20(3).png?alt=media&token=816a29d4-35db-44f1-96e0-4c32572d0831";
                Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fman%20(3).png?alt=media&token=816a29d4-35db-44f1-96e0-4c32572d0831").into(iv);
            }
        });
        pic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                chosenPic = "https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fman%20(4).png?alt=media&token=b939dc67-98e5-4b24-af0c-9646b1a6971f";
                Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fman%20(4).png?alt=media&token=b939dc67-98e5-4b24-af0c-9646b1a6971f").into(iv);
            }
        });
        pic4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                chosenPic = "https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fuser%20(6).png?alt=media&token=71e527e7-d129-425f-987e-b679777a12d3";
                Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fuser%20(6).png?alt=media&token=71e527e7-d129-425f-987e-b679777a12d3").into(iv);
            }
        });
        pic5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                chosenPic = "https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fuser%20(7).png?alt=media&token=9ea73f37-3353-49a6-b73f-e0ea007e1969";
                Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fuser%20(7).png?alt=media&token=9ea73f37-3353-49a6-b73f-e0ea007e1969").into(iv);
            }
        });
        pic6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                chosenPic = "https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fuser%20(8).png?alt=media&token=cff77c3f-a7be-4709-b1e8-385e71f0d922";
                Picasso.with(context).load("https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/profilepics%2Fuser%20(8).png?alt=media&token=cff77c3f-a7be-4709-b1e8-385e71f0d922").into(iv);
            }
        });
        pic7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            Uri uri = data.getData();
            StorageReference filepath = mStorage.child("profilepics").child(picName);
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference pathReference = mStorage.child("profilepics/"+picName);
                    pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.with(context).load(uri.toString()).centerCrop()
                                    .resize(iv.getMeasuredWidth(),iv.getMeasuredHeight()).into(iv);
                            chosenPic = uri.toString();

                            whatNow = "PIC";
                            confRufHolder.setVisibility(View.VISIBLE);

                            btnUpdateName.setClickable(false);
                            btnUpdateMail.setClickable(false);
                            btnUpdatePass.setClickable(false);
                        }
                    });
                }
            });
        }
    }

    void deleteOnePost(final Post p){
        if(p.getMediaType().equals("NOFILE")){
            FirebaseDatabase.getInstance().getReference().child("posts").child(p.getId()).removeValue();
            //Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
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
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                            //Toast.makeText(context, "error on delete", Toast.LENGTH_SHORT).show();
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
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                            //Toast.makeText(context, "error on delete", Toast.LENGTH_SHORT).show();
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
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                            //Toast.makeText(context, "error on delete", Toast.LENGTH_SHORT).show();
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
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AccountActivity.this,FeedActivity.class));
    }
}
