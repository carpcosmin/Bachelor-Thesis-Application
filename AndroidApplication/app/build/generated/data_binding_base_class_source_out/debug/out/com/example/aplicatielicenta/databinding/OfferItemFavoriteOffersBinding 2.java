// Generated by view binder compiler. Do not edit!
package com.example.aplicatielicenta.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.aplicatielicenta.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class OfferItemFavoriteOffersBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button btnDeleteFavoriteOffer;

  @NonNull
  public final CardView cardView;

  @NonNull
  public final ImageView imageIV;

  @NonNull
  public final TextView tvAddress;

  @NonNull
  public final TextView tvPrice;

  @NonNull
  public final TextView tvTitle;

  private OfferItemFavoriteOffersBinding(@NonNull LinearLayout rootView,
      @NonNull Button btnDeleteFavoriteOffer, @NonNull CardView cardView,
      @NonNull ImageView imageIV, @NonNull TextView tvAddress, @NonNull TextView tvPrice,
      @NonNull TextView tvTitle) {
    this.rootView = rootView;
    this.btnDeleteFavoriteOffer = btnDeleteFavoriteOffer;
    this.cardView = cardView;
    this.imageIV = imageIV;
    this.tvAddress = tvAddress;
    this.tvPrice = tvPrice;
    this.tvTitle = tvTitle;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static OfferItemFavoriteOffersBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static OfferItemFavoriteOffersBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.offer_item_favorite_offers, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static OfferItemFavoriteOffersBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btn_delete_favorite_offer;
      Button btnDeleteFavoriteOffer = ViewBindings.findChildViewById(rootView, id);
      if (btnDeleteFavoriteOffer == null) {
        break missingId;
      }

      id = R.id.card_view;
      CardView cardView = ViewBindings.findChildViewById(rootView, id);
      if (cardView == null) {
        break missingId;
      }

      id = R.id.imageIV;
      ImageView imageIV = ViewBindings.findChildViewById(rootView, id);
      if (imageIV == null) {
        break missingId;
      }

      id = R.id.tvAddress;
      TextView tvAddress = ViewBindings.findChildViewById(rootView, id);
      if (tvAddress == null) {
        break missingId;
      }

      id = R.id.tvPrice;
      TextView tvPrice = ViewBindings.findChildViewById(rootView, id);
      if (tvPrice == null) {
        break missingId;
      }

      id = R.id.tvTitle;
      TextView tvTitle = ViewBindings.findChildViewById(rootView, id);
      if (tvTitle == null) {
        break missingId;
      }

      return new OfferItemFavoriteOffersBinding((LinearLayout) rootView, btnDeleteFavoriteOffer,
          cardView, imageIV, tvAddress, tvPrice, tvTitle);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
