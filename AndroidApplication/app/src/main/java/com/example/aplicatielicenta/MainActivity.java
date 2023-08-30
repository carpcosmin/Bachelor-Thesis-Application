package com.example.aplicatielicenta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aplicatielicenta.entities.User;
import com.example.aplicatielicenta.retrofit.ApiService;
import com.example.aplicatielicenta.retrofit.ProfilePictureAPI;
import com.example.aplicatielicenta.retrofit.RetrofitService;
import com.example.aplicatielicenta.retrofit.UserAPI;
import com.example.aplicatielicenta.ui.home.HomeFragment;
import com.example.aplicatielicenta.ui.profile.ProfileFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicatielicenta.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        // check if the server is available
        RetrofitService retrofitService = new RetrofitService();
        ApiService apiService = retrofitService.getRetrofit().create(ApiService.class);
        apiService.pingServer().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Request failed, display a Toast and redirect to IntroActivity
                Toast.makeText(MainActivity.this, "Server is not responding", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, IntroActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Drawer controls
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_favorites, R.id.nav_my_offers)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View headerView = navigationView.getHeaderView(0);
        TextView email = headerView.findViewById(R.id.nav_email);
        TextView signOut = headerView.findViewById(R.id.tv_signOut);
        CircleImageView profilePicture = headerView.findViewById(R.id.profile_picture);

        //getting the details of the User which is logged in
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token != null) {
            UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
            userAPI.getUserByToken(token).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    user = response.body();
                    if(user != null){
                        email.setText(user.getEmail());
                        // get the current profile picture
                        ProfilePictureAPI profilePictureAPI = retrofitService.getRetrofit().create(ProfilePictureAPI.class);
                        profilePictureAPI.getProfilePicture(user.getId()).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    try {
                                        // Convert response body to byte array
                                        byte[] imageBytes = response.body().bytes();

                                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                                        profilePicture.setImageBitmap(bitmap);

                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                } else {
                                    Log.e("API Error", "Failed to fetch images: " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
        } else {
            startActivity(new Intent(MainActivity.this, IntroActivity.class));
        }


        // handling the sign out of the User
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("token");
                editor.apply();

                startActivity(new Intent(MainActivity.this, IntroActivity.class));
                Toast.makeText(MainActivity.this, "Signed Out",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        //End of Drawer controls

        // setting the click method for the add button
        binding.appBarMain.btnAddOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addOfferIntent = new Intent(MainActivity.this, AddOfferActivity.class);
                addOfferIntent.putExtra("user", user);
                startActivity(addOfferIntent);
            }
        });
    }

    //get the current user data to use it in other Fragments
    public User getUserData(){
        User currentUser = new User();
        if(user != null)
        {
            currentUser.setId(user.getId());
            currentUser.setFirstName(user.getFirstName());
            currentUser.setLastName(user.getLastName());
            currentUser.setEmail(user.getEmail());
            currentUser.setPhoneNumber(user.getPhoneNumber());
            currentUser.setPassword(user.getPassword());
        }
        return currentUser;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}