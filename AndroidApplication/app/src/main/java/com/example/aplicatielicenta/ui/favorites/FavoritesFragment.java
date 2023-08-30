package com.example.aplicatielicenta.ui.favorites;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicatielicenta.MainActivity;
import com.example.aplicatielicenta.R;
import com.example.aplicatielicenta.ViewOfferActivity;
import com.example.aplicatielicenta.adapters.FavoriteOffersAdapter;
import com.example.aplicatielicenta.adapters.MyOffersAdapter;
import com.example.aplicatielicenta.adapters.OfferAdapter;
import com.example.aplicatielicenta.entities.FavoriteOffer;
import com.example.aplicatielicenta.entities.Offer;
import com.example.aplicatielicenta.entities.User;
import com.example.aplicatielicenta.retrofit.FavoriteOfferAPI;
import com.example.aplicatielicenta.retrofit.RetrofitService;
import com.example.aplicatielicenta.ui.home.HomeFragment;
import com.example.aplicatielicenta.ui.myoffers.MyOffersFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_favorites, container, false);


        //getting the data of the logged in user
        MainActivity mainActivity =(MainActivity) getActivity();
        User currentUser = mainActivity.getUserData();

        RetrofitService retrofitService = new RetrofitService();
        FavoriteOfferAPI favoriteOfferAPI = retrofitService.getRetrofit().create(FavoriteOfferAPI.class);

        favoriteOfferAPI.getFavoriteOffersForUser(currentUser.getId()).enqueue(new Callback<List<FavoriteOffer>>() {
            @Override
            public void onResponse(Call<List<FavoriteOffer>> call, Response<List<FavoriteOffer>> response) {
                if (response.isSuccessful()) {
                    List<FavoriteOffer> favorites = response.body();
                    List<Offer> favoriteOffers = new ArrayList<>();

                    // Extract the Offer objects from the Favorite objects
                    for (FavoriteOffer favorite : favorites) {
                        favoriteOffers.add(favorite.getOffer());
                    }

                    RecyclerView favoriteOffersRV = root.findViewById(R.id.rv_favoriteOffers);
                    favoriteOffersRV.setLayoutManager(new LinearLayoutManager(FavoritesFragment.super.getContext()));
                    FavoriteOffersAdapter favoriteOffersAdapter = new FavoriteOffersAdapter(favoriteOffers, currentUser, getContext());
                    favoriteOffersRV.setAdapter(favoriteOffersAdapter);

                    favoriteOffersAdapter.setOnDeleteButtonClickListener(new MyOffersAdapter.OnDeleteButtonClickListener() {
                        @Override
                        public void onDeleteButtonClicked(int position) {
                            favoriteOfferAPI.deleteFavoriteOfferById(favorites.get(position).getId()).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    Toast.makeText(getContext(), "Offer removed from Favorites", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {

                                }
                            });
                        }
                    });

                    favoriteOffersAdapter.setOnItemClickedListener(new OfferAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Intent viewOfferIntent = new Intent(FavoritesFragment.super.getContext(), ViewOfferActivity.class);
                            // sending the id of the offer to the View Activity
                            viewOfferIntent.putExtra("offerId", favoriteOffers.get(position).getId());
                            startActivity(viewOfferIntent);
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<List<FavoriteOffer>> call, Throwable t) {

            }
        });


        return root;
    }
}
