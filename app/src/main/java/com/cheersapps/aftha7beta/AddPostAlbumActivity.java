package com.cheersapps.aftha7beta;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cheersapps.aftha7beta.adapter.GridAlbumAdapter;
import com.cheersapps.aftha7beta.entity.Media;
import com.cheersapps.aftha7beta.entity.Post;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddPostAlbumActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnAddPhots;
    private Button btnSaveImages;
    private EditText postInputAlbum;
    LinearLayout lnrImgs;
    GridView gridView;
    ArrayList<Uri> listDownloadUri;
    ArrayList<Image> listImages;
    private final int PICK_IMAGE_MULTIPLE =1;

    private StorageReference mStorage;
    FirebaseAuth mAuth;

    Context context;
    double latLocation,longLoction = 0;

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
        btnAddPhots.setOnClickListener(this);
        btnSaveImages.setOnClickListener(this);

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
                uploadImage(listImages,0);
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
        newPostRef.setValue("just an image");
        final String newImageName = newPostRef.getKey();
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
                        Toast.makeText(context, "done : " + uri.toString(), Toast.LENGTH_LONG).show();
                        listDownloadUri.add(uri);
                        if(position < list.size()-1){
                            uploadImage(list,position+1);
                        }else{
                            Toast.makeText(context, "All Done : ", Toast.LENGTH_LONG).show();

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
                            startActivity(new Intent(AddPostAlbumActivity.this, FeedActivity.class));
                        }
                    }
                });
            }
        });
    }
}
