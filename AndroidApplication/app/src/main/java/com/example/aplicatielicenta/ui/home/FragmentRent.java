package com.example.aplicatielicenta.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aplicatielicenta.R;
import com.example.aplicatielicenta.ViewOfferActivity;
import com.example.aplicatielicenta.adapters.OfferAdapter;
import com.example.aplicatielicenta.entities.Offer;
import com.example.aplicatielicenta.entities.User;
import com.example.aplicatielicenta.retrofit.OfferAPI;
import com.example.aplicatielicenta.retrofit.RetrofitService;
import com.example.aplicatielicenta.retrofit.UserAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentRent extends Fragment {

    private List<Offer> offerList = new ArrayList<>();
    private List<Offer> offersToRent = new ArrayList<>();
    User currentUser;

    private OfferAdapter offerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_rent, container, false);

        RetrofitService retrofitService = new RetrofitService();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token != null) {
            UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
            userAPI.getUserByToken(token).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    currentUser = response.body();
                    RecyclerView offersRV = root.findViewById(R.id.offersRV);
                    offersRV.setLayoutManager(new LinearLayoutManager(FragmentRent.super.getContext()));

                    OfferAPI offerAPI = retrofitService.getRetrofit().create(OfferAPI.class);
                    offerAPI.getAllOffers().enqueue(new Callback<List<Offer>>() {
                        @Override
                        public void onResponse(Call<List<Offer>> call, Response<List<Offer>> response) {
                            offerList = response.body();

                            // display only the offers which are marked as "Rent"
                            offersToRent.clear();
                            for (Offer offer : offerList) {
                                Boolean toRent = offer.getToRent();
                                if (toRent != null && toRent) {
                                    offersToRent.add(offer);
                                }
                            }
                            offerAdapter = new OfferAdapter(offersToRent, currentUser, getContext());
                            offersRV.setAdapter(offerAdapter);
                            offerAdapter.notifyDataSetChanged();

                            offerAdapter.setOnItemClickedListener(new OfferAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    Intent viewOfferIntent = new Intent(FragmentRent.super.getContext(), ViewOfferActivity.class);
                                    // sending the id of the offer to the View Activity
                                    viewOfferIntent.putExtra("offerId", offersToRent.get(position).getId());
                                    startActivity(viewOfferIntent);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<List<Offer>> call, Throwable t) {

                        }
                    });
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
        }
        return root;
    }
}