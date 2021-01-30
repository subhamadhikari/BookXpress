package com.example.bookxpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    Button toLogin,signUp;
    EditText usernameIN,emailIn,passIn ;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        progressBar = findViewById(R.id.progressBar2);

        usernameIN = findViewById(R.id.createUserName);
        emailIn = findViewById(R.id.createEmail);
        passIn = findViewById(R.id.createPassword);

        toLogin = findViewById(R.id.toLogActBtn);
        signUp = findViewById(R.id.signUpBtn);

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
//                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        firebaseFirestore =FirebaseFirestore.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name = usernameIN.getText().toString();
                final String email = emailIn.getText().toString();
                String pass = passIn.getText().toString();

                if (name.isEmpty() || email.isEmpty() || pass.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "No Empty field allowed!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pass.length() < 7){
                    Toast.makeText(RegisterActivity.this, "Password is short", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            String id = firebaseAuth.getUid();

                            Users user = new Users(name,email,id);

                            assert id != null;
                            firebaseFirestore.collection("Users").document(id).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                        finish();
                                    }else {
                                        Toast.makeText(RegisterActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            progressBar.setVisibility(View.INVISIBLE);

                            Toast.makeText(RegisterActivity.this, "User created successfully!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(RegisterActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
    }

}