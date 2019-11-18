package com.example.safe_eats;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private static final String COLOR_LOW = "#2dc937";
    private static final String COLOR_MODERATE = "#e7b416";
    private static final String COLOR_HIGH = "#cc3232";

    private List<Restaurant> list;
    private OnRestaurantClickListener onRestaurantClickListener;

    public RestaurantAdapter(List<Restaurant> list, OnRestaurantClickListener onRestaurantClickListener) {
        this.list = list;
        this.onRestaurantClickListener = onRestaurantClickListener;
    }

    public interface OnRestaurantClickListener {
        void onItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvName;
        public TextView tvAddr;
        public TextView tvInspect;

        OnRestaurantClickListener onRestaurantClickListener;

        public ViewHolder(View v, OnRestaurantClickListener onRestaurantClickListener) {
            super(v);

            tvName = v.findViewById(R.id.tvRestaurantName);
            tvAddr = v.findViewById(R.id.tvRestaurantAddress);
            tvInspect = v.findViewById(R.id.tvRestaurantCondition);
            this.onRestaurantClickListener = onRestaurantClickListener;

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onRestaurantClickListener.onItemClick(getAdapterPosition());
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View listView = inflater.inflate(R.layout.row_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(listView, onRestaurantClickListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewholder, int position) {
        Restaurant restaurant = list.get(position);

        TextView textViewName = viewholder.tvName;
        TextView textViewAddr = viewholder.tvAddr;
        TextView textViewInspection = viewholder.tvInspect;
        HazardRating hazardRating = HazardRating.NoResult;

        if(restaurant.getInspections().size() >0){
            hazardRating = restaurant.getInspections().get(0).getHazardRating();
        }

        textViewName.setText(restaurant.getName());
        textViewAddr.setText(restaurant.getAddress());
        textViewInspection.setText(hazardRating.toString());


        switch (hazardRating) {
            case Low:
                textViewInspection.setTextColor(Color.parseColor(COLOR_LOW));
                break;
            case Moderate:
                textViewInspection.setTextColor(Color.parseColor(COLOR_MODERATE));
                break;
            case NoResult:
                textViewInspection.setTextColor(Color.BLACK);
            default:
                textViewInspection.setTextColor(Color.parseColor(COLOR_HIGH));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<Restaurant> newList) {
        list = newList;
    }
}
