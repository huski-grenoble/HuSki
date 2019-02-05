package com.example.huski.dataStructure;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.huski.R;

import java.util.ArrayList;

public class CardAdapter extends ArrayAdapter<cardStruct> {

    public CardAdapter(Context context, ArrayList<cardStruct> cards){
        super(context,0,cards);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        cardStruct card = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.cardName);
        // Populate the data into the template view using the data object
        tvName.setText(card.getName());
        // Return the completed view to render on screen
        return convertView;
    }

}
