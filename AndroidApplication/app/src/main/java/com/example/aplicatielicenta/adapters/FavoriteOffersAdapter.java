package com.example.aplicatielicenta.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicatielicenta.R;
import com.example.aplicatielicenta.entities.FavoriteOffer;
import com.example.aplicatielicenta.entities.Offer;
import com.example.aplicatielicenta.entities.User;
import com.example.aplicatielicenta.retrofit.FavoriteOfferAPI;
import com.example.aplicatielicenta.retrofit.ImageAPI;
import com.example.aplicatielicenta.retrofit.RetrofitService;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteOffersAdapter extends RecyclerView.Adapter<FavoriteOffersAdapter.OfferHolder>{
    public List<Offer> offersList;
    private final Context context;
    private User currentUser;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    private OfferAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickedListener(OfferAdapter.OnItemClickListener listener) {
        onItemClickListener = listener;
    }


    public interface OnDeleteButtonClickListener {
        void onDeleteButtonClicked(int position);
    }

    private static MyOffersAdapter.OnDeleteButtonClickListener onDeleteButtonClickListener;

    public void setOnDeleteButtonClickListener(MyOffersAdapter.OnDeleteButtonClickListener listener) {
        onDeleteButtonClickListener = listener;
    }

    public FavoriteOffersAdapter(List<Offer> offerList, User currentUser,  Context context) {
        this.offersList = offerList;
        this.currentUser = currentUser;
        this.context = context;
    }


    @NonNull
    @Override
    public FavoriteOffersAdapter.OfferHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.offer_item_favorite_offers, parent, false);
        return new FavoriteOffersAdapter.OfferHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteOffersAdapter.OfferHolder holder, @SuppressLint("RecyclerView") int position) {
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
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    holder.imageIV.setImageBitmap(bitmap);
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
    }

    @Override
    public int getItemCount() {
        return offersList.size();
    }

    public class OfferHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public TextView tvAddress;
        public TextView tvPrice;
        public ImageView imageIV;
        public Button btnDeleteFavoriteOffer;
        public View mView;

        public OfferHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            imageIV = itemView.findViewById(R.id.imageIV);
            btnDeleteFavoriteOffer = itemView.findViewById(R.id.btn_delete_favorite_offer);

            btnDeleteFavoriteOffer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onDeleteButtonClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onDeleteButtonClickListener.onDeleteButtonClicked(position);
                            offersList.remove(position);
                            notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }

}
