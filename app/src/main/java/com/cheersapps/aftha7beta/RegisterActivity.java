package com.cheersapps.aftha7beta;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cheersapps.aftha7beta.entity.User;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText log,pass,mail;
    Button signUp;
    FirebaseAuth mAuth;
    ImageButton profile;
    private Firebase mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Firebase.setAndroidContext(this);
        log = (EditText) findViewById(R.id.et_register);
        pass = (EditText) findViewById(R.id.et_password);
        mail = (EditText) findViewById(R.id.et_mail);
        signUp = (Button) findViewById(R.id.btn_register);
        mAuth = FirebaseAuth.getInstance();
        profile = (ImageButton) findViewById(R.id.pic);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this, "signing up now", Toast.LENGTH_LONG).show();
                mAuth.createUserWithEmailAndPassword(mail.getText().toString(),pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mAuth.signInWithEmailAndPassword(mail.getText().toString(),pass.getText().toString());

                            mRef = new Firebase("https://aftha7-2a05e.firebaseio.com/users");

                            mRef.child(mAuth.getCurrentUser().getUid().toString()).setValue(new User(log.getText().toString(),mail.
                                    getText().toString(),pass.getText().toString(),"pic"));
                        }else{
                            Toast.makeText(RegisterActivity.this, "signing up failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(in);
    }
}
