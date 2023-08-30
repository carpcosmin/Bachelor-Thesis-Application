package com.example.aplicatielicenta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.aplicatielicenta.entities.Offer;
import com.example.aplicatielicenta.entities.Property;
import com.example.aplicatielicenta.entities.PropertyType;
import com.example.aplicatielicenta.entities.User;
import com.example.aplicatielicenta.retrofit.ImageAPI;
import com.example.aplicatielicenta.retrofit.OfferAPI;
import com.example.aplicatielicenta.retrofit.ProfilePictureAPI;
import com.example.aplicatielicenta.retrofit.RetrofitService;


import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;


import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewOfferActivity extends AppCompatActivity {
    ViewPager viewPager;
    TextView tvTitleContent, tvPriceContent, tvDescriptionContent, tvAddressContent,
                tvSurfaceContent, tvRoomsContent, tvFloorContent, tvTypeContent,
                tvUserFullName,tvAC, tvCentralHeating, tvBalcony, tvParking, tvSmokers, tvPetFriendly;
    Button btnCall, btnMessage;
    CircleImageView profilePicture;
    int currentOfferID;
    private MapView mapView;
    private MapboxMap mapboxMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_view_offer);

        // initialize the controls
        viewPager = findViewById(R.id.view_pager);
        tvTitleContent = findViewById(R.id.tvTitleContent);
        tvPriceContent = findViewById(R.id.tvPriceContent);
        tvDescriptionContent = findViewById(R.id.tvDescriptionContent);
        tvAddressContent = findViewById(R.id.tvAddressContent);
        tvSurfaceContent = findViewById(R.id.tvSurfaceContent);
        tvRoomsContent = findViewById(R.id.tvRoomsContent);
        tvFloorContent = findViewById(R.id.tvFloorContent);
        tvTypeContent = findViewById(R.id.tvTypeContent);
        tvUserFullName = findViewById(R.id.tvUserFullName);
        tvAC = findViewById(R.id.tvAC);
        tvCentralHeating = findViewById(R.id.tvCentralHeating);
        tvBalcony = findViewById(R.id.tvBalcony);
        tvParking = findViewById(R.id.tvParking);
        tvSmokers = findViewById(R.id.tvSmokers);
        tvPetFriendly = findViewById(R.id.tvPetFriendly);
        btnCall = findViewById(R.id.btnCallUser);
        btnMessage = findViewById(R.id.btnMessageUser);
        mapView = findViewById(R.id.mapView);
        profilePicture = findViewById(R.id.profile_picture);

        // getting the data of the offer which will be displayed
        Intent offerIntent = getIntent();
        if (offerIntent.hasExtra("offerId")) {
            currentOfferID = offerIntent.getIntExtra("offerId", 0);
        }

        // getting the images
        RetrofitService retrofitService = new RetrofitService();
        ImageAPI imageAPI = retrofitService.getRetrofit().create(ImageAPI.class);

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
                        List<Bitmap> bitmapList = new ArrayList<>();
                        for (byte[] image : imageList) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                            bitmapList.add(bitmap);
                        }

                        // initialize the ViewPager adapter
                        ImageAdapter adapter = new ImageAdapter(ViewOfferActivity.this, bitmapList);
                        viewPager.setAdapter(adapter);

                        CircleIndicator indicator = findViewById(R.id.indicator);
                        indicator.setViewPager(viewPager);


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

        // getting the details of the offer
        OfferAPI offerAPI = retrofitService.getRetrofit().create(OfferAPI.class);
        offerAPI.getOfferById(currentOfferID).enqueue(new Callback<Offer>() {
            @Override
            public void onResponse(Call<Offer> call, Response<Offer> response) {
                Offer currentOffer = response.body();
                Property currentProperty = currentOffer.getProperty();
                User offerCreator = currentOffer.getUser();

                // fill the TextViews with data
                tvTitleContent.setText(currentOffer.getTitle());
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setDecimalSeparator(',');
                symbols.setGroupingSeparator('.');
                DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);
                String formattedPrice = decimalFormat.format(currentOffer.getPrice());
                formattedPrice += " \u20AC";
                tvPriceContent.setText(formattedPrice);
                tvDescriptionContent.setText(currentOffer.getDescription());
                double surface = currentProperty.getSurface();
                String surfaceString = String.format("%.1f m\u00B2", surface);
                tvSurfaceContent.setText(surfaceString);
                tvRoomsContent.setText(String.valueOf(currentProperty.getNoOfRooms()));
                tvFloorContent.setText(String.valueOf(currentProperty.getFloor()));
                tvAddressContent.setText(currentProperty.getAddress());
                String fullName = offerCreator.getFirstName() + " " + offerCreator.getLastName();
                tvUserFullName.setText(fullName);
                PropertyType propertyType = currentProperty.getType();
                tvTypeContent.setText(propertyType.toString());


                boolean hasAC = currentProperty.isHasAC();
                boolean hasCentralHeating = currentProperty.isHasCentralHeating();
                boolean hasBalcony = currentProperty.isHasBalcony();
                boolean hasParking = currentProperty.isHasParkingSpace();
                boolean acceptsSmokers = currentProperty.isAcceptSmokers();
                boolean isPetFriendly = currentProperty.isPetFriendly();

                if (hasAC) {
                    tvAC.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_24, 0, 0, 0);
                } else {
                    tvAC.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_close_24, 0, 0, 0);
                }

                if (hasCentralHeating) {
                    tvCentralHeating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_24, 0, 0, 0);
                } else {
                    tvCentralHeating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_close_24, 0, 0, 0);
                }

                if (hasBalcony) {
                    tvBalcony.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_24, 0, 0, 0);
                } else {
                    tvBalcony.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_close_24, 0, 0, 0);
                }

                if (hasParking) {
                    tvParking.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_24, 0, 0, 0);
                } else {
                    tvParking.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_close_24, 0, 0, 0);
                }

                if (acceptsSmokers) {
                    tvSmokers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_24, 0, 0, 0);
                } else {
                    tvSmokers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_close_24, 0, 0, 0);
                }

                if (isPetFriendly) {
                    tvPetFriendly.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_24, 0, 0, 0);
                } else {
                    tvPetFriendly.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_close_24, 0, 0, 0);
                }

                mapView.onCreate(savedInstanceState);
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                        ViewOfferActivity.this.mapboxMap = mapboxMap;
                        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                            @Override
                            public void onStyleLoaded(@NonNull Style style) {
                                // Map is ready
                                String address = tvAddressContent.getText().toString();
                                geocodeAddress(address);

                            }
                        });
                    }
                });

                // get the current profile picture
                ProfilePictureAPI profilePictureAPI = retrofitService.getRetrofit().create(ProfilePictureAPI.class);
                profilePictureAPI.getProfilePicture(currentOffer.getUser().getId()).enqueue(new Callback<ResponseBody>() {
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

                btnCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phoneNumber = offerCreator.getPhoneNumber();
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                        dialIntent.setData(Uri.parse("tel:" + phoneNumber));
                        startActivity(dialIntent);
                    }
                });

                btnMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phoneNumber = offerCreator.getPhoneNumber();
                        Uri uri = Uri.parse("smsto:" + phoneNumber);
                        Intent messageIntent = new Intent(Intent.ACTION_SENDTO, uri);
                        String message = "Hello! I would like to know more details about your offer.";
                        messageIntent.putExtra("sms_body", message);
                        startActivity(messageIntent);
                    }
                });
            }

            @Override
            public void onFailure(Call<Offer> call, Throwable t) {

            }
        });
    }

    private void geocodeAddress(String address) {
        MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
                .accessToken("pk.eyJ1IjoiY29zbWluMjgwMiIsImEiOiJjbGJmMmVyMjEwMjJzM29wZHczN2JuZDQyIn0.TqZbu8NH54R_M37w02mVwA") // Replace with your Mapbox access token
                .query(address)
                .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                .mode(GeocodingCriteria.MODE_PLACES)
                .build();

        mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                if (response.isSuccessful()) {
                    List<CarmenFeature> results = response.body().features();
                    if (results != null && !results.isEmpty()) {
                        CarmenFeature feature = results.get(0);
                        LatLng location = new LatLng(feature.center().latitude(), feature.center().longitude());
                        addMarker(location, address);
                        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12.0));
                    }
                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void addMarker(LatLng location, String address) {
        mapboxMap.addMarker(new MarkerOptions()
                .position(location)
                .snippet(address));
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

    public class ImageAdapter extends PagerAdapter {
        private Context context;
        private List<Bitmap> bitmapList;

        public ImageAdapter(Context context, List<Bitmap> bitmapList) {
            this.context = context;
            this.bitmapList = bitmapList;
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

            // Get the image view
            ImageView imageView = view.findViewById(R.id.viewPager_image);

            // Hide the delete button
            Button deleteImageButton = view.findViewById(R.id.btn_delete_image);
            deleteImageButton.setVisibility(View.GONE);

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

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

}
