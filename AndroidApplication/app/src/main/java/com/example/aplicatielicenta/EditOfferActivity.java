package com.example.aplicatielicenta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
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

import com.example.aplicatielicenta.entities.Image;
import com.example.aplicatielicenta.entities.Offer;
import com.example.aplicatielicenta.entities.Property;
import com.example.aplicatielicenta.entities.PropertyType;
import com.example.aplicatielicenta.entities.User;
import com.example.aplicatielicenta.retrofit.ImageAPI;
import com.example.aplicatielicenta.retrofit.OfferAPI;
import com.example.aplicatielicenta.retrofit.PropertyAPI;
import com.example.aplicatielicenta.retrofit.RetrofitService;
import com.example.aplicatielicenta.ui.myoffers.MyOffersFragment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditOfferActivity extends AppCompatActivity {

    private static final int READ_PERMISSION = 101;
    private static final int PICK_IMAGES_REQUEST = 1;
    ArrayList<Uri> uriImagesArray = new ArrayList<>();
    EditText etTitle, etDescription, etAddress, etRooms, etFloor, etSurface, etPrice;
    Spinner spinnerType;
    private PropertyType[] propertyTypes;
    Button btnSaveOffer, btnAddImages;
    CheckBox cbHasAC, cbHasCentralHeating, cbHasBalcony, cbHasParking, cbAcceptSmokers, cbIsPetFriendly;
    int currentOfferID;
    ViewPager viewPager;
    List<Long> imageIds = new ArrayList<Long>();
    List<Bitmap> bitmapList = new ArrayList<>();
    HashMap<Long, Bitmap> imageBitmapMap = new HashMap<>();
    private ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_offer);

        //initialize the controls
        viewPager = findViewById(R.id.editOffer_view_pager);
        btnAddImages = findViewById(R.id.btnAddImage);
        btnSaveOffer = findViewById(R.id.btnSaveOffer);
        etTitle = findViewById(R.id.etEditTitle);
        etDescription = findViewById(R.id.etEditDescription);
        etAddress = findViewById(R.id.etEditAddress);
        etRooms = findViewById(R.id.etEditRooms);
        etFloor = findViewById(R.id.etEditFloor);
        etSurface = findViewById(R.id.etEditSurface);
        etPrice = findViewById(R.id.etEditPrice);
        cbHasAC = findViewById(R.id.checkBoxAC);
        cbHasCentralHeating = findViewById(R.id.checkBoxHeating);
        cbHasBalcony = findViewById(R.id.checkBoxBalcony);
        cbHasParking = findViewById(R.id.checkBoxParking);
        cbAcceptSmokers = findViewById(R.id.checkBoxSmokers);
        cbIsPetFriendly = findViewById(R.id.checkBoxPets);
        spinnerType = findViewById(R.id.spinnerType);
        populateSpinner();

        // getting the data of the offer which needs to be edited
        Intent offerIntent = getIntent();
        if (offerIntent.hasExtra("offerId")) {
            currentOfferID = offerIntent.getIntExtra("offerId", 0);
        }

        // getting the images
        RetrofitService retrofitService = new RetrofitService();
        ImageAPI imageAPI = retrofitService.getRetrofit().create(ImageAPI.class);

        imageAPI.getOfferImageIds(currentOfferID).enqueue(new Callback<List<Long>>() {
            @Override
            public void onResponse(Call<List<Long>> call, Response<List<Long>> response) {
                imageIds = response.body();

                // get the images
                imageAPI.getOfferImages(currentOfferID).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                // Convert response body to byte array
                                byte[] imageBytes = response.body().bytes();

                                // Split image bytes into separate images using delimiter string
                                String delimiter = "IMAGE_DELIMITER";
                                List<byte[]> imageList = new ArrayList<>();
                                int start = 0;
                                int end;
                                while ((end = indexOf(imageBytes, delimiter.getBytes(), start)) != -1) {
                                    imageList.add(Arrays.copyOfRange(imageBytes, start, end));
                                    start = end + delimiter.length();
                                }

                                // Convert byte arrays to bitmaps and add to image list
                                // List<Bitmap> bitmapList = new ArrayList<>();
                                for (byte[] image : imageList) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                                    bitmapList.add(bitmap);
                                }

                                // create a hash map which stores the id of the image as key
                                for (int i = 0; i < imageIds.size(); i++) {
                                    Long imageId = imageIds.get(i);
                                    Bitmap bitmap = bitmapList.get(i);
                                    imageBitmapMap.put(imageId, bitmap);
                                }

                                // initialize the ViewPager adapter and indicator
                                initAdapter(imageBitmapMap);


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

            @Override
            public void onFailure(Call<List<Long>> call, Throwable t) {

            }
        });

        btnAddImages.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(EditOfferActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(EditOfferActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION);
            }
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // allow multiple image selection
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Images"), PICK_IMAGES_REQUEST);
        });


        PropertyAPI propertyAPI = retrofitService.getRetrofit().create(PropertyAPI.class);
        OfferAPI offerAPI = retrofitService.getRetrofit().create(OfferAPI.class);
        offerAPI.getOfferById(currentOfferID).enqueue(new Callback<Offer>() {
            @Override
            public void onResponse(Call<Offer> call, Response<Offer> response) {
                Offer offerToEdit = response.body();
                etTitle.setText(offerToEdit.getTitle());
                etDescription.setText(offerToEdit.getDescription());

                // inserting into text fields the current data
                Property propertyToEdit = offerToEdit.getProperty();
                etAddress.setText(propertyToEdit.getAddress());
                etRooms.setText(String.valueOf(propertyToEdit.getNoOfRooms()));
                etFloor.setText(String.valueOf(propertyToEdit.getFloor()));
                etSurface.setText(String.valueOf(propertyToEdit.getSurface()));
                cbAcceptSmokers.setChecked(propertyToEdit.isAcceptSmokers());
                cbHasAC.setChecked(propertyToEdit.isHasAC());
                cbHasBalcony.setChecked(propertyToEdit.isHasBalcony());
                cbHasParking.setChecked(propertyToEdit.isHasParkingSpace());
                cbHasCentralHeating.setChecked(propertyToEdit.isHasCentralHeating());
                cbIsPetFriendly.setChecked(propertyToEdit.isPetFriendly());
                etPrice.setText(String.valueOf(offerToEdit.getPrice()));

                PropertyType propertyType = offerToEdit.getProperty().getType();
                int selectedPosition = 0;
                for (int i = 0; i < propertyTypes.length; i++) {
                    if (propertyTypes[i] == propertyType) {
                        selectedPosition = i;
                        break;
                    }
                }
                spinnerType.setSelection(selectedPosition);

                btnSaveOffer.setOnClickListener(v -> {
                    String title = etTitle.getText().toString();
                    if (TextUtils.isEmpty(title)) {
                        Toast.makeText(EditOfferActivity.this, "Title cannot be empty!", Toast.LENGTH_SHORT).show();
                        etTitle.requestFocus();
                        return;
                    }

                    String description = etDescription.getText().toString();
                    if (TextUtils.isEmpty(description)) {
                        Toast.makeText(EditOfferActivity.this, "Description cannot be empty!", Toast.LENGTH_SHORT).show();
                        etDescription.requestFocus();
                        return;
                    }

                    String address = etAddress.getText().toString();
                    if (TextUtils.isEmpty(address)) {
                        Toast.makeText(EditOfferActivity.this, "Address cannot be empty!", Toast.LENGTH_SHORT).show();
                        etAddress.requestFocus();
                        return;
                    }

                    int noOfRooms = 0;
                    if (!TextUtils.isEmpty(etRooms.getText().toString())) {
                        noOfRooms = Integer.parseInt(etRooms.getText().toString());
                    } else {
                        Toast.makeText(EditOfferActivity.this, "Number of Rooms cannot be empty!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (noOfRooms <= 0) {
                        Toast.makeText(EditOfferActivity.this, "Invalid number of rooms", Toast.LENGTH_SHORT).show();
                        etRooms.requestFocus();
                        return;
                    }

                    int floor = 0;
                    if (!TextUtils.isEmpty(etFloor.getText().toString())) {
                        floor = Integer.parseInt(etFloor.getText().toString());
                    } else {
                        Toast.makeText(EditOfferActivity.this, "Floor cannot be empty!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    float surface = 0;
                    if (!TextUtils.isEmpty(etSurface.getText().toString())) {
                        surface = Float.parseFloat(etSurface.getText().toString());
                    } else {
                        Toast.makeText(EditOfferActivity.this, "Surface cannot be empty!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (surface <= 0) {
                        Toast.makeText(EditOfferActivity.this, "Invalid surface", Toast.LENGTH_SHORT).show();
                        etSurface.requestFocus();
                        return;
                    }

                    PropertyType selectedPropertyType = (PropertyType) spinnerType.getSelectedItem();


                    boolean hasAC = cbHasAC.isChecked();
                    boolean hasCentralHeating = cbHasCentralHeating.isChecked();
                    boolean hasBalcony = cbHasBalcony.isChecked();
                    boolean hasParking = cbHasParking.isChecked();
                    boolean acceptSmokers = cbAcceptSmokers.isChecked();
                    boolean isPetFriendly = cbIsPetFriendly.isChecked();

                    float price = 0;
                    if (!TextUtils.isEmpty(etPrice.getText().toString())) {
                        price = Float.parseFloat(etPrice.getText().toString());
                    } else {
                        Toast.makeText(EditOfferActivity.this, "Price cannot be empty!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (price <= 0) {
                        Toast.makeText(EditOfferActivity.this, "Invalid price", Toast.LENGTH_SHORT).show();
                        etPrice.requestFocus();
                        return;
                    }
                    float finalPrice = price;

                    // update the property's details
                    propertyToEdit.setAddress(address);
                    propertyToEdit.setNoOfRooms(noOfRooms);
                    propertyToEdit.setFloor(floor);
                    propertyToEdit.setSurface(surface);
                    propertyToEdit.setPetFriendly(isPetFriendly);
                    propertyToEdit.setHasParkingSpace(hasParking);
                    propertyToEdit.setHasBalcony(hasBalcony);
                    propertyToEdit.setHasAC(hasAC);
                    propertyToEdit.setHasCentralHeating(hasCentralHeating);
                    propertyToEdit.setAcceptSmokers(acceptSmokers);
                    propertyToEdit.setType(selectedPropertyType);


                    propertyAPI.updatePropertyById(propertyToEdit.getId(), propertyToEdit).enqueue(new Callback<Property>() {
                        @Override
                        public void onResponse(Call<Property> call, Response<Property> response) {

                            // update the offer's details
                            offerToEdit.setTitle(title);
                            offerToEdit.setDescription(description);
                            offerToEdit.setPrice(finalPrice);
                            offerToEdit.setProperty(response.body());

                            offerAPI.updateOfferById(currentOfferID, offerToEdit).enqueue(new Callback<Offer>() {
                                @Override
                                public void onResponse(Call<Offer> call, Response<Offer> response) {
                                    Offer updatedOffer = response.body();

                                    // Convert the list of Uri objects to a list of MultipartBody.Part objects
                                    List<MultipartBody.Part> images = new ArrayList<>();
                                    for (Uri uri : uriImagesArray) {
                                        File file = new File(getRealPathFromUri(uri));
                                        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(uri)), file);
                                        MultipartBody.Part body = MultipartBody.Part.createFormData("images", file.getName(), requestFile);
                                        images.add(body);
                                    }

                                    ImageAPI imageAPI = retrofitService.getRetrofit().create(ImageAPI.class);
                                    imageAPI.uploadImage(updatedOffer.getId(), images).enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {

                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {

                                        }
                                    });

                                    Toast.makeText(EditOfferActivity.this, "Offer Updated!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(EditOfferActivity.this, MainActivity.class));
                                    finish();
                                }

                                @Override
                                public void onFailure(Call<Offer> call, Throwable t) {

                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<Property> call, Throwable t) {

                        }
                    });

                });
            }

            @Override
            public void onFailure(Call<Offer> call, Throwable t) {

            }
        });

        btnAddImages.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(EditOfferActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(EditOfferActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION);
            }
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // allow multiple image selection
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Images"), PICK_IMAGES_REQUEST);
        });
    }

    // initialize the adapter and indicator
    private void initAdapter(HashMap<Long, Bitmap> imageBitmapMap) {
        adapter = new ImageAdapter(EditOfferActivity.this, imageBitmapMap);
        viewPager.setAdapter(adapter);
        CircleIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
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
        String[] projection = {MediaStore.Images.Media.DATA};
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
            if (clipData != null) { // multiple image selection
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    // Add the image URI to the list of URIs
                    uriImagesArray.add(imageUri);

                    // Load the bitmap for the selected image URI
                    Bitmap bitmap = loadBitmapFromUri(imageUri);

                    bitmapList.add(bitmap);
                }
            } else { // single image selection
                Uri imageUri = data.getData();
                // Add the image URI to the list of URIs
                uriImagesArray.add(imageUri);

                // Load the bitmap for the selected image URI
                Bitmap bitmap = loadBitmapFromUri(imageUri);
                bitmapList.add(bitmap);
            }

            // Update the adapter
            viewPager.getAdapter().notifyDataSetChanged();
            updateIndicator();
        }
    }

    // Helper method to load a bitmap from a URI
    private Bitmap loadBitmapFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to populate the spinner with PropertyType values
    private void populateSpinner() {
        propertyTypes = PropertyType.values();

        ArrayAdapter<PropertyType> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, propertyTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
    }

    // function which updates the Circle Indicator (display the corrct number of dots
    // which is related to the number of pictures)
    private void updateIndicator() {
        CircleIndicator indicator = findViewById(R.id.indicator);
        if (adapter != null) {
            int count = adapter.getCount();
            indicator.setViewPager(viewPager);
            indicator.setVisibility(count > 1 ? View.VISIBLE : View.INVISIBLE);
        } else {
            indicator.setVisibility(View.INVISIBLE);
        }
    }

    public class ImageAdapter extends PagerAdapter {
        private Context context;
        private List<Bitmap> bitmapList;
        private HashMap<Long, Bitmap> imageBitmapMap;

        public ImageAdapter(Context context, HashMap<Long, Bitmap> imageBitmapMap) {
            this.context = context;
            this.imageBitmapMap = imageBitmapMap;
            this.bitmapList = new ArrayList<>(imageBitmapMap.values());
        }

        @Override
        public int getCount() {
            return bitmapList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            // Inflate the view
            View view = LayoutInflater.from(context).inflate(R.layout.viewpager_item, container, false);

            // Handle the delete button
            Button btnDeleteImage = view.findViewById(R.id.btn_delete_image);
            btnDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Long imageId = new ArrayList<>(imageBitmapMap.keySet()).get(position);

                    RetrofitService retrofitService = new RetrofitService();
                    ImageAPI imageAPI = retrofitService.getRetrofit().create(ImageAPI.class);

                    imageAPI.deleteImage(imageId).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                imageBitmapMap.remove(imageId);
                                bitmapList = new ArrayList<>(imageBitmapMap.values()); // Update bitmapList
                                notifyDataSetChanged();

                                // Update the view pager's adapter with the new bitmapList
                                viewPager.setAdapter(new ImageAdapter(context, imageBitmapMap));
                                updateIndicator();

                                Toast.makeText(EditOfferActivity.this, "Image deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("API Error", "Failed to delete image: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }

                    });
                }
            });

            // Get the image view
            ImageView imageView = view.findViewById(R.id.viewPager_image);

            // Set the bitmap to the image view
            imageView.setImageBitmap(bitmapList.get(position));

            // Add the view to the container
            container.addView(view);

            // Return the view
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            // Remove the view from the container
            container.removeView((View) object);
        }
    }
}