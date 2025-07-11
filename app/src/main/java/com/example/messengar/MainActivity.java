package com.example.messengar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    RecyclerView mainUserRecyclerView;
UserAdpter adapter;
FirebaseDatabase database;
ArrayList<Users> usersArrayList;
ImageView imglogout;
ImageView cumbut,setbut;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        cumbut=findViewById(R.id.cambut);
        setbut=findViewById(R.id.settingBut);



        DatabaseReference reference=database.getReference().child("User");
         usersArrayList=new ArrayList<>();  //Allocating memory of user arraylist
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){    //Dta of the user will start getting  store here
                    Users users=dataSnapshot.getValue(Users.class);
                    users.setUserId(dataSnapshot.getKey());
                    usersArrayList.add(users);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
         imglogout=findViewById(R.id.logoutimg);

         imglogout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Dialog dialog=new Dialog(MainActivity.this,R.style.dialoge);
                 dialog.setContentView(R.layout.dialog_layout);
                 Button no,yes;
                 yes=dialog.findViewById(R.id.yesbutton);
                 no=dialog.findViewById(R.id.nobutton);
                 yes.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         FirebaseAuth.getInstance().signOut();
                         Intent intent =new Intent(MainActivity.this, login.class);
                         startActivity(intent);
                           finish();
                     }
                 });
                 no.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         dialog.dismiss();
                     }
                 });
                 dialog.show();

             }
         });
        mainUserRecyclerView=findViewById(R.id.mainUserRecyclerView);
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdpter(MainActivity.this, usersArrayList);
        mainUserRecyclerView.setAdapter(adapter);
        // Creating adapter
        setbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,setting.class);
                startActivity(intent);
            }
        });

        cumbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,10);
            }
        });



        if(auth.getCurrentUser()==null){
            Intent intent =new Intent(MainActivity.this,login.class);
            startActivity(intent);
        }
    }
}