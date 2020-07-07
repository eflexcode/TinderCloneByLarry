package com.example.tinderclonetest1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tinderclonetest1.adapter.MessagingAdapter;
import com.example.tinderclonetest1.placeholder.CardStackViewPlaceHolder;
import com.example.tinderclonetest1.placeholder.ChatsList;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    Toolbar toolbar;
    Intent intent;

    CircleImageView profilePic;
    TextView chatUsername;
    EditText sendEditText;
    ImageButton sendButton;
    ImageView sendImage;
    RecyclerView chatRecyclerView;
    MessagingAdapter adapter;

    StorageReference cameraReference;
    StorageReference galleryReference;
    UploadTask imageTask;
    Uri uri;

    DatabaseReference sendReference;
    FirebaseUser firebaseUser;

    List<ChatsList> chatsLists = new ArrayList<>();

    String getId;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        intent = getIntent();
        getId = intent.getStringExtra("id");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        profilePic = findViewById(R.id.profile_pic);
        chatUsername = findViewById(R.id.username);
        sendEditText = findViewById(R.id.send_edit_text);
        sendButton = findViewById(R.id.send_button);
        sendImage = findViewById(R.id.sendImageSide);
        chatRecyclerView = findViewById(R.id.message_recycler);
        chatRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);

        // update isseen to true in on create
        DatabaseReference updateIsSeen = FirebaseDatabase.getInstance().getReference("Chats");
        updateIsSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    ChatsList chats = snapshot.getValue(ChatsList.class);
                    assert chats != null;
                    if (chats.getSenderId().equals(getId) && chats.getReceiverId().equals(firebaseUser.getUid())) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("isseen", true);

                        snapshot.getRef().updateChildren(map);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // firebase paths for upload images
        cameraReference = FirebaseStorage.getInstance().getReference("cameraUpload");
        galleryReference = FirebaseStorage.getInstance().getReference("galleryUpload");

        //change send button color if sendEditText is empty or not
        sendEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String getS = s.toString();
                if (getS.isEmpty()){
                    sendButton.setImageResource(R.drawable.ic_send_hash);
                }else {
                    sendButton.setImageResource(R.drawable.ic_send_blue);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        //open the details of the user you are chatting with
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this, OtherUserDetailsActivity.class);
                intent.putExtra("id",getId);
                startActivity(intent);
            }
        });

        // set user you are chatting with details to toolbar
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(getId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                CardStackViewPlaceHolder placeHolder = dataSnapshot.getValue(CardStackViewPlaceHolder.class);
                assert placeHolder != null;
                imageUrl = placeHolder.getFirstImageUrl();
                if (placeHolder.getFirstImageUrl().equals("default")){
                    profilePic.setImageResource(R.drawable.no_p);
                }else {
                    //load image from the internet
                    Glide.with(MessageActivity.this).load(placeHolder.getFirstImageUrl()).into(profilePic);
                }

                chatUsername.setText(placeHolder.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getMessage(getId);

    }

    //get all message which i have received or sent in line 216 and add to list
    private void getMessage(final String userId) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatsLists.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatsList chatsList = snapshot.getValue(ChatsList.class);

                    assert chatsList != null;
                    assert firebaseUser != null;
                    if (chatsList.getSenderId().equals(firebaseUser.getUid()) && chatsList.getReceiverId().equals(userId) ||
                            chatsList.getReceiverId().equals(firebaseUser.getUid()) && chatsList.getSenderId().equals(userId)){
                       chatsLists.add(chatsList);
                    }
                }

                //send the list to adapter
                adapter = new MessagingAdapter(chatsLists,MessageActivity.this,imageUrl);
                chatRecyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //close activity
    public void finish(View view) {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.security, menu);

        return true;

    }

    //when send button is clicked int the ui this method is called and it write to firebase
    public void sendMessageToFirebase(View view) {

        String getMessage = sendEditText.getText().toString();
        if (getMessage.isEmpty()){
            Toast.makeText(this, "cannot send an empty message", Toast.LENGTH_SHORT).show();
            return;
        }

        sendReference = FirebaseDatabase.getInstance().getReference("Chats");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String senderId  = firebaseUser.getUid();
        String receiverId = getId;

        Map<String, Object> chatMap = new HashMap<>();
        chatMap.put("senderId", senderId);
        chatMap.put("receiverId", receiverId);
        chatMap.put("message", getMessage);
        chatMap.put("imageUrl","default");
        chatMap.put("isseen",false);

        sendReference.push().setValue(chatMap);
        sendEditText.setText("");
    }

    //get selected image from gallery details for firebase
    public String uploadDeatiles(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //when the blue image sign button is clicked in the ui this method is called
    public void sendImage(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Upload")
                .setMessage("continue with")
                .setPositiveButton("camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //opens camera
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 2);

                        dialog.dismiss();
                    }
                }).setNegativeButton("file upload", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //opens gallery
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, 1);
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //
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

                        sendReference = FirebaseDatabase.getInstance().getReference("Chats");
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String senderId  = firebaseUser.getUid();
                        String receiverId = getId;

                        Map<String, Object> chatMap = new HashMap<>();
                        chatMap.put("senderId", senderId);
                        chatMap.put("receiverId", receiverId);
                        chatMap.put("message", "image");
                        chatMap.put("imageUrl",stringUri);
                        chatMap.put("isseen",false);

                        sendReference.push().setValue(chatMap);

                        Toast.makeText(MessageActivity.this, "image upload successful", Toast.LENGTH_SHORT).show();


                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

                    sendReference = FirebaseDatabase.getInstance().getReference("Chats");
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    String senderId  = firebaseUser.getUid();
                    String receiverId = getId;

                    Map<String, Object> chatMap = new HashMap<>();
                    chatMap.put("senderId", senderId);
                    chatMap.put("receiverId", receiverId);
                    chatMap.put("message", "image");
                    chatMap.put("imageUrl",stringUri);
                    chatMap.put("isseen",false);

                    sendReference.push().setValue(chatMap);


                    Toast.makeText(MessageActivity.this, "image upload successful", Toast.LENGTH_SHORT).show();


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
