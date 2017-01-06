package com.cheersapps.aftha7beta;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText loginInput,passwordInput;
    Button btnLogin,btnSignUp;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        loginInput = (EditText) findViewById(R.id.et_signin);
        passwordInput = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_signin);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
                // Intent in = new Intent(MainActivity.this,FeedActivity.class);
                //startActivity(in);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    Toast.makeText(MainActivity.this,"now u r logged in"+firebaseAuth.getCurrentUser().getUid(),Toast.LENGTH_SHORT).show();
                    Log.e("LOGGEDIN//",firebaseAuth.getCurrentUser().getUid());
                    startActivity(new Intent(MainActivity.this,FeedActivity.class));
                    finish();
                }


            }
        };
    }

    private void doLogin() {
        String login = loginInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if(!TextUtils.isEmpty(login) && !TextUtils.isEmpty(password)){
            progressDialog.setMessage("Loging,Please wait");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(login,password).
                    addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful()){
                                Toast.makeText(MainActivity.this,"Login successful",Toast.LENGTH_SHORT).show();
                                //Intent in = new Intent(MainActivity.this,FeedActivity.class);
                                //startActivity(in);
                            }else{
                                Toast.makeText(MainActivity.this,"Login failed",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Firebase.setAndroidContext(MainActivity.this);
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onBackPressed() {
        showExitDialog();

    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Are you leaving us soon !?");
        builder.setMessage("Choose carefully.");

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        finish();
                        System.exit(0);
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

}
