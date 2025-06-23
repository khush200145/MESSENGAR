package com.example.messengar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    TextView logsignup;
    Button button;
    EditText email,password;
    FirebaseAuth auth;
    String emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    android.app.ProgressDialog progressDialog;




    //delete this if you dont get any error
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..."); //after users inputs his login credentials it will not directly take him inside the app but it will show a progressdialog
        progressDialog.setCancelable(false);        //if we set it true then there will come a cancel button which user could use to cancel the progressdialog but we donot want that
        getSupportActionBar().hide();
        auth=FirebaseAuth.getInstance();
        button=findViewById(R.id.logbutton);
        email=findViewById(R.id.editTextLogEmail);
        password=findViewById(R.id.editTextLogPassword);
        logsignup=findViewById(R.id.logsignup);

        logsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(login.this, registration.class);
                startActivity(intent);
                finish();
            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email=email.getText().toString();
                String pass=password.getText().toString();

                if((TextUtils.isEmpty(Email))){
                    progressDialog.dismiss(); //we dont want to show progressdialog to come after checking email
                    Toast.makeText(login.this,"Enter The Email",Toast.LENGTH_SHORT).show();
                }else if((TextUtils.isEmpty(pass))){
                    progressDialog.dismiss();
                    Toast.makeText(login.this,"Enter The Password",Toast.LENGTH_SHORT).show();
                }else if(!Email.matches(emailPattern)){
                    progressDialog.dismiss();
                    email.setError("Give Proper Email Address");
                }else if(password.length()<6){
                    progressDialog.dismiss();
                    password.setError("More Than Six Characters");
                    Toast.makeText(login.this,"Password needs to be longer than six characters",Toast.LENGTH_SHORT).show();
                }else{
                    auth.signInWithEmailAndPassword(Email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                if (!login.this.isFinishing() && !login.this.isDestroyed()) {
                                    progressDialog.show();
                                }

                                try{
                                    Intent intent=new Intent(login.this,MainActivity.class);
                                    progressDialog.dismiss();
                                    startActivity(intent);
                                    finish();        //if we dont use this then if the user presses back button then app will not close and it will go back to login screen
                                }catch(Exception e){
                                    Toast.makeText(login.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                                }
                            }else{
                                Toast.makeText(login.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();;
                            }
                        }
                    });

                }


            }
        });
    }
}