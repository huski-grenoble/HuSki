package com.example.huski;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
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

    Button addBtn,connectionBtn;
    SwipeRefreshLayout mySwipeRefreshLayout;
    public static ArrayList<cardStruct> arrayOfCards;
    public static CardAdapter adapter;
    ListView cardList;
    BluetoothAdapter mBluetoothAdapter;
    private boolean isConnected;

    public static ListFragment newInstance() {
        return (new ListFragment());
    }

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
        connectionBtn = v.findViewById(R.id.connectionBtn);
        mySwipeRefreshLayout =  v.findViewById(R.id.swiperefresh);
        isConnected = isBluetoothActivated();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardStruct newCard = new cardStruct("coucou");
                adapter.add(newCard);
            }
        });
        connectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
            }
        });

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        isConnected = isBluetoothActivated();
                        adapter.notifyDataSetChanged();
                        onCompletion();
                    }
                }
        );

        cardList.setAdapter(adapter);
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("key",arrayOfCards);
        super.onSaveInstanceState(savedInstanceState);
    }

    protected boolean isBluetoothActivated(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(getActivity(), "you cannot use the application", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                // Bluetooth is not enable
                connectionBtn.setBackgroundColor(0xFFFF000);
                connectionBtn.setText("Enable bluetooth | connect the gateway");
                return false;
            }
            connectionBtn.setBackgroundColor(0xFF00FF00);
            connectionBtn.setText("Status : connected");
            return true;
        }
    }

    private void onCompletion(){
        mySwipeRefreshLayout.setRefreshing(false);
    }
}
