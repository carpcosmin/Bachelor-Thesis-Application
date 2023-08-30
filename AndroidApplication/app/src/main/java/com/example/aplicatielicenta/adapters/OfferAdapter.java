package com.example.aplicatielicenta.adapters;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aplicatielicenta.IntroActivity;
import com.example.aplicatielicenta.MainActivity;
import com.example.aplicatielicenta.R;
import com.example.aplicatielicenta.entities.FavoriteOffer;
import com.example.aplicatielicenta.entities.Image;
import com.example.aplicatielicenta.entities.Offer;
import com.example.aplicatielicenta.entities.User;
import com.example.aplicatielicenta.retrofit.FavoriteOfferAPI;
import com.example.aplicatielicenta.retrofit.ImageAPI;
import com.example.aplicatielicenta.retrofit.OfferAPI;
import com.example.aplicatielicenta.retrofit.RetrofitService;
import com.example.aplicatielicenta.retrofit.UserAPI;
import com.example.aplicatielicenta.ui.home.HomeFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferHolder>{

    public List<Offer> offersList;
    List<FavoriteOffer> favorites;
    private final Context context;
    private User currentUser;

    public OfferAdapter(List<Offer> offerList, User currentUser,  Context context) {
        this.offersList = offerList;
        this.currentUser = currentUser;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickedListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    @NonNull
    @Override
    public OfferHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.offer_item, parent, false);
        return new OfferHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferAdapter.OfferHolder holder, @SuppressLint("RecyclerView") int position) {
        Offer item = offersList.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvAddress.setText(item.getProperty().getAddress());
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);
        String formattedPrice = decimalFormat.format(item.getPrice());
        formattedPrice += " \u20AC";
        holder.tvPrice.setText(formattedPrice);

        RetrofitService retrofitService = new RetrofitService();
        ImageAPI imageAPI = retrofitService.getRetrofit().create(ImageAPI.class);
        imageAPI.getOfferImages(item.getId()).enqueue(new Callback<ResponseBody>() {
           @Override
           public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
               if (response.isSuccessful()) {
                   // Convert response body to byte array
                   byte[] imageBytes;
                   try {
                       imageBytes = response.body().bytes();
                   } catch (IOException e) {
                       throw new RuntimeException(e);
                   }
                   // Display image in ImageView
                   if (imageBytes.length > 0) {
                       Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                       holder.imageIV.setImageBitmap(bitmap);
                   } else {
                       // Set null image placeholder
                       holder.imageIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.null_image_placeholder));
                   }
               }
           }

           @Override
           public void onFailure(Call<ResponseBody> call, Throwable t) {

           }
       });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });

        FavoriteOfferAPI favoriteOfferAPI = retrofitService.getRetrofit().create(FavoriteOfferAPI.class);
        favoriteOfferAPI.getFavoriteOffersForUser(currentUser.getId()).enqueue(new Callback<List<FavoriteOffer>>() {
            @Override
            public void onResponse(Call<List<FavoriteOffer>> call, Response<List<FavoriteOffer>> response) {
                if (response.isSuccessful()) {
                    favorites = response.body();
                    // Extract the Offer objects from the Favorite objects
                    for (FavoriteOffer favorite : favorites) {
                        if(favorite.getOffer().getId() == item.getId()) {
                            holder.btnFav.setChecked(true);
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<List<FavoriteOffer>> call, Throwable t) {
            }
        });

        holder.btnFav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    boolean isAlreadyFavorite = false;
                    // Check if the offer is already in favorites
                    for (FavoriteOffer favorite : favorites) {
                        if(favorite.getOffer().getId() == item.getId()) {
                            isAlreadyFavorite = true;
                            break;
                        }
                    }
                    if(!isAlreadyFavorite) {
                        // Add the offer to favorites
                        FavoriteOfferAPI favoriteOfferAPI = retrofitService.getRetrofit().create(FavoriteOfferAPI.class);
                        favoriteOfferAPI.addOfferToFavorite(currentUser.getId(), item.getId()).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                            }
                        });
                    }
                } else {
                    favoriteOfferAPI.deleteFavoriteOfferById(favorites.get(position).getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return offersList.size();
    }

    public static class OfferHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public TextView tvAddress;
        public TextView tvPrice;
        public CheckBox btnFav;
        public ImageView imageIV;
        public View mView;

        public OfferHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            imageIV = itemView.findViewById(R.id.imageIV);
            btnFav = itemView.findViewById(R.id.checkBoxFav);
        }
    }

}

