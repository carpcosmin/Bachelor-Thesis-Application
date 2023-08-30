package com.example.aplicatielicenta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aplicatielicenta.entities.Offer;
import com.example.aplicatielicenta.entities.Property;
import com.example.aplicatielicenta.entities.PropertyType;
import com.example.aplicatielicenta.entities.User;
import com.example.aplicatielicenta.retrofit.ImageAPI;
import com.example.aplicatielicenta.retrofit.OfferAPI;
import com.example.aplicatielicenta.retrofit.PropertyAPI;
import com.example.aplicatielicenta.retrofit.RetrofitService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddOfferActivity extends AppCompatActivity {

    private static final int READ_PERMISSION = 101;
    private static final int PICK_IMAGES_REQUEST = 1;
    ArrayList<Uri> uriImagesArray = new ArrayList<>();
    TextView textViewPhotos;
    TextView textViewDelete;
    EditText etTitle, etDescription, etAddress, etRooms, etFloor, etSurface, etPrice;
    Spinner spinnerType;
    Button btnSaveOffer, btnAddImages;
    CheckBox cbHasAC, cbHasCentralHeating, cbHasBalcony, cbHasParking, cbAcceptSmokers, cbIsPetFriendly;
    PropertyType selectedType = null;
    MaterialButtonToggleGroup toggleGroupOfferType;
    MaterialButton btnRent, btnSell;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);

        //initialize the controls
        btnAddImages = findViewById(R.id.btnAddImage);
        textViewPhotos = findViewById(R.id.tvNoOfPhotos);
        textViewDelete = findViewById(R.id.tvDeleteImage);
        btnSaveOffer = findViewById(R.id.btnSaveOffer);
        etTitle = findViewById(R.id.editTextTitle);
        etDescription = findViewById(R.id.editTextDescription);
        etAddress = findViewById(R.id.editTextAddress);
        etRooms = findViewById(R.id.editTextRooms);
        etFloor = findViewById(R.id.editTextFloor);
        etSurface = findViewById(R.id.editTextSurface);
        etPrice = findViewById(R.id.editTextPrice);
        cbHasAC = findViewById(R.id.checkBoxAC);
        cbHasCentralHeating = findViewById(R.id.checkBoxHeating);
        cbHasBalcony = findViewById(R.id.checkBoxBalcony);
        cbHasParking = findViewById(R.id.checkBoxParking);
        cbAcceptSmokers = findViewById(R.id.checkBoxSmokers);
        cbIsPetFriendly = findViewById(R.id.checkBoxPets);
        spinnerType = findViewById(R.id.spinnerType);
        toggleGroupOfferType = findViewById(R.id.toggleGroupOfferType);
        btnRent = findViewById(R.id.btnRent);
        btnSell = findViewById(R.id.btnSell);

        toggleGroupOfferType.check(R.id.btnRent);

        final ArrayAdapter<PropertyType>[] adapter = new ArrayAdapter[]{new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, PropertyType.values())};
        spinnerType.setAdapter(adapter[0]);

        textViewPhotos.setVisibility(View.GONE);
        textViewDelete.setVisibility(View.GONE);

        btnAddImages.setOnClickListener(v -> {

            if(ContextCompat.checkSelfPermission(AddOfferActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(AddOfferActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION);
            }
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // allow multiple image selection
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setPackage("com.google.android.apps.photos");
            startActivityForResult(Intent.createChooser(intent, "Select Images"), PICK_IMAGES_REQUEST);
        });

        btnSaveOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
                if(TextUtils.isEmpty(title)){
                    Toast.makeText(AddOfferActivity.this, "Title cannot be empty!", Toast.LENGTH_SHORT).show();
                    etTitle.requestFocus();
                    return;
                }

                String description = etDescription.getText().toString();
                if(TextUtils.isEmpty(description)){
                    Toast.makeText(AddOfferActivity.this, "Description cannot be empty!", Toast.LENGTH_SHORT).show();
                    etDescription.requestFocus();
                    return;
                }

                String address = etAddress.getText().toString();
                if(TextUtils.isEmpty(address)){
                    Toast.makeText(AddOfferActivity.this, "Address cannot be empty!", Toast.LENGTH_SHORT).show();
                    etAddress.requestFocus();
                    return;
                }

                int noOfRooms = 0;
                if(!TextUtils.isEmpty(etRooms.getText().toString())){
                    noOfRooms = Integer.parseInt(etRooms.getText().toString());
                }else{
                    Toast.makeText(AddOfferActivity.this, "Number of Rooms cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if( noOfRooms <= 0 ){
                    Toast.makeText(AddOfferActivity.this, "Invalid number of rooms", Toast.LENGTH_SHORT).show();
                    etRooms.requestFocus();
                    return;
                }

                int floor = 0;
                if(!TextUtils.isEmpty(etFloor.getText().toString())){
                    floor = Integer.parseInt(etFloor.getText().toString());
                }else{
                    Toast.makeText(AddOfferActivity.this, "Floor cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                float surface = 0;
                if(!TextUtils.isEmpty(etSurface.getText().toString())){
                    surface = Float.parseFloat(etSurface.getText().toString());
                }else{
                    Toast.makeText(AddOfferActivity.this, "Surface cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if( surface <= 0 ){
                    Toast.makeText(AddOfferActivity.this, "Invalid surface", Toast.LENGTH_SHORT).show();
                    etSurface.requestFocus();
                    return;
                }

                selectedType = (PropertyType) spinnerType.getSelectedItem();

                boolean hasAC = cbHasAC.isChecked();
                boolean hasCentralHeating = cbHasCentralHeating.isChecked();
                boolean hasBalcony = cbHasBalcony.isChecked();
                boolean hasParking = cbHasParking.isChecked();
                boolean acceptSmokers = cbAcceptSmokers.isChecked();
                boolean isPetFriendly = cbIsPetFriendly.isChecked();
                boolean toRent = btnRent.isChecked();

                float price = 0;
                if(!TextUtils.isEmpty(etPrice.getText().toString())){
                    price = Float.parseFloat(etPrice.getText().toString());
                }else{
                    Toast.makeText(AddOfferActivity.this, "Price cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if( price <= 0 ){
                    Toast.makeText(AddOfferActivity.this, "Invalid price", Toast.LENGTH_SHORT).show();
                    etPrice.requestFocus();
                    return;
                }

                //creating a Property
                Property newProperty = new Property();
                newProperty.setAddress(address);
                newProperty.setNoOfRooms(noOfRooms);
                newProperty.setFloor(floor);
                newProperty.setSurface(surface);
                newProperty.setPetFriendly(isPetFriendly);
                newProperty.setHasParkingSpace(hasParking);
                newProperty.setHasBalcony(hasBalcony);
                newProperty.setHasAC(hasAC);
                newProperty.setHasCentralHeating(hasCentralHeating);
                newProperty.setAcceptSmokers(acceptSmokers);
                newProperty.setType(selectedType);

                //getting the current user's data
                Intent intent = getIntent();
                if (intent.hasExtra("user")) {
                    currentUser = (User) intent.getSerializableExtra("user");
                }

                //saving the Property in the database
                RetrofitService retrofitService = new RetrofitService();
                PropertyAPI propertyAPI = retrofitService.getRetrofit().create(PropertyAPI.class);
                float finalPrice = price;
                propertyAPI.saveProperty(newProperty).enqueue(new Callback<Property>() {
                    @Override
                    public void onResponse(Call<Property> call, Response<Property> response) {
                        Property propertyCreated = response.body();

                        Offer newOffer = new Offer();

                        newOffer.setTitle(title);
                        newOffer.setDescription(description);
                        newOffer.setPrice(finalPrice);
                        newOffer.setUser(currentUser);
                        newOffer.setProperty(propertyCreated);
                        newOffer.setToRent(toRent);

                        OfferAPI offerAPI = retrofitService.getRetrofit().create(OfferAPI.class);
                        offerAPI.saveOffer(newOffer).enqueue(new Callback<Offer>() {
                            @Override
                            public void onResponse(@NonNull Call<Offer> call, @NonNull Response<Offer> response) {
                                Offer createdOffer = response.body();

                                // Convert the list of Uri objects to a list of MultipartBody.Part objects
                                List<MultipartBody.Part> images = new ArrayList<>();
                                for (Uri uri : uriImagesArray) {
                                    File file = new File(getRealPathFromUri(uri));
                                    RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(uri)), file);
                                    MultipartBody.Part body = MultipartBody.Part.createFormData("images", file.getName(), requestFile);
                                    images.add(body);
                                }

                                ImageAPI imageAPI = retrofitService.getRetrofit().create(ImageAPI.class);
                                imageAPI.uploadImage(createdOffer.getId(), images).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {

                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {

                                    }
                                });

                                Toast.makeText(AddOfferActivity.this, "Offer Added!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AddOfferActivity.this, MainActivity.class));
                                finish();
                            }

                            @Override
                            public void onFailure(Call<Offer> call, Throwable t) {
                                Toast.makeText(AddOfferActivity.this, "FAIL", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onFailure(Call<Property> call, Throwable t) {

                    }
                });
            }
        });
    }

    public String getRealPathFromUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK && data != null) {
            ClipData clipData = data.getClipData();
            GridLayout imageGrid = findViewById(R.id.image_grid);
            if (clipData != null) { // multiple image selection
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    uriImagesArray.add(imageUri);
                    ImageView imageView = new ImageView(this);
                    imageView.setImageURI(imageUri);
                    // set layout parameters for the ImageView
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.width = 300; // set the width of the ImageView
                    params.height = 300; // set the height of the ImageView
                    params.setMargins(10, 10, 10, 10); // set the margins for the ImageView
                    params.setGravity(Gravity.CENTER);
                    imageView.setLayoutParams(params);
                    // add the ImageView to the GridLayout
                    imageGrid.addView(imageView);
                    textViewPhotos.setVisibility(View.VISIBLE);
                    textViewDelete.setVisibility(View.VISIBLE);

                    //delete the image when the user clicks on it
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imageGrid.removeView(v);
                            uriImagesArray.remove(imageUri);
                            if(imageGrid.getChildCount() > 0){
                                textViewPhotos.setText("Photos Added (" + imageGrid.getChildCount() + ")");
                                textViewPhotos.setVisibility(View.VISIBLE);
                                textViewDelete.setVisibility(View.VISIBLE);
                            }
                            else {
                                textViewPhotos.setVisibility(View.GONE);
                                textViewDelete.setVisibility(View.GONE);
                            }
                        }
                    });
                    //displaying the number of added pictures
                    textViewPhotos.setText("Photos Added (" + imageGrid.getChildCount() +")");
                    textViewDelete.setText("Press on a picture to delete it");
                }
            } else { // single image selection
                Uri imageUri = data.getData();
                uriImagesArray.add(imageUri);
                ImageView imageView = new ImageView(this);
                imageView.setImageURI(imageUri);
                // set layout parameters for the ImageView
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 300; // set the width of the ImageView
                params.height = 300; // set the height of the ImageView
                params.setMargins(10, 10, 10, 10); // set the margins for the ImageView
                params.setGravity(Gravity.CENTER);
                imageView.setLayoutParams(params);
                // add the ImageView to the GridLayout
                imageGrid.addView(imageView);
                textViewPhotos.setVisibility(View.VISIBLE);
                textViewDelete.setVisibility(View.VISIBLE);

                //delete the image when the user clicks on it
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageGrid.removeView(v);
                        uriImagesArray.remove(imageUri);
                        textViewPhotos.setText("Photos Added (" + imageGrid.getChildCount() + ")");
                        textViewDelete.setVisibility(View.VISIBLE);
                        if(imageGrid.getChildCount() > 0){
                            textViewPhotos.setText("Photos Added (" + imageGrid.getChildCount() + ")");
                            textViewPhotos.setVisibility(View.VISIBLE);
                            textViewDelete.setVisibility(View.VISIBLE);
                        }
                        else {
                            textViewPhotos.setVisibility(View.GONE);
                            textViewDelete.setVisibility(View.GONE);
                        }
                    }
                });
                //displaying the number of added pictures
                textViewPhotos.setText("Photos Added (" + imageGrid.getChildCount() +")");
                textViewDelete.setVisibility(View.VISIBLE);
            }
        }
    }
}