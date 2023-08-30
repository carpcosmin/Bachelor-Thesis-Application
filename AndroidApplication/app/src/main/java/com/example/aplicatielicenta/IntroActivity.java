package com.example.aplicatielicenta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class IntroActivity extends AppCompatActivity {

    Button btnLogIn;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        initializeControls();

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logInIntent = new Intent(IntroActivity.this, LogInActivity.class);
                startActivity(logInIntent);
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(IntroActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
                finish();
            }
        });
    }



    public void initializeControls(){
        btnLogIn=findViewById(R.id.btnLogIn);
        btnSignUp = findViewById(R.id.btnSignUp);
    }
}