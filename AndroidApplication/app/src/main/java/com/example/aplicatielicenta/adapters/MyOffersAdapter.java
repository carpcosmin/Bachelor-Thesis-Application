package com.example.aplicatielicenta.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicatielicenta.AddOfferActivity;
import com.example.aplicatielicenta.MainActivity;
import com.example.aplicatielicenta.R;
import com.example.aplicatielicenta.entities.Offer;
import com.example.aplicatielicenta.retrofit.ImageAPI;
import com.example.aplicatielicenta.retrofit.RetrofitService;
import com.example.aplicatielicenta.ui.myoffers.MyOffersFragment;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOffersAdapter extends RecyclerView.Adapter<MyOffersAdapter.OfferHolder>{

    public static List<Offer> offersList;

    public interface OnDeleteButtonClickListener {
        void onDeleteButtonClicked(int position);
    }

    public interface OnEditButtonClickListener {
        void onEditButtonClicked(int position);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    private OfferAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickedListener(OfferAdapter.OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    private static OnDeleteButtonClickListener onDeleteButtonClickListener;
    private static OnEditButtonClickListener onEditButtonClickListener;

    public void setOnDeleteButtonClickListener(OnDeleteButtonClickListener listener) {
        onDeleteButtonClickListener = listener;
    }

    public void setOnEditButtonClickListener(OnEditButtonClickListener listener) {
        onEditButtonClickListener = listener;
    }

    public MyOffersAdapter(List<Offer> offerList) {
        offersList = offerList;
    }

    @NonNull
    @Override
    public OfferHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.offer_item_myoffers, parent, false);
        OfferHolder viewHolder = new OfferHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OfferHolder holder, int position) {
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
                    holder.image.setImageBitmap(bitmap);
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
        public Button btnEditOffer;
        public Button btnDeleteOffer;

        public ImageView image;
        public View mView;

        public OfferHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            image = itemView.findViewById(R.id.imageView);
            btnEditOffer = itemView.findViewById(R.id.btnEditOffer);
            btnDeleteOffer = itemView.findViewById(R.id.btnDeleteOffer);

            btnDeleteOffer.setOnClickListener(new View.OnClickListener() {
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

            btnEditOffer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onEditButtonClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onEditButtonClickListener.onEditButtonClicked(position);
                            notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }

}


