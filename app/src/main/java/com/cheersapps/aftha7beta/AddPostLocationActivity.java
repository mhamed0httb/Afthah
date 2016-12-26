package com.cheersapps.aftha7beta;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddPostLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    MarkerOptions marker;
    Button btnAddLocation;
    double latitude,longitude = 0;
    int from = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        marker = new MarkerOptions();
        btnAddLocation = (Button) findViewById(R.id.btn_add_location);

        from = getIntent().getIntExtra("from",0);

        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(from == 1){
                    Intent in = new Intent(AddPostLocationActivity.this,AddPostTextActivity.class);
                    in.putExtra("lat",latitude);
                    in.putExtra("long",longitude);
                    startActivity(in);
                }else if(from == 2){
                    Intent in = new Intent(AddPostLocationActivity.this,AddPostCameraActivity.class);
                    in.putExtra("lat",latitude);
                    in.putExtra("long",longitude);
                    startActivity(in);
                }else if(from == 3){
                    Intent in = new Intent(AddPostLocationActivity.this,AddPostAlbumActivity.class);
                    in.putExtra("lat",latitude);
                    in.putExtra("long",longitude);
                    startActivity(in);
                }else if(from == 4){
                    Intent in = new Intent(AddPostLocationActivity.this,AddPostVideoActivity.class);
                    in.putExtra("lat",latitude);
                    in.putExtra("long",longitude);
                    startActivity(in);
                }else{
                    startActivity(new Intent(AddPostLocationActivity.this,FeedActivity.class));
                }

            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng kairouan = new LatLng(35.6487699,10.0932645);
        marker.position(kairouan).title("Marker in Kairouan");
        mMap.addMarker(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kairouan,10.0f));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                mMap.clear();
                marker.position(latLng).title("here");
                mMap.addMarker(marker);
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(AddPostLocationActivity.this,AddPostTextActivity.class);
        in.putExtra("lat",0);
        in.putExtra("long",0);
        startActivity(in);

    }
}
