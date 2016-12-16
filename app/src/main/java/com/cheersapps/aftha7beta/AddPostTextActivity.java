package com.cheersapps.aftha7beta;

import android.*;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cheersapps.aftha7beta.entity.Post;
import com.cheersapps.aftha7beta.entity.User;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class AddPostTextActivity extends AppCompatActivity {

    Button btnAddPostText;
    EditText inputNewPostText;

    private Firebase mRef;
    FirebaseAuth mAuth;
    Context context;
    double latLocation,longLoction = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post_text);
        context = this;
        Firebase.setAndroidContext(context);
        mAuth = FirebaseAuth.getInstance();

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_return);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow_32x32);
        toolbar.setTitle("Add Post");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //End Toolbar

        btnAddPostText = (Button) findViewById(R.id.btn_add_post_for_input_text);
        inputNewPostText = (EditText) findViewById(R.id.add_post_input_text);
        mRef = new Firebase("https://aftha7-2a05e.firebaseio.com/posts");

        if(checkLocationPermission()){
            Toast.makeText(context, "granted", Toast.LENGTH_LONG).show();
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            String locationProvider = LocationManager.GPS_PROVIDER;
            Location myLocation = locationManager.getLastKnownLocation(locationProvider);
            if(myLocation != null){
                longLoction = myLocation.getLongitude();
                latLocation = myLocation.getLatitude();
                Toast.makeText(context, "lat " + latLocation, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context, "lat & lng null", Toast.LENGTH_LONG).show();
            }

            btnAddPostText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addPost();
                }
            });





        }else{
            Toast.makeText(context, "not granted", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
    }



    public boolean checkLocationPermission()
    {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    finish();
                    startActivity(getIntent());

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Intent in = new Intent(AddPostTextActivity.this,FeedActivity.class);
                    startActivity(in);
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void addPost(){
        String input = inputNewPostText.getText().toString();
        if(input.equals("")){
            Toast.makeText(context, "Say something", Toast.LENGTH_SHORT).show();
        }else{
            ProgressDialog loading = new ProgressDialog(context);
            loading.setMessage("Please wait");
            loading.setCanceledOnTouchOutside(false);
            loading.show();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM,yyyy");
            final String date = simpleDateFormat.format(new Date());

            DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
            final String time = timeFormat.format(new Date());


            //GET OWNER DATA
            final String ownerUid = mAuth.getCurrentUser().getUid().toString();
            Firebase ownerRef = new Firebase("https://aftha7-2a05e.firebaseio.com/users/" + ownerUid);
            ownerRef.addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
                @Override
                public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                    User ownerUser = dataSnapshot.getValue(User.class);
                    //EVERYTHING ELSE HERE
                    //Post p = new Post(0,inputNewPostText.getText().toString(),"https://firebasestorage.googleapis.com/v0/b/aftha7-2a05e.appspot.com/o/white.png?alt=media&token=7c98551c-e474-430c-956e-0f78715ea30f",date,time,latLocation,longLoction,ownerUser);
                    Post p = new Post(0,inputNewPostText.getText().toString(),"NOFILE","NOFILE",date,time,latLocation,longLoction,ownerUid);
                    mRef.push().setValue(p);
                    Intent in = new Intent(AddPostTextActivity.this, FeedActivity.class);
                    startActivity(in);
                    //END EVERYTHING ELSE HERE
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
            //END GET OWNER DATA
        }

    }
}
