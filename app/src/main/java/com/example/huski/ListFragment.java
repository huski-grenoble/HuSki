package com.example.huski;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class ListFragment extends Fragment {
    ListView mListSki;

    public static ListFragment newInstance() {
        return (new ListFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_list, container, false);
        //mListSki = (ListView) v.findViewById(R.id.listSKi);
        final ArrayAdapter<cardStruct> adapter;

        return inflater.inflate(R.layout.fragment_list, container, false);
    }
}
