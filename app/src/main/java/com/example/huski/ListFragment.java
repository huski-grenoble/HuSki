package com.example.huski;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.huski.dataStructure.CardAdapter;
import com.example.huski.dataStructure.cardStruct;

import java.util.ArrayList;
import java.util.List;


public class ListFragment extends Fragment {
    public static ListFragment newInstance() {
        return (new ListFragment());
    }
    Button addBtn;
    public static ArrayList<cardStruct> arrayOfCards;
    public static CardAdapter adapter;
    ListView cardList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey("key")){
            arrayOfCards = new ArrayList<cardStruct>();
        }
        else{
            arrayOfCards = savedInstanceState.getParcelableArrayList("key");
        }

        adapter = new CardAdapter(getActivity(),arrayOfCards,new FindFragment());
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        cardList = v.findViewById(R.id.cardList);
        addBtn = v.findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardStruct newCard = new cardStruct("coucou");
                adapter.add(newCard);
            }
        });

        cardList.setAdapter(adapter);
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("key",arrayOfCards);
        super.onSaveInstanceState(savedInstanceState);
    }
}
