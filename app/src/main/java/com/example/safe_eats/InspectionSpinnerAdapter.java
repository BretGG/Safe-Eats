package com.example.safe_eats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class InspectionSpinnerAdapter extends BaseAdapter {

    Context context;
    ArrayList<Inspection> inspections;
    LayoutInflater inflater;

    public InspectionSpinnerAdapter(Context context, ArrayList<Inspection> inspections) {
        this.context = context;
        this.inspections = inspections;

        inflater = (LayoutInflater.from(context));
    }


    @Override
    public int getCount() {
        return inspections.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.spinner_row_layout, null);
        TextView tvYear = view.findViewById(R.id.tvDetailYear);
        TextView tvHazard = view.findViewById(R.id.tvDetailHazard);

        tvYear.setText(inspections.get(i).getInspectionDate().toString().substring(24));
        tvHazard.setText(inspections.get(i).getHazardRating().toString());

        return view;
    }
}
