package com.exalt.vmuseum.utilities.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.exalt.vmuseum.R;
import com.exalt.vmuseum.VMuseum;
import com.exalt.vmuseum.models.PlaceDetails;
import com.squareup.picasso.Picasso;

/**
 * Created by Abdallah on 8/1/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private Context context;

    public RecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.places_list_row_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PlaceDetails place = VMuseum.placesList.get(position);
        holder.nameView.setText(place.getName());
        if (place.getImage() != null) {
            Picasso.with(context).load(place.getImage()).resize(100, 100).into(holder.imageView);
        } else {
            Picasso.with(context).load(R.drawable.no_image).resize(100, 100).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        if (VMuseum.placesList == null) {
            return 0;
        }
        return VMuseum.placesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nameView;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            nameView = (TextView) view.findViewById(R.id.place_row_name_view);
            imageView = (ImageView) view.findViewById(R.id.place_row_image_view);
        }
    }
}
