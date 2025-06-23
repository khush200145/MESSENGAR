package com.example.messengar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatWin extends AppCompatActivity {
    String receiverimg;
    String receiverUid,receiverName,SenderUid;
    CircleImageView profile;
    TextView receiverNName;
    CardView sendbtn;
    EditText textmsg;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    public static  String senderImg;
    public static  String receiverIImg;
    String senderRoom,receiverRoom;
        RecyclerView mmessangesAdpter;
        ArrayList<msgModelclass>messagessArrayList;
        messagesAdpter messagesAdpter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_win);
        database = FirebaseDatabase.getInstance(); // ✅ Must be initialized
        firebaseAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getSupportActionBar().hide();
        receiverName=getIntent().getStringExtra("nameeee");
        receiverimg=getIntent().getStringExtra("receiverImg");
        receiverUid=getIntent().getStringExtra("uid");
        SenderUid= firebaseAuth.getUid();
        senderRoom=SenderUid+receiverUid;
        receiverRoom=receiverUid+SenderUid;
        messagessArrayList=new ArrayList<>();


        mmessangesAdpter=findViewById(R.id.msgadpter);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmessangesAdpter.setLayoutManager(linearLayoutManager);
        messagesAdpter=new messagesAdpter(chatWin.this,messagessArrayList);
        mmessangesAdpter.setAdapter(messagesAdpter);





        sendbtn=findViewById(R.id.sendbtnn);
        textmsg=findViewById(R.id.textmsg);


        profile=findViewById(R.id.profileimgg);
        receiverNName=findViewById(R.id.receivername);
        Picasso.get().load(receiverimg).into(profile);

        receiverNName.setText(""+ receiverName);
        DatabaseReference reference=database.getReference().child("User").child(firebaseAuth.getUid());
        DatabaseReference chatreference=database.getReference().child("chats").child(senderRoom).child("messages");

        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagessArrayList.clear();   //This will ensure that we donot repeat the messages each time we refresh
                for(DataSnapshot datasnapshot:snapshot.getChildren()){
                    msgModelclass messages=datasnapshot.getValue(msgModelclass.class);
                    messagessArrayList.add(messages);
                }
                 messagesAdpter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg = snapshot.child("profilepic").getValue().toString();
                receiverIImg=receiverimg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


sendbtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        String message=textmsg.getText().toString();
        if(message.isEmpty()){
            Toast.makeText(chatWin.this, "Enter the message first", Toast.LENGTH_SHORT).show();
        }

       textmsg.setText("");
        Date date=new Date();
        msgModelclass messagess=new msgModelclass(message,SenderUid,date.getTime());
    database=FirebaseDatabase.getInstance();
    database.getReference().child("chats").child(senderRoom).child("messages").push().setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
       database.getReference().child("chats").child(receiverRoom).child("messages").push().setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {

           }
       })  ;
        }
    });
    }
});

    }
}