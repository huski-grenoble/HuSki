package com.example.huski;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.huski.dataStructure.CardAdapter;
import com.example.huski.dataStructure.cardStruct;

import java.util.ArrayList;
import java.util.List;


public class ListFragment extends Fragment {
    public static ListFragment newInstance() {
        return (new ListFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ArrayList<cardStruct> arrayOfCards = new ArrayList<cardStruct>();
        cardStruct newCard = new cardStruct("coucou");

        CardAdapter adapter = new CardAdapter(getActivity(),arrayOfCards);
        adapter.add(newCard);
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        ListView cardList = v.findViewById(R.id.cardList);

        cardList.setAdapter(adapter);

        return v;
    }

}
