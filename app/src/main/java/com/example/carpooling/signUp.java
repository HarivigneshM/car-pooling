package com.example.carpooling;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class signUp extends AppCompatActivity {

    EditText SignUpName,SignUpEmail,SignUpPassword,SignUpMobile;
    String uid;
    Button signUp;
    FirebaseAuth fAuth2;
    FirebaseFirestore fStore2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SignUpName=findViewById(R.id.SignUpName);
        SignUpEmail=findViewById(R.id.SignUpEmail);
        SignUpMobile=findViewById(R.id.SignUpMobile);
        SignUpPassword=findViewById(R.id.SignUpPassword);
        TextView toLogin=findViewById(R.id.sign_click_here);
        fAuth2=FirebaseAuth.getInstance();
        fStore2=FirebaseFirestore.getInstance();
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        signUp=findViewById(R.id.signUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=SignUpEmail.getText().toString().trim();
                String password=SignUpPassword.getText().toString().trim();
                String mobile=SignUpMobile.getText().toString().trim();
                String name=SignUpName.getText().toString().trim();
                fAuth2.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isComplete()){
                            uid= Objects.requireNonNull(fAuth2.getCurrentUser()).getUid();
                            DocumentReference documentReference=fStore2.collection("users").document(uid);
                            Map<String,Object> user= new HashMap<>();
                            user.put("email",email);
                            user.put("name",name);
                            user.put("mobile",mobile);
                            documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(signUp.this, "Sign Up Successsfull", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(getApplicationContext(), landingPage.class);
                                    startActivity(intent);
                                }
                            });
                        }
                        else{
                            Toast.makeText(signUp.this, "Something happenend", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}