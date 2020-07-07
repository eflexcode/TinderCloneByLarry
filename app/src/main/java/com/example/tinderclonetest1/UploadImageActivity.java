package com.example.tinderclonetest1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class UploadImageActivity extends AppCompatActivity {

    Toolbar toolbar;
    StorageReference cameraReference;
    StorageReference galleryReference;
    UploadTask imageTask;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // firebase paths for upload images
        cameraReference = FirebaseStorage.getInstance().getReference("cameraUpload");
        galleryReference = FirebaseStorage.getInstance().getReference("galleryUpload");
        setTitle("");

    }

    public void finish(View view) {
        finish();
    }

    public void openCamera(View view) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 2);

    }

    public void openGallery(View view) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);

    }


    public String uploadDeatiles(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    public void doUpload() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("uploading");
        progressDialog.show();

        if (uri != null) {
            final StorageReference reference = galleryReference.child(uploadDeatiles(uri) + "." + System.currentTimeMillis());
            imageTask = reference.putFile(uri);
            imageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();

                        Uri uri1 = task.getResult();
                        String stringUri = uri1.toString();

                        FirebaseUser firebaseAuth;
                        DatabaseReference databaseReference;

                        firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();
                        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid());

                        Map<String, Object> map = new HashMap<>();
                        map.put("firstImageUrl", stringUri);
                        databaseReference.updateChildren(map);

                        Toast.makeText(UploadImageActivity.this, "image upload successful", Toast.LENGTH_SHORT).show();


                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Uri is null", Toast.LENGTH_SHORT).show();
        }

    }

    public void doUploadByte(byte[] mbyte) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("uploading");
        progressDialog.show();


        final StorageReference reference = galleryReference.child("" + System.currentTimeMillis());
        imageTask = reference.putBytes(mbyte);
        imageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();

                    Uri uri1 = task.getResult();
                    String stringUri = uri1.toString();

                    FirebaseUser firebaseAuth;
                    DatabaseReference databaseReference;

                    firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();
                    databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid());

                    Map<String, Object> map = new HashMap<>();
                    map.put("firstImageUrl", stringUri);
                    databaseReference.updateChildren(map);

                    Toast.makeText(UploadImageActivity.this, "image upload successful", Toast.LENGTH_SHORT).show();


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //check if the app returns from gallery
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            doUpload();

        }

        //check if the app returns from camera

        if (requestCode == 2 && resultCode == RESULT_OK) {

            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();

            doUploadByte(bytes);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String fUser = firebaseUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(fUser);

        Map<String, Object> map = new HashMap<>();
        map.put("isOnline", "online");

        databaseReference.updateChildren(map);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String fUser = firebaseUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(fUser);

        Map<String, Object> map = new HashMap<>();
        map.put("isOnline", "offline");

        databaseReference.updateChildren(map);
    }
}
