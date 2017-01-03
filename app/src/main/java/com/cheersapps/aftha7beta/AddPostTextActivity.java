package com.cheersapps.aftha7beta;

import android.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cheersapps.aftha7beta.entity.Post;
import com.cheersapps.aftha7beta.entity.User;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class AddPostTextActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Button btnAddPostText;
    ImageButton btnAddPostLocation,btnAllowLocation;
    EditText inputNewPostText;
    ImageButton btnReturn;

    private Firebase mRef;
    FirebaseAuth mAuth;
    Context context;
    double latLocation,longLoction = 0;

    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    boolean myLocationCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post_text);
        context = this;
        Firebase.setAndroidContext(context);
        mAuth = FirebaseAuth.getInstance();

        //Toolbar
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_return);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow_32x32);
        toolbar.setTitle("Add Post");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/
        //End Toolbar


        btnAddPostText = (Button) findViewById(R.id.btn_add_post_for_input_text);
        btnAddPostLocation = (ImageButton) findViewById(R.id.btn_add_post_text_location);
        inputNewPostText = (EditText) findViewById(R.id.add_post_input_text);
        btnReturn = (ImageButton)findViewById(R.id.btn_back_from_add_post_text);
        btnAllowLocation = (ImageButton) findViewById(R.id.btn_allow_my_location);
        mRef = new Firebase("https://aftha7-2a05e.firebaseio.com/posts");

        latLocation = getIntent().getDoubleExtra("lat",0);
        longLoction = getIntent().getDoubleExtra("long",0);

        if(latLocation != 0 && longLoction != 0 ){
            MyDynamicToast.informationMessage(AddPostTextActivity.this, "Location successfully set");
        }


        btnAddPostText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPost();
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnAddPostLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(AddPostTextActivity.this,AddPostLocationActivity.class);
                in.putExtra("from",1);
                startActivity(in);
            }
        });

        if (checkPlayServices()) {
            buildGoogleApiClient();
        }


        btnAllowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myLocationCheck){
                    latLocation = 0;
                    longLoction = 0;
                    mGoogleApiClient.disconnect();
                    btnAllowLocation.setImageResource(R.mipmap.ic_location_off_black_36dp);
                    myLocationCheck = false;
                    btnAddPostLocation.setVisibility(View.VISIBLE);
                }else{
                    mGoogleApiClient.connect();
                    loadAllowMyLocationDialog();
                }
            }
        });
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
                    btnAllowLocation.setImageResource(R.mipmap.ic_location_on_black_36dp);
                    myLocationCheck = true;
                    btnAddPostLocation.setVisibility(View.INVISIBLE);
                    displayLocation();
                    MyDynamicToast.informationMessage(AddPostTextActivity.this, "Location granted");
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    btnAllowLocation.setImageResource(R.mipmap.ic_location_off_black_36dp);
                    latLocation = 0;
                    longLoction = 0;
                    btnAddPostLocation.setVisibility(View.VISIBLE);
                    MyDynamicToast.informationMessage(AddPostTextActivity.this, "Location denied");
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
            //Toast.makeText(context, "Say something", Toast.LENGTH_SHORT).show();
            MyDynamicToast.warningMessage(AddPostTextActivity.this, "Write something !!");
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
                    MyDynamicToast.successMessage(AddPostTextActivity.this, "Post Added Successfully :)");
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

    void loadAllowMyLocationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("If you activate this feature, Aftha7 is going to save your current position")
                .setTitle("Allow my location ?");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                if(checkLocationPermission()){
                    //Toast.makeText(context, "granted", Toast.LENGTH_LONG).show();
                    btnAllowLocation.setImageResource(R.mipmap.ic_location_on_black_36dp);
                    myLocationCheck = true;
                    btnAddPostLocation.setVisibility(View.INVISIBLE);
                    displayLocation();
                    MyDynamicToast.informationMessage(AddPostTextActivity.this, "Location granted");
                }else{
                    Toast.makeText(context, "not granted", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "FINE_LOCATION & COARSE_LOCATION not granted", Toast.LENGTH_SHORT).show();
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            latLocation = latitude;
            longLoction = longitude;
            //Toast.makeText(context, latLocation + "//" + longLoction, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Couldn't get the location. Make sure location is enabled on the device", Toast.LENGTH_LONG).show();
            latLocation = 0;
            longLoction = 0;
            btnAllowLocation.setImageResource(R.mipmap.ic_location_off_black_36dp);
            btnAddPostLocation.setVisibility(View.VISIBLE);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //displayLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }
}
