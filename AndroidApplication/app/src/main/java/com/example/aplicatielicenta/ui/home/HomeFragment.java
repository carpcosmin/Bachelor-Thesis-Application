package com.example.aplicatielicenta.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.aplicatielicenta.EditOfferActivity;
import com.example.aplicatielicenta.IntroActivity;
import com.example.aplicatielicenta.MainActivity;
import com.example.aplicatielicenta.R;
import com.example.aplicatielicenta.ViewOfferActivity;
import com.example.aplicatielicenta.adapters.OfferAdapter;
import com.example.aplicatielicenta.entities.Offer;
import com.example.aplicatielicenta.entities.User;
import com.example.aplicatielicenta.retrofit.OfferAPI;
import com.example.aplicatielicenta.retrofit.RetrofitService;
import com.example.aplicatielicenta.retrofit.UserAPI;
import com.example.aplicatielicenta.ui.myoffers.MyOffersFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        tabLayout = root.findViewById(R.id.mainActivityTabLayout);
        viewPager = root.findViewById(R.id.mainActivityViewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Rent"));
        tabLayout.addTab(tabLayout.newTab().setText("Buy"));

        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        return new FragmentRent();
                    case 1:
                        return new FragmentBuy();
                    default:
                        return null;

                }
            }

            @Override
            public int getCount() {
                return tabLayout.getTabCount();
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return root;
    }
}