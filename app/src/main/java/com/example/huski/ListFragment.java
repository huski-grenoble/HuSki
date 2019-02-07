package com.example.huski;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huski.dataStructure.CardAdapter;
import com.example.huski.dataStructure.cardStruct;

import java.util.ArrayList;
import java.util.List;


public class ListFragment extends Fragment {

    private static final String STATE_LIST = "State Adapter Data";
    private static Bundle savedState;
    Button addBtn,connectionBtn;
    AlertDialog.Builder popupAddSki;
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

        if(savedState == null){
            arrayOfCards = new ArrayList<cardStruct>();
            adapter = new CardAdapter(getActivity(),arrayOfCards,new FindFragment());
            adapter.notifyDataSetChanged();

        }
        else{
            arrayOfCards = savedState.getParcelableArrayList(STATE_LIST);
            adapter = new CardAdapter(getActivity(),arrayOfCards,new FindFragment());
            adapter.notifyDataSetChanged();
        }

        View v = inflater.inflate(R.layout.fragment_list, container, false);

        cardList = v.findViewById(R.id.cardList);
        addBtn = v.findViewById(R.id.addBtn);
        connectionBtn = v.findViewById(R.id.connectionBtn);
        mySwipeRefreshLayout =  v.findViewById(R.id.swiperefresh);
        isConnected = isBluetoothActivated();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_add_ski, null);
                final cardStruct newCard = new cardStruct("coucou");
                final EditText nameInput = (EditText) view.findViewById(R.id.initName);
                popupAddSki = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom));
                //popupAddSki = new AlertDialog.Builder(getContext());
                popupAddSki.create();
                popupAddSki.setTitle("Enter a name");
                popupAddSki.setCancelable(false);
                popupAddSki.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                popupAddSki.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newCard.setName(nameInput.getText().toString());
                        ListFragment.adapter.notifyDataSetChanged();
                        adapter.add(newCard);
                    }
                });

                popupAddSki.setView(view);
                popupAddSki.show();

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
        super.onSaveInstanceState(savedInstanceState);
    }

    public Bundle saveState()
    {
        // save whatever you would have in onSaveInstanceState() and return a bundle with the saved data
        Bundle savedState = new Bundle();
        savedState.putParcelableArrayList(STATE_LIST,getList());
        return savedState;

    }

    public ArrayList<cardStruct> getList() {
        return new ArrayList<cardStruct>(arrayOfCards);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        savedState = saveState();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
