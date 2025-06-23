package com.example.messengar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;

public class setting extends AppCompatActivity {
    ImageView setprofile;
    EditText setname,setstatus;
    Button donebut;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    String email,password;
    Uri setImageUri;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        setprofile=findViewById(R.id.settingprofile);
        setname=findViewById(R.id.settingname);
        setstatus=findViewById(R.id.settingstatus);
        donebut=findViewById(R.id.donebut);

        DatabaseReference reference=database.getReference().child("User").child(auth.getUid());
        StorageReference storageReference=storage.getReference().child("Upload").child(auth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                email=snapshot.child("mail").getValue().toString();
                password=snapshot.child("password").getValue().toString();
                String name=snapshot.child("userName").getValue().toString();
                String profile=snapshot.child("profilepic").getValue().toString();
                String status=snapshot.child("status").getValue().toString();
                setname.setText(name);
                setstatus.setText(status);
                Picasso.get().load(profile).into(setprofile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

setprofile.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);
    }
});
        donebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = setname.getText().toString();
                String status = setstatus.getText().toString();
                StorageReference storageReference = storage.getReference().child("Upload").child(auth.getUid()); // 🔁 fixed path

                if (setImageUri != null) {
                    // Upload new image
                    storageReference.putFile(setImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String finalImageUri = uri.toString();
                                        updateUserProfile(finalImageUri, name, status);
                                    }
                                });
                            } else {
                                Toast.makeText(setting.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    // Use existing image
                    database.getReference().child("User").child(auth.getUid()).child("profilepic").get().addOnSuccessListener(snapshot -> {
                        String existingUri = snapshot.exists() ? snapshot.getValue().toString() :
                                "https://firebasestorage.googleapis.com/v0/b/messengar-d59ea.firebasestorage.app/o/man.png?alt=media&token=96009321-4ad8-4a57-8f98-783f8ea03517";

                        updateUserProfile(existingUri, name, status);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(setting.this, "Couldn't fetch existing profile picture", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            private void updateUserProfile(String imageUrl, String name, String status) {
                Users users = new Users(auth.getUid(), name, email, password, imageUrl, status);
                database.getReference().child("User").child(auth.getUid())
                        .setValue(users)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(setting.this, "Data is saved", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(setting.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(setting.this, "Error occurred", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10){
            if(data!=null){
                setImageUri=data.getData();
                setprofile.setImageURI(setImageUri);
            }
        }else{

        }

    }
}