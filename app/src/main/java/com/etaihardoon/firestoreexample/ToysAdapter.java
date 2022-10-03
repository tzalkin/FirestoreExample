package com.etaihardoon.firestoreexample;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


public class ToysAdapter extends ArrayAdapter<Toy> {
    private Context context;
    private ArrayList<Toy> toyArrayList;

    public ToysAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Toy> toyArrayList) {
        super(context, resource, toyArrayList);

        this.context = context;
        this.toyArrayList = toyArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.toy_row, null, false);
        Toy toy = toyArrayList.get(position);

        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvPrice = view.findViewById(R.id.tvPrice);

        tvName.setText(toy.getName());
        tvPrice.setText(toy.getPrice() + "$");

        return view;
    }
}
