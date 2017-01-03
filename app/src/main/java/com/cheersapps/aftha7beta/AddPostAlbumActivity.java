package com.cheersapps.aftha7beta;

import android.app.Activity;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cheersapps.aftha7beta.adapter.GridAlbumAdapter;
import com.cheersapps.aftha7beta.entity.Media;
import com.cheersapps.aftha7beta.entity.Post;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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
import java.util.ArrayList;
import java.util.Date;

public class AddPostAlbumActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Button btnAddPhots;
    private Button btnSaveImages;
    private EditText postInputAlbum;
    LinearLayout lnrImgs;
    GridView gridView;
    ArrayList<Uri> listDownloadUri;
    ArrayList<Image> listImages;
    ArrayList<String> listNewNames;
    private final int PICK_IMAGE_MULTIPLE =1;

    private StorageReference mStorage;
    FirebaseAuth mAuth;

    Context context;
    double latLocation,longLoction = 0;

    ImageButton btnAddPostLocation,btnAllowLocation;
    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    boolean myLocationCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post_album);

        context = this;
        Firebase.setAndroidContext(context);
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        btnAddPhots = (Button)findViewById(R.id.btnAddPhots);
        btnSaveImages = (Button)findViewById(R.id.btnSaveImages);
        //lnrImgs = (LinearLayout)view.findViewById(R.id.lnrImages);
        gridView = (GridView) findViewById(R.id.grid_view_images_album);
        postInputAlbum = (EditText)findViewById(R.id.add_post_input_text_album);
        listDownloadUri = new ArrayList<Uri>();
        listNewNames = new ArrayList<String>();
        btnAddPhots.setOnClickListener(this);
        btnSaveImages.setOnClickListener(this);

        btnAddPostLocation = (ImageButton) findViewById(R.id.btn_add_post_album_location);
        btnAllowLocation = (ImageButton) findViewById(R.id.btn_allow_my_location_album);

        btnAddPostLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(AddPostAlbumActivity.this,AddPostLocationActivity.class);
                in.putExtra("from",3);
                startActivity(in);
            }
        });

        latLocation = getIntent().getDoubleExtra("lat",0);
        longLoction = getIntent().getDoubleExtra("long",0);

        if(latLocation != 0 && longLoction != 0 ){
            MyDynamicToast.informationMessage(AddPostAlbumActivity.this, "Location successfully set");
        }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddPhots:
                Intent intent = new Intent(AddPostAlbumActivity.this, AlbumSelectActivity.class);
                //set limit on number of images that can be selected, default is 10
                intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 10);
                startActivityForResult(intent, Constants.REQUEST_CODE);
                break;
            case R.id.btnSaveImages:
                if(postInputAlbum.getText().toString().equals("")){
                    MyDynamicToast.warningMessage(AddPostAlbumActivity.this, "Write something !!");
                }else if(listImages == null){
                    MyDynamicToast.warningMessage(AddPostAlbumActivity.this, "Please add at least one image");
                }else{
                    uploadImage(listImages,0);
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Toast.makeText(context, "chosen", Toast.LENGTH_SHORT).show();
            //lnrImgs.removeAllViews();
            //The array list has the image paths of the selected images
            listImages = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            GridAlbumAdapter gridAdapter = new GridAlbumAdapter(context, listImages);
            gridView.setAdapter(gridAdapter);
            for (Image oneImg:listImages) {
                /*ImageView imgV = new ImageView(context);
                imgV.setImageURI(Uri.parse(oneImg.path));
                lnrImgs.addView(imgV);*/
            }

        }
    }

    private void uploadImage(final ArrayList<Image> list, final int position){
        final ProgressDialog progressDialogUploading = new ProgressDialog(context);
        final Firebase mRef = new Firebase("https://aftha7-2a05e.firebaseio.com/");
        Firebase mmRef = mRef.child("postImages");
        Firebase newPostRef = mmRef.push();
        //newPostRef.setValue("just an image");
        newPostRef.setValue(new Media(newPostRef.getKey().toString(),"NOTYET"));
        final String newImageName = newPostRef.getKey();
        listNewNames.add(newImageName);
        progressDialogUploading.setMessage("Uploading...");
        progressDialogUploading.setCanceledOnTouchOutside(false);
        progressDialogUploading.show();
        StorageReference filePath = mStorage.child("postPhotos").child(newImageName);
        File imgFile = new File(list.get(position).path);
        filePath.putFile(Uri.fromFile(imgFile)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialogUploading.dismiss();
                StorageReference pathReference = mStorage.child("postPhotos/"+newImageName);
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Toast.makeText(context, "done : " + uri.toString(), Toast.LENGTH_LONG).show();
                        listDownloadUri.add(uri);
                        if(position < list.size()-1){
                            uploadImage(list,position+1);
                        }else{
                            //Toast.makeText(context, "All Done : ", Toast.LENGTH_LONG).show();

                            //ADD POST HERE
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM,yyyy");
                            final String date = simpleDateFormat.format(new Date());
                            DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
                            final String time = timeFormat.format(new Date());

                            final Firebase mPostsRef = new Firebase("https://aftha7-2a05e.firebaseio.com/posts");
                            Post p = new Post(0,postInputAlbum.getText().toString(),"ALBUM","ALBUM",date,time,latLocation,longLoction, mAuth.getCurrentUser().getUid().toString());
                            Firebase refPostName = mPostsRef.push();
                            refPostName.setValue(p);
                            Log.e("NEW ID//",refPostName.getKey().toString());
                            String postId = refPostName.getKey().toString();
                            //END ADD POST HERE

                            //ADD MEDIA HERE
                            for (Uri oneUri:listDownloadUri) {
                                final Firebase mediaRef = new Firebase("https://aftha7-2a05e.firebaseio.com/media");
                                Media media = new Media(oneUri.toString(),postId);
                                mediaRef.push().setValue(media);
                            }
                            //END ADD MEDIA HERE
                            for(String oneName:listNewNames){
                                FirebaseDatabase.getInstance().getReference().child("postImages").child(oneName).child("postId").setValue(postId);
                            }
                            MyDynamicToast.successMessage(AddPostAlbumActivity.this, "Post Added Successfully :)");
                            startActivity(new Intent(AddPostAlbumActivity.this, FeedActivity.class));
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                MyDynamicToast.errorMessage(AddPostAlbumActivity.this, "Something went wrong");
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
                    MyDynamicToast.informationMessage(AddPostAlbumActivity.this, "Location granted");
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    btnAllowLocation.setImageResource(R.mipmap.ic_location_off_black_36dp);
                    latLocation = 0;
                    longLoction = 0;
                    btnAddPostLocation.setVisibility(View.VISIBLE);
                    MyDynamicToast.informationMessage(AddPostAlbumActivity.this, "Location denied");
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
                    MyDynamicToast.informationMessage(AddPostAlbumActivity.this, "Location granted");
                    btnAllowLocation.setImageResource(R.mipmap.ic_location_on_black_36dp);
                    myLocationCheck = true;
                    btnAddPostLocation.setVisibility(View.INVISIBLE);
                    displayLocation();
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
