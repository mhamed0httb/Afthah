package com.cheersapps.aftha7beta;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.cheersapps.aftha7beta.entity.Post;
import com.cheersapps.aftha7beta.entity.User;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    ImageView imgProfile;
    Button updatePass,updateName,updateMail;
    TextView name,mail;
    Context context;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Firebase.setAndroidContext(ProfileActivity.this);
        context = this;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        imgProfile = (ImageView) findViewById(R.id.img_profile);
        updatePass = (Button) findViewById(R.id.btn_update_pass);
        updateName = (Button) findViewById(R.id.btn_update_name);
        updateMail = (Button) findViewById(R.id.btn_update_mail);
        name = (TextView) findViewById(R.id.name_profile);
        mail = (TextView) findViewById(R.id.mail_profile);

        mDatabase.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Picasso.with(context).load(user.getImage().toString()).centerCrop().fit().into(imgProfile);
                name.setText(user.getName().toString());
                mail.setText(user.getMail().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        updateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDialogUpdateName();
            }
        });

        updateMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDialogUpdateMail();
            }
        });

        updatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDialogUpdatePass();
            }
        });
    }


    void loadDialogUpdateName(){

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_update_profile_name);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // set the custom dialog components - text, image and button
        final EditText editName = (EditText) dialog.findViewById(R.id.edit_update_profile_name);
        Button btnEdit = (Button) dialog.findViewById(R.id.btn_update_profile_name);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editName.getText().equals("")){
                    Toast.makeText(context, "write something", Toast.LENGTH_LONG).show();
                }else{
                    mDatabase.child(mAuth.getCurrentUser().getUid()).child("name").setValue(editName.getText().toString());
                    finish();
                    startActivity(getIntent());
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    void loadDialogUpdateMail(){

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_update_profile_mail);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // set the custom dialog components - text, image and button
        final EditText editMail = (EditText) dialog.findViewById(R.id.edit_update_profile_mail);
        Button btnEdit = (Button) dialog.findViewById(R.id.btn_update_profile_mail);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editMail.getText().equals(null)){
                    Toast.makeText(context, "write something", Toast.LENGTH_LONG).show();
                }else{
                    // ReUTHENTICATE
                    FirebaseUser userr = mAuth.getCurrentUser();
                    AuthCredential credential = EmailAuthProvider
                            .getCredential("samra@mail.com", "password"); // need a popup here to enter mail and pass
                    userr.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(context, "Re-authenticated", Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    user.updateEmail(editMail.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                                                        mDatabase.child(mAuth.getCurrentUser().getUid()).child("mail").setValue(editMail.getText().toString());
                                                        finish();
                                                        startActivity(getIntent());
                                                    }
                                                }
                                            });
                                }
                            });
                    //END ReUTHENTICATE


                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    void loadDialogUpdatePass(){

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_update_profile_password);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // set the custom dialog components - text, image and button
        final EditText editPass = (EditText) dialog.findViewById(R.id.edit_update_profile_pass);
        Button btnEdit = (Button) dialog.findViewById(R.id.btn_update_profile_pass);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editPass.getText().equals(null)){
                    Toast.makeText(context, "write something", Toast.LENGTH_LONG).show();
                }else{
                    // ReUTHENTICATE
                    FirebaseUser userr = mAuth.getCurrentUser();
                    AuthCredential credential = EmailAuthProvider
                            .getCredential("samra@mail.com", "password"); // need a popup here to enter mail and pass
                    userr.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(context, "Re-authenticated", Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    user.updatePassword(editPass.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                                                        mDatabase.child(mAuth.getCurrentUser().getUid()).child("pass").setValue(editPass.getText().toString());
                                                        finish();
                                                        startActivity(getIntent());
                                                    }
                                                }
                                            });



                                }
                            });
                    //END ReUTHENTICATE


                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
