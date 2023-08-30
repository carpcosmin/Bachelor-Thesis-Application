package com.example.aplicatielicenta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    EditText editTextFirstName;
    EditText editTextLastName;
    EditText editTextEmail;
    EditText editTextPhone;
    EditText editTextPassword;
    EditText editTextConfirmPassword;
    Button btnSignUp;
    ProgressBar progressBar;
    TextView tvLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initializeControls();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                createUser();
            }
        });

        tvLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logInIntent = new Intent(SignUpActivity.this, LogInActivity.class);
                startActivity(logInIntent);
            }
        });
    }

    private void createUser() {
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String email = editTextEmail.getText().toString();
        String phoneNumber = editTextPhone.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        if(TextUtils.isEmpty(firstName)){
            Toast.makeText(this, "First name field is empty!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            editTextFirstName.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(lastName)){
            Toast.makeText(this, "Last name field is empty!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            editTextLastName.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Email field is empty!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            editTextEmail.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(this, "Phone number is empty!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            editTextPhone.requestFocus();
            return;
        }

        if(phoneNumber.length() < 10){
            Toast.makeText(this, "Phone number too short!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            editTextPhone.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Password field is empty!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            editTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6 ){
            Toast.makeText(this, "Password must be at least 6 characters long!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            editTextPassword.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(this, "Password must be confirmed!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            editTextConfirmPassword.requestFocus();
            return;
        }
        if(!password.equals(confirmPassword)){
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            editTextConfirmPassword.requestFocus();
            return;
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(password);

        RetrofitService retrofitService = new RetrofitService();
        UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
        userAPI.saveUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SignUpActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
                finish();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SignUpActivity.this, "Error: Could not create account!" ,Toast.LENGTH_SHORT).show();
                Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, "Error occured when creating new user", t);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
    }

    private void initializeControls() {
        editTextFirstName = findViewById(R.id.etFirstName);
        editTextLastName = findViewById(R.id.etLastName);
        editTextEmail = findViewById(R.id.etSignUpEmail);
        editTextPhone = findViewById(R.id.etSignUpPhoneNumber);
        editTextPassword = findViewById(R.id.etPassword);
        editTextConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnConfirm);
        tvLogIn = findViewById(R.id.tv_login);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

    }

}