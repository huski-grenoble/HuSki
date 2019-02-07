package com.example.huski.dataStructure;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huski.FindFragment;
import com.example.huski.ListFragment;
import com.example.huski.MainActivity;
import com.example.huski.R;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends ArrayAdapter<cardStruct> {
    //Variable definition
    Activity activity;
    FindFragment findFragment;
    AlertDialog.Builder dialog;
    ImageButton localiseBtn,deleteBtn;
    TextView cardName,uuid;

    public CardAdapter(Activity activity, ArrayList<cardStruct> cards,FindFragment findFragment){
        super(activity,0,cards);
        this.activity = activity;
        this.findFragment = findFragment;
    }

    @Override
    public View getView(final int position,View convertView, ViewGroup parent) {
        // Get the data item for this position
        final cardStruct card = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view, parent, false);
        }
        // Link to XML
        cardName = convertView.findViewById(R.id.cardName);
        uuid = convertView.findViewById(R.id.uuid);
        deleteBtn = convertView.findViewById(R.id.deleteButton);
        localiseBtn =  convertView.findViewById(R.id.localiseButton);
        // set cardName
        cardName.setText(card.getName());
        uuid.setText(card.getUuid().toString());

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListFragment.arrayOfCards.remove(card);
                ListFragment.adapter.notifyDataSetChanged();
                ListFragment.adapter.notifyDataSetInvalidated();
            }
        });

        localiseBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ((MainActivity) getContext()).startTransactionFragment(new FindFragment());
            }
        });

        //Alert to edit text

        cardName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final View view = LayoutInflater.from(getContext()).inflate(R.layout.popup, null);
                final EditText renameInput = (EditText) view.findViewById(R.id.rename);
                dialog = new AlertDialog.Builder(getContext());
                dialog.create();
                dialog.setTitle("Edit the name");

                dialog.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        card.setName(renameInput.getText().toString());
                        Toast.makeText(activity, "change"+card.getName(), Toast.LENGTH_SHORT).show();
                        cardName.setText(card.getName());
                        ListFragment.adapter.notifyDataSetChanged();
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setView(view);
                dialog.show();
                return false;
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

}
