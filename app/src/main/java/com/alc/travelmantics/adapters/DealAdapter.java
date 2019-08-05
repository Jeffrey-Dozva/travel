package com.alc.travelmantics.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alc.travelmantics.DealActivity;
import com.alc.travelmantics.R;
import com.alc.travelmantics.ViewDeals;
import com.alc.travelmantics.models.Deal;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.ViewHolder> {

  private ArrayList<Deal> pDeals;

  private Context pContext;

  public DealAdapter(Context context, ArrayList<Deal> deals) {
    pDeals = deals;
    pContext = context;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.deal, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, final int position) {

    holder.deal_title.setText(pDeals.get(position).getTitle());
    holder.deal_description.setText(pDeals.get(position).getDescription());
    holder.deal_price.setText(pDeals.get(position).getPrice());
    holder.deal_container.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(pContext, DealActivity.class);
        intent.putExtra("deal", pDeals.get(position));
        pContext.startActivity(intent);
      }
    });

    if (pDeals.get(position).getImageUrl() != null) {
      Uri uri = Uri.parse(pDeals.get(position).getImageUrl());

      holder.deal_image.setImageURI(uri.toString());

    }
  }

  @Override
  public int getItemCount() {
    return pDeals.size();
  }


  public class ViewHolder extends RecyclerView.ViewHolder {

    MaterialCardView deal_container;
    TextView deal_title, deal_description, deal_price;
    SimpleDraweeView deal_image;

    public ViewHolder(View itemView) {
      super(itemView);
      deal_container = itemView.findViewById(R.id.deal_container);
      deal_title = itemView.findViewById(R.id.deal_title);
      deal_description = itemView.findViewById(R.id.deal_description);
      deal_price = itemView.findViewById(R.id.deal_price);
      deal_image = itemView.findViewById(R.id.deal_image);

    }
  }

}
