package com.example.aplicatielicenta.ui.profile;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.aplicatielicenta.AddOfferActivity;
import com.example.aplicatielicenta.IntroActivity;
import com.example.aplicatielicenta.MainActivity;
import com.example.aplicatielicenta.R;
import com.example.aplicatielicenta.ViewOfferActivity;
import com.example.aplicatielicenta.entities.User;
import com.example.aplicatielicenta.retrofit.ImageAPI;
import com.example.aplicatielicenta.retrofit.ProfilePictureAPI;
import com.example.aplicatielicenta.retrofit.RetrofitService;
import com.example.aplicatielicenta.retrofit.UserAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final int READ_PERMISSION = 101;
    ArrayList<Uri> uriImagesArray = new ArrayList<>();
    CircleImageView profilePicture;
    TextView changeProfilePicture;
    Button btnDeleteImage;
    Button btnSaveChanges;
    Button btnDeleteAccount;
    EditText newFirstName, newLastName, newEmail, newPhoneNumber, newPassword, confirmNewPassword;
    boolean imageLoaded = false;


    // Create an ActivityResultLauncher to handle the result of the image selection
    private ActivityResultLauncher<Intent> imageSelectionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == MainActivity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    uriImagesArray.add(imageUri);
                    profilePicture.setImageURI(imageUri);
                }
            }
    );

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);


        profilePicture = root.findViewById(R.id.profile_picture);
        changeProfilePicture = root.findViewById(R.id.change_profile_picture);
        btnSaveChanges = root.findViewById(R.id.btnSave);
        newFirstName = root.findViewById(R.id.etChangeFirstName);
        newLastName = root.findViewById(R.id.etChangeLastName);
        newEmail = root.findViewById(R.id.etChangeEmail);
        newPhoneNumber = root.findViewById(R.id.etChangePhoneNumber);
        newPassword = root.findViewById(R.id.etChangePassword);
        confirmNewPassword = root.findViewById(R.id.etConfirmNewPassword);
        btnDeleteAccount = root.findViewById(R.id.btnDeleteAccount);
        btnDeleteImage = root.findViewById(R.id.btn_delete_profilePicture);

        //getting the data of the logged in user
        MainActivity mainActivity =(MainActivity) getActivity();
        User currentUser = mainActivity.getUserData();
        if(currentUser != null){
            newFirstName.setText(currentUser.getFirstName());
            newLastName.setText(currentUser.getLastName());
            newEmail.setText(currentUser.getEmail());
            newPhoneNumber.setText(currentUser.getPhoneNumber());
        }


        RetrofitService retrofitService = new RetrofitService();
        ProfilePictureAPI profilePictureAPI = retrofitService.getRetrofit().create(ProfilePictureAPI.class);
        // get the current profile picture
        profilePictureAPI.getProfilePicture(currentUser.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // Convert response body to byte array
                        byte[] imageBytes = response.body().bytes();

                        if (imageBytes.length > 0) {
                            // Image is loaded
                            imageLoaded = true;
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                            profilePicture.setImageBitmap(bitmap);
                        } else {
                            // Image is not loaded
                            imageLoaded = false;
                            profilePicture.setImageResource(R.drawable.default_profile_picture);
                        }

                        // Update the visibility of the delete button
                        if (imageLoaded) {
                            btnDeleteImage.setVisibility(View.VISIBLE);
                        } else {
                            btnDeleteImage.setVisibility(View.GONE);
                        }
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

        changeProfilePicture.setOnClickListener(v -> {

            if(ContextCompat.checkSelfPermission(ProfileFragment.super.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(ProfileFragment.super.getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION);
            }
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setPackage("com.google.android.apps.photos");
            imageSelectionLauncher.launch(Intent.createChooser(intent, "Select Image"));

            // Update the visibility of the delete button after selecting an image
            if (profilePicture.getDrawable() == null) {
                // No image loaded, hide the delete button
                btnDeleteImage.setVisibility(View.GONE);
            } else {
                // Image loaded, show the delete button
                btnDeleteImage.setVisibility(View.VISIBLE);
            }
        });

        btnSaveChanges.setOnClickListener(v -> {
            String firstName = newFirstName.getText().toString();
            String lastName = newLastName.getText().toString();
            String email = newEmail.getText().toString();
            String phoneNumber = newPhoneNumber.getText().toString();
            String password = newPassword.getText().toString();
            String confirmPassword = confirmNewPassword.getText().toString();

            //Validations
            if(TextUtils.isEmpty(firstName)){
                Toast.makeText(ProfileFragment.super.getContext(), "First name field is empty!", Toast.LENGTH_SHORT).show();
                newFirstName.requestFocus();
                return;
            }

            if(TextUtils.isEmpty(lastName)){
                Toast.makeText(ProfileFragment.super.getContext(), "Last name field is empty!", Toast.LENGTH_SHORT).show();
                newLastName.requestFocus();
                return;
            }

            if(TextUtils.isEmpty(email)){
                Toast.makeText(ProfileFragment.super.getContext(), "Email field is empty!", Toast.LENGTH_SHORT).show();
                newEmail.requestFocus();
                return;
            }

            if(TextUtils.isEmpty(phoneNumber)){
                Toast.makeText(ProfileFragment.super.getContext(), "Phone number is empty!", Toast.LENGTH_SHORT).show();
                newPhoneNumber.requestFocus();
                return;
            }

            if(phoneNumber.length() < 10){
                Toast.makeText(ProfileFragment.super.getContext(), "Phone number too short!", Toast.LENGTH_SHORT).show();
                newPhoneNumber.requestFocus();
                return;
            }

            // Check if the password is changed
            if (!password.equals("")) {
                // Check if the confirm password matches the new password
                if (password.equals(confirmPassword)) {
                    // Update the password
                    currentUser.setPassword(confirmPassword);
                } else {
                    // Display an error message
                    Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            //End of validations

            //Update the user's information
            currentUser.setFirstName(firstName);
            currentUser.setLastName(lastName);
            currentUser.setEmail(email);
            currentUser.setPhoneNumber(phoneNumber);


            UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
            userAPI.updateUserById(currentUser.getId(), currentUser).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()){
                        // Convert the Uri object to MultipartBody.Part object
                        if (!uriImagesArray.isEmpty()) {
                            Uri uri = uriImagesArray.get(0); // Retrieve the first image URI
                            File file = new File(getRealPathFromUri(uri));
                            RequestBody requestFile = RequestBody.create(MediaType.parse(ProfileFragment.super.getContext().getContentResolver().getType(uri)), file);
                            MultipartBody.Part body = MultipartBody.Part.createFormData("profilePicture", file.getName(), requestFile);

                            profilePictureAPI.uploadProfilePicture(currentUser.getId(), body).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if(response.isSuccessful()){

                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {

                                }
                            });
                        }
                        Toast.makeText(ProfileFragment.super.getContext(), "Account information updated!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(ProfileFragment.super.getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
        });

        btnDeleteAccount.setOnClickListener(v -> {
            UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
            userAPI.deleteUserById(currentUser.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        ProfilePictureAPI profilePictureAPI = retrofitService.getRetrofit().create(ProfilePictureAPI.class);
                        profilePictureAPI.deleteProfilePicture(currentUser.getId()).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
                        Toast.makeText(ProfileFragment.super.getContext(), "Account deleted", Toast.LENGTH_SHORT).show();

                        //redirecting the user to the Intro Activity after the account is deleted
                        startActivity(new Intent(ProfileFragment.super.getContext(), IntroActivity.class));
                        getActivity().finish();
                    } else {
                        Toast.makeText(ProfileFragment.super.getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
        });

        btnDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfilePictureAPI profilePictureAPI = retrofitService.getRetrofit().create(ProfilePictureAPI.class);
                profilePictureAPI.deleteProfilePicture(currentUser.getId()).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Toast.makeText(ProfileFragment.super.getContext(), "Picture deleted", Toast.LENGTH_SHORT).show();
                        profilePicture.setImageResource(R.drawable.default_profile_picture);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        });

        return root;
    }

    // Helper method to find the index of a byte array within another byte array
    private int indexOf(byte[] array, byte[] target, int start) {
        for (int i = start; i < array.length - target.length + 1; i++) {
            boolean match = true;
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return i;
            }
        }
        return -1;
    }
    public String getRealPathFromUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = ProfileFragment.super.getContext().getContentResolver().query(uri, projection, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }
}