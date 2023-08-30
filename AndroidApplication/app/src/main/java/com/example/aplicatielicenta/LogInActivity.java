package com.example.aplicatielicenta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aplicatielicenta.entities.User;
import com.example.aplicatielicenta.retrofit.RetrofitService;
import com.example.aplicatielicenta.retrofit.UserAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogInActivity extends AppCompatActivity {

    EditText editTextEmail;
    EditText editTextPassword;
    Button btnLogIn;
    ProgressBar progressBar;
    TextView tvSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        initializeControls();

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                logInUser();
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });
    }

    private void logInUser() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Email field is empty!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            editTextEmail.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Password field is empty!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            editTextPassword.requestFocus();
            return;
        }

        RetrofitService retrofitService = new RetrofitService();
        UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
        LogInRequest logInRequest = new LogInRequest(email, password);
        Call<User> call = userAPI.login(logInRequest);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    User user = response.body();

                    // saving the token in Shared Preferences
                    SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", user.getToken());
                    editor.apply();

                    //sending the user to the main activity after logging in
                    Intent logInIntent = new Intent(LogInActivity.this, MainActivity.class);
                    startActivity(logInIntent);
                    finish();

                    Toast.makeText(LogInActivity.this, "Log in successful!" ,Toast.LENGTH_SHORT).show();

                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LogInActivity.this, "Incorrect email or password" ,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LogInActivity.this, "Log in failed" ,Toast.LENGTH_SHORT).show();
            }


        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
    }

    private void initializeControls() {
        editTextEmail = findViewById(R.id.etLogInEmail);
        editTextPassword = findViewById(R.id.etLogInpassword);
        btnLogIn = findViewById(R.id.btnLogIn);
        tvSignUp = findViewById(R.id.tv_signup);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

    }
}