package com.example.aplicatielicenta.ui.myoffers;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicatielicenta.AddOfferActivity;
import com.example.aplicatielicenta.EditOfferActivity;
import com.example.aplicatielicenta.MainActivity;
import com.example.aplicatielicenta.R;
import com.example.aplicatielicenta.ViewOfferActivity;
import com.example.aplicatielicenta.adapters.MyOffersAdapter;
import com.example.aplicatielicenta.adapters.OfferAdapter;
import com.example.aplicatielicenta.entities.FavoriteOffer;
import com.example.aplicatielicenta.entities.Offer;
import com.example.aplicatielicenta.entities.Property;
import com.example.aplicatielicenta.entities.User;
import com.example.aplicatielicenta.retrofit.FavoriteOfferAPI;
import com.example.aplicatielicenta.retrofit.ImageAPI;
import com.example.aplicatielicenta.retrofit.OfferAPI;
import com.example.aplicatielicenta.retrofit.PropertyAPI;
import com.example.aplicatielicenta.retrofit.RetrofitService;
import com.example.aplicatielicenta.ui.home.FragmentRent;
import com.example.aplicatielicenta.ui.profile.ProfileFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOffersFragment extends Fragment {
    private List<Offer> allOffers = new ArrayList<>();
    private final List<Offer> offersPostedByCurrentUser = new ArrayList<>();
    private RecyclerView userOffersRV;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    private OfferAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickedListener(OfferAdapter.OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_my_offers, container, false);

        RetrofitService retrofitService = new RetrofitService();
        OfferAPI offerAPI = retrofitService.getRetrofit().create(OfferAPI.class);
        offerAPI.getAllOffers().enqueue(new Callback<List<Offer>>() {
            @Override
            public void onResponse(Call<List<Offer>> call, Response<List<Offer>> response) {
                allOffers = response.body();

                //getting the data of the logged in user
                MainActivity mainActivity =(MainActivity) getActivity();
                User currentUser = mainActivity.getUserData();

                // keeping the Offers which are posted by the current User
                for(Offer offer : allOffers){
                    if(offer.getUser().getId() == currentUser.getId() ){
                        offersPostedByCurrentUser.add(offer);
                    }
                }

                userOffersRV = root.findViewById(R.id.userOffersRV);
                userOffersRV.setLayoutManager(new LinearLayoutManager(MyOffersFragment.super.getContext()));

                MyOffersAdapter offerAdapter = new MyOffersAdapter(offersPostedByCurrentUser);
                userOffersRV.setAdapter(offerAdapter);

                offerAdapter.setOnDeleteButtonClickListener(new MyOffersAdapter.OnDeleteButtonClickListener() {
                    @Override
                    public void onDeleteButtonClicked(int position) {
                        Offer currentOffer = offersPostedByCurrentUser.get(position);
                        int currentOfferID = offersPostedByCurrentUser.get(position).getId();
                        Property propertyToDelete = offersPostedByCurrentUser.get(position).getProperty();

                        // delete the offer
                        offerAPI.deleteOfferById(currentOfferID).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                PropertyAPI propertyAPI = retrofitService.getRetrofit().create(PropertyAPI.class);
                                propertyAPI.deletePropertyById(currentOffer.getProperty().getId()).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        Toast.makeText(getContext(), "Offer deleted successfully!", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                    }
                                });

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                            }
                        });
                    }
                });

                //editing an offer
                offerAdapter.setOnEditButtonClickListener(new MyOffersAdapter.OnEditButtonClickListener() {
                    @Override
                    public void onEditButtonClicked(int position) {
                        Intent editOfferIntent = new Intent(MyOffersFragment.super.getContext(), EditOfferActivity.class);
                        // sending the id of the offer to the Edit Activity
                        editOfferIntent.putExtra("offerId", offersPostedByCurrentUser.get(position).getId());
                        startActivity(editOfferIntent);
                    }
                });

                offerAdapter.setOnItemClickedListener(new OfferAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent viewOfferIntent = new Intent(MyOffersFragment.super.getContext(), ViewOfferActivity.class);
                        // sending the id of the offer to the View Activity
                        viewOfferIntent.putExtra("offerId", offersPostedByCurrentUser.get(position).getId());
                        startActivity(viewOfferIntent);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Offer>> call, Throwable t) {

            }
        });

        return root;
    }
}
