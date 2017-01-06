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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPostCameraActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final static int CAMERA_RQ = 6969;
    private StorageReference mStorage;
    FirebaseAuth mAuth;
    private ProgressDialog progressDialogGetFile;

    ImageView displayImageCamera;
    EditText addPostInputTextCamera;
    Button btnAddPostCamera,btnChangePhoto;

    Context context;

    double latLocation,longLoction = 0;
    MarkerOptions marker;

    ImageButton btnReturn;
    ImageButton btnAddPostLocation,btnAllowLocation;

    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    boolean myLocationCheck = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post_camera);

        context = this;

        Firebase.setAndroidContext(context);
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        marker = new MarkerOptions();

        displayImageCamera = (ImageView) findViewById(R.id.display_image_take_post_camera);
        addPostInputTextCamera = (EditText) findViewById(R.id.add_post_input_text_camera);
        btnAddPostCamera = (Button) findViewById(R.id.btn_add_post_camera);
        btnChangePhoto = (Button) findViewById(R.id.btn_change_photo);
        btnReturn = (ImageButton)findViewById(R.id.btn_back_from_add_post_camera);
        progressDialogGetFile = new ProgressDialog(context);
        btnAddPostLocation = (ImageButton) findViewById(R.id.btn_add_post_camera_location);
        btnAllowLocation = (ImageButton) findViewById(R.id.btn_allow_my_location_camera);



        btnChangePhoto.setOnClickListener(new View.OnClickListener() {
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

        btnAddPostCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDynamicToast.warningMessage(AddPostCameraActivity.this, "Please add an Image !!");
            }
        });

        startMaterialCamera();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_RQ) {

            if (resultCode == RESULT_OK) {
                //Toast.makeText(context, "Saved to: " + data.getDataString(), Toast.LENGTH_LONG).show();
                final Uri imUri = data.getData();

                displayImageCamera.setImageURI(imUri);
                btnAddPostCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(addPostInputTextCamera.getText().toString().equals("")){
                            MyDynamicToast.warningMessage(AddPostCameraActivity.this, "Write something !!");
                        }else if(imUri == null){
                            MyDynamicToast.warningMessage(AddPostCameraActivity.this, "Please add an Image !!");
                        }else{
                            PostCamera pc = new PostCamera(imUri,addPostInputTextCamera.getText().toString(),latLocation,longLoction);
                            AddPostCameraThread th = new AddPostCameraThread(context);
                            th.execute(pc);
                            startActivity(new Intent(AddPostCameraActivity.this,FeedActivity.class));
                            //uploadImage(imUri);
                        }
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
        //newPostRef.setValue("just an image");
        newPostRef.setValue(new Media(newPostRef.getKey().toString(),"NOTYET"));
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
                        //Toast.makeText(context, "done : " + uri.toString(), Toast.LENGTH_LONG).show();
                        //ADD POST HERE
                        addPost(uri, newImageName);
                        //END ADD POST HERE
                        MyDynamicToast.successMessage(AddPostCameraActivity.this, "Post Added Successfully :)");
                        startActivity(new Intent(AddPostCameraActivity.this,FeedActivity.class));
                        finish();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                MyDynamicToast.errorMessage(AddPostCameraActivity.this, "Something went wrong");
            }
        });
    }

    private void addPost(Uri uriImage, String imgKey){
        String input = addPostInputTextCamera.getText().toString();
        if(input.equals("")){
            //Toast.makeText(context, "Say something !!", Toast.LENGTH_SHORT).show();
            MyDynamicToast.warningMessage(AddPostCameraActivity.this, "Write something !!");
        }else{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM,yyyy");
            final String date = simpleDateFormat.format(new Date());
            DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
            final String time = timeFormat.format(new Date());

            final Firebase mPostsRef = new Firebase("https://aftha7-2a05e.firebaseio.com/posts");
            Post p = new Post(0,addPostInputTextCamera.getText().toString(),uriImage.toString(),"IMAGE",date,time,latLocation,longLoction, mAuth.getCurrentUser().getUid().toString());
            Firebase refPostName = mPostsRef.push();
            refPostName.setValue(p);

           //FirebaseDatabase.getInstance().getReference().child("postImages").child(imgKey).setValue(refPostName.getKey());
            FirebaseDatabase.getInstance().getReference().child("postImages").child(imgKey).child("postId").setValue(refPostName.getKey());
            Log.e("NEW ID//",refPostName.getKey().toString());
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
                    btnAllowLocation.setImageResource(R.mipmap.ic_location_on_black_36dp);
                    myLocationCheck = true;
                    btnAddPostLocation.setVisibility(View.INVISIBLE);
                    displayLocation();
                    MyDynamicToast.informationMessage(AddPostCameraActivity.this, "Location granted");
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    btnAllowLocation.setImageResource(R.mipmap.ic_location_off_black_36dp);
                    latLocation = 0;
                    longLoction = 0;
                    btnAddPostLocation.setVisibility(View.VISIBLE);
                    MyDynamicToast.informationMessage(AddPostCameraActivity.this, "Location denied");
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
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
                    MyDynamicToast.informationMessage(AddPostCameraActivity.this, "Location granted");
                    btnAllowLocation.setImageResource(R.mipmap.ic_location_on_black_36dp);
                    myLocationCheck = true;
                    btnAddPostLocation.setVisibility(View.INVISIBLE);
                    displayLocation();
                }else{
                    //Toast.makeText(context, "not granted", Toast.LENGTH_LONG).show();
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
    public void onBackPressed() {
        startActivity(new Intent(AddPostCameraActivity.this, FeedActivity.class));
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

    void loadDialogSetPostLocation(){
        final Dialog dialog = new Dialog(context,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_set_post_location);


        // set the custom dialog components - text, image and button
        final Button btnAddLocation = (Button) dialog.findViewById(R.id.btn_set_post_location_map);
        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDynamicToast.warningMessage(AddPostCameraActivity.this, "Choose a location");
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
                                MyDynamicToast.informationMessage(AddPostCameraActivity.this, "Location successfully set");
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
