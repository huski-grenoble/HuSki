package com.example.huski.dataStructure;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huski.ListFragment;
import com.example.huski.MainActivity;
import com.example.huski.R;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends ArrayAdapter<cardStruct> {

    public CardAdapter(Context context, ArrayList<cardStruct> cards){
        super(context,0,cards);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final cardStruct card = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view, parent, false);
        }
        // Lookup view for data population
        final TextView cardName = (TextView) convertView.findViewById(R.id.cardName);
        ImageButton deleteBtn = (ImageButton) convertView.findViewById(R.id.deleteButton);
        ImageButton localiseBtn = (ImageButton) convertView.findViewById(R.id.localiseButton);
        // Populate the data into the template view using the data object
        cardName.setText(card.getName());

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListFragment.arrayOfCards.remove(card);
                ListFragment.adapter.notifyDataSetChanged();
                ListFragment.adapter.notifyDataSetInvalidated();
            }
        });

        localiseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListFragment.moveToLocalise();
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

}
