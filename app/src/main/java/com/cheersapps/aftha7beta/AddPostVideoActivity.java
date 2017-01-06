package com.cheersapps.aftha7beta;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.afollestad.materialcamera.MaterialCamera;
import com.cheersapps.aftha7beta.entity.Media;
import com.cheersapps.aftha7beta.entity.Post;
import com.cheersapps.aftha7beta.entity.PostCamera;
import com.cheersapps.aftha7beta.thread.AddPostCameraThread;
import com.cheersapps.aftha7beta.thread.AddPostVideoThread;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import java.util.Date;

public class AddPostVideoActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final static int CAMERA_RQ = 6969;
    private StorageReference mStorage;
    FirebaseAuth mAuth;
    private ProgressDialog progressDialogGetFile;

    EditText addPostInputTextVideo;
    Button btnAddPostVideo,btnChangeVideo;

    VideoView displayVideo;
    ImageButton btnPauseVideo,btnPlayvideo,btnStopVideo;

    Context context;

    double latLocation,longLoction = 0;

    ImageButton btnReturn;
    ImageButton btnAddPostLocation,btnAllowLocation;

    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    boolean myLocationCheck = false;
    MarkerOptions marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post_video);

        context = this;
        Firebase.setAndroidContext(context);
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        marker = new MarkerOptions();

        addPostInputTextVideo = (EditText) findViewById(R.id.add_post_input_text_video);
        btnAddPostVideo = (Button) findViewById(R.id.btn_add_post_video);
        btnChangeVideo = (Button) findViewById(R.id.btn_change_video);
        displayVideo = (VideoView)findViewById(R.id.display_video_take_post_video);
        btnPlayvideo = (ImageButton)findViewById(R.id.btn_play_video);
        btnPauseVideo = (ImageButton)findViewById(R.id.btn_pause_video);
        btnStopVideo = (ImageButton)findViewById(R.id.btn_stop_video);
        progressDialogGetFile = new ProgressDialog(context);
        btnReturn = (ImageButton)findViewById(R.id.btn_back_from_add_post_video);
        btnAddPostLocation = (ImageButton) findViewById(R.id.btn_add_post_video_location);
        btnAllowLocation = (ImageButton) findViewById(R.id.btn_allow_my_location_video);

        btnPlayvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayVideo.start();
            }
        });

        btnPauseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayVideo.pause();
            }
        });

        btnStopVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayVideo.stopPlayback();
            }
        });

        if (checkPlayServices()) {
            buildGoogleApiClient();
        }

        if(checkCameraPermission()){
            if(checkAudioPermission()){
                if(checkExternalStoragePermission()){
                    startMaterialCamera();
                }else{
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }else{
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 3);
            }
        }else{
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 2);
        }

        btnChangeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMaterialCamera();
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
                loadDialogSetPostLocation();
            }
        });

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

        btnAddPostVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDynamicToast.warningMessage(AddPostVideoActivity.this, "Please add a Video !!");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_RQ) {

            if (resultCode == RESULT_OK) {
                //Toast.makeText(context, "Saved to: " + data.getDataString(), Toast.LENGTH_LONG).show();
                final Uri vidUri = data.getData();
                displayVideo.setVideoURI(vidUri);
                displayVideo.start();
                btnAddPostVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(addPostInputTextVideo.getText().toString().equals("")){
                            MyDynamicToast.warningMessage(AddPostVideoActivity.this, "Write something !!");
                        }else if(vidUri == null){
                            MyDynamicToast.warningMessage(AddPostVideoActivity.this, "Please add a Video !!");
                        }else{
                            PostCamera pc = new PostCamera(vidUri,addPostInputTextVideo.getText().toString(),latLocation,longLoction);
                            AddPostVideoThread th = new AddPostVideoThread(context);
                            th.execute(pc);
                            startActivity(new Intent(AddPostVideoActivity.this,FeedActivity.class));
                            //uploadVideo(vidUri);
                        }

                    }
                });
            } else if(data != null) {
                Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
                e.printStackTrace();
                //Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                MyDynamicToast.errorMessage(AddPostVideoActivity.this, "Error on Camera : " + e.getMessage());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startMaterialCamera();
                    startMaterialCamera();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Intent in = new Intent(AddPostVideoActivity.this,FeedActivity.class);
                    startActivity(in);
                }
                return;
            }

            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startMaterialCamera();
                    finish();
                    startActivity(getIntent());
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Intent in = new Intent(AddPostVideoActivity.this,FeedActivity.class);
                    startActivity(in);
                }
                return;
            }

            case 3: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startMaterialCamera();
                    finish();
                    startActivity(getIntent());
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Intent in = new Intent(AddPostVideoActivity.this,FeedActivity.class);
                    startActivity(in);
                }
                return;
            }
            case 4: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    btnAllowLocation.setImageResource(R.mipmap.ic_location_on_black_36dp);
                    myLocationCheck = true;
                    btnAddPostLocation.setVisibility(View.INVISIBLE);
                    displayLocation();
                    MyDynamicToast.informationMessage(AddPostVideoActivity.this, "Location granted");
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    btnAllowLocation.setImageResource(R.mipmap.ic_location_off_black_36dp);
                    latLocation = 0;
                    longLoction = 0;
                    btnAddPostLocation.setVisibility(View.VISIBLE);
                    MyDynamicToast.informationMessage(AddPostVideoActivity.this, "Location denied");
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public boolean checkExternalStoragePermission()
    {
        String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
    public boolean checkCameraPermission()
    {
        String permission = "android.permission.CAMERA";
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
    public boolean checkAudioPermission()
    {
        String permission = "android.permission.RECORD_AUDIO";
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private void startMaterialCamera(){
        File saveFolder = new File(Environment.getExternalStorageDirectory(), "MaterialCamera Sample");
        if (!saveFolder.exists()) saveFolder.mkdir();
        new MaterialCamera(this)                               // Constructor takes an Activity
                .allowRetry(true)                                  // Whether or not 'Retry' is visible during playback
                .autoSubmit(false)                                 // Whether or not user is allowed to playback videos after recording. This can affect other things, discussed in the next section.
                .saveDir(saveFolder)                               // The folder recorded videos are saved to
                .primaryColorAttr(R.attr.colorPrimary)             // The theme color used for the camera, defaults to colorPrimary of Activity in the constructor
                .showPortraitWarning(true)                         // Whether or not a warning is displayed if the user presses record in portrait orientation
                .defaultToFrontFacing(false)                       // Whether or not the camera will initially show the front facing camera
                .retryExits(false)                                 // If true, the 'Retry' button in the playback screen will exit the camera instead of going back to the recorder
                .restartTimerOnRetry(false)                        // If true, the countdown timer is reset to 0 when the user taps 'Retry' in playback
                .continueTimerInPlayback(false)                    // If true, the countdown timer will continue to go down during playback, rather than pausing.
                .videoEncodingBitRate(1024000)                     // Sets a custom bit rate for video recording.
                .audioEncodingBitRate(50000)                       // Sets a custom bit rate for audio recording.
                .videoFrameRate(24)                                // Sets a custom frame rate (FPS) for video recording.
                .qualityProfile(MaterialCamera.QUALITY_HIGH)       // Sets a quality profile, manually setting bit rates or frame rates with other settings will overwrite individual quality profile settings
                .videoPreferredHeight(720)                         // Sets a preferred height for the recorded video output.
                .videoPreferredAspect(4f / 3f)                     // Sets a preferred aspect ratio for the recorded video output.
                .maxAllowedFileSize(1024 * 1024 * 5)               // Sets a max file size of 5MB, recording will stop if file reaches this limit. Keep in mind, the FAT file system has a file size limit of 4GB.
                .iconRecord(R.drawable.mcam_action_capture)        // Sets a custom icon for the button used to start recording
                .iconStop(R.drawable.mcam_action_stop)             // Sets a custom icon for the button used to stop recording
                .iconFrontCamera(R.drawable.mcam_camera_front)     // Sets a custom icon for the button used to switch to the front camera
                .iconRearCamera(R.drawable.mcam_camera_rear)       // Sets a custom icon for the button used to switch to the rear camera
                .iconPlay(R.drawable.evp_action_play)              // Sets a custom icon used to start playback
                .iconPause(R.drawable.evp_action_pause)            // Sets a custom icon used to pause playback
                .iconRestart(R.drawable.evp_action_restart)        // Sets a custom icon used to restart playback
                .labelRetry(R.string.mcam_retry)                   // Sets a custom button label for the button used to retry recording, when available
                .labelConfirm(R.string.mcam_use_video)             // Sets a custom button label for the button used to confirm/submit a recording
                .autoRecordWithDelaySec(3)                         // The video camera will start recording automatically after a 5 second countdown. This disables switching between the front and back camera initially.
                .autoRecordWithDelayMs(3000)                       // Same as the above, expressed with milliseconds instead of seconds.
                .audioDisabled(false)                              // Set to true to record video without any audio.
                .start(CAMERA_RQ);
    }

    void uploadVideo(Uri u){
        String input = addPostInputTextVideo.getText().toString();
        if(input.equals("")){
            //Toast.makeText(context, "Say something !!", Toast.LENGTH_SHORT).show();
            MyDynamicToast.warningMessage(AddPostVideoActivity.this, "Write something !!");
        }else{
            final ProgressDialog progressDialogUploading = new ProgressDialog(context);
            final Firebase mRef = new Firebase("https://aftha7-2a05e.firebaseio.com/");
            Firebase mmRef = mRef.child("postVideos");
            Firebase newPostRef = mmRef.push();
            //newPostRef.setValue("just a video");
            newPostRef.setValue(new Media(newPostRef.getKey().toString(),"NOTYET"));
            final String newVideoName = newPostRef.getKey();
            progressDialogUploading.setMessage("Uploading...");
            progressDialogUploading.setCanceledOnTouchOutside(false);
            progressDialogUploading.show();
            StorageReference filePath = mStorage.child("postVideos").child(newVideoName);
            filePath.putFile(u).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialogUploading.dismiss();
                    StorageReference pathReference = mStorage.child("postVideos/"+newVideoName);
                    pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Toast.makeText(context, "done : " + uri.toString(), Toast.LENGTH_LONG).show();
                            //ADD POST HERE
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM,yyyy");
                            final String date = simpleDateFormat.format(new Date());
                            DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
                            final String time = timeFormat.format(new Date());

                            final Firebase mPostsRef = new Firebase("https://aftha7-2a05e.firebaseio.com/posts");
                            Post p = new Post(0,addPostInputTextVideo.getText().toString(),uri.toString(),"VIDEO",date,time,latLocation,longLoction, mAuth.getCurrentUser().getUid().toString());
                            Firebase refPostName = mPostsRef.push();
                            refPostName.setValue(p);
                            Log.e("NEW ID//",refPostName.getKey().toString());
                            String postId = refPostName.getKey().toString();
                            FirebaseDatabase.getInstance().getReference().child("postVideos").child(newVideoName).child("postId").setValue(refPostName.getKey());
                            //END ADD POST HERE
                            MyDynamicToast.successMessage(AddPostVideoActivity.this, "Post Added Successfully :)");
                            startActivity(new Intent(AddPostVideoActivity.this,FeedActivity.class));
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    MyDynamicToast.errorMessage(AddPostVideoActivity.this, "Something went wrong");
                }
            });
        }
    }

    public boolean checkLocationPermission()
    {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
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
                    MyDynamicToast.informationMessage(AddPostVideoActivity.this, "Location granted");
                    btnAllowLocation.setImageResource(R.mipmap.ic_location_on_black_36dp);
                    myLocationCheck = true;
                    btnAddPostLocation.setVisibility(View.INVISIBLE);
                    displayLocation();
                }else{
                    //Toast.makeText(context, "not granted", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 4);
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
            //Toast.makeText(context, "Couldn't get the location. Make sure location is enabled on the device", Toast.LENGTH_LONG).show();
            MyDynamicToast.warningMessage(AddPostVideoActivity.this, "Couldn't get the location. Make sure location is enabled on the device");
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
                //Toast.makeText(getApplicationContext(),
                        //"This device is not supported.", Toast.LENGTH_LONG)
                        //.show();
                MyDynamicToast.informationMessage(AddPostVideoActivity.this, "This device is not supported");
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddPostVideoActivity.this,FeedActivity.class));
    }

    void loadDialogSetPostLocation(){
        final Dialog dialog = new Dialog(context,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_set_post_location);


        // set the custom dialog components - text, image and button
        final Button btnAddLocation = (Button) dialog.findViewById(R.id.btn_set_post_location_map);
        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDynamicToast.warningMessage(AddPostVideoActivity.this, "Choose a location");
            }
        });
        MapView mMapView;
        MapsInitializer.initialize(context);

        mMapView = (MapView) dialog.findViewById(R.id.map_set_post_location);
        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                LatLng kairouan = new LatLng(35.6487699,10.0932645);
                marker.position(kairouan).title("Marker in Kairouan");
                googleMap.addMarker(marker);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kairouan,10.0f));

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(final LatLng latLng) {
                        googleMap.clear();
                        marker.position(latLng).title("here");
                        googleMap.addMarker(marker);
                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                        btnAddLocation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                latLocation = latLng.latitude;
                                longLoction = latLng.longitude;
                                MyDynamicToast.informationMessage(AddPostVideoActivity.this, "Location successfully set");
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
        });

        dialog.show();
    }
}
