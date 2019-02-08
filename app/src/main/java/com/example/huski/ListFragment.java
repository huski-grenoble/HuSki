package com.example.huski;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huski.dataStructure.CardAdapter;
import com.example.huski.dataStructure.cardStruct;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class ListFragment extends Fragment {

    private static final String TAG = "MyActivity";

    Button addBtn,connectionBtn;
    AlertDialog.Builder popupAddSki;
    SwipeRefreshLayout mySwipeRefreshLayout;
    public static ArrayList<cardStruct> arrayOfCards;
    public static CardAdapter adapter;
    ListView cardList;
    BluetoothAdapter mBluetoothAdapter;

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

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_add_ski, null);
                final cardStruct newCard = new cardStruct("coucou");
                final EditText nameInput = (EditText) view.findViewById(R.id.initName);
                popupAddSki = new AlertDialog.Builder(getContext());
                popupAddSki.create();
                popupAddSki.setTitle("Enter a name");

                popupAddSki.setPositiveButton("Name", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newCard.setName(nameInput.getText().toString());
                        ListFragment.adapter.notifyDataSetChanged();
                        adapter.add(newCard);
                    }
                });
                popupAddSki.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
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
                        adapter.notifyDataSetChanged();
                        onCompletion();
                    }
                }
        );

        cardList.setAdapter(adapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(mBroadcastReceiver1, filter1);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("key",arrayOfCards);
        super.onSaveInstanceState(savedInstanceState);
    }

    protected void bluetoothOn(){
        connectionBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary) );
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        String deviceName = "";
        if (pairedDevices.size() > 0) {
            boolean isPaired = false;
            for (BluetoothDevice d: pairedDevices) {
                if(d.getBondState() == 12){
                    isPaired = true;
                    deviceName = d.getName();
                }
            }
            if(isPaired == true){
                connectionBtn.setText("Status : connected"+deviceName);
            }
            else{
                connectionBtn.setText("None device");
            }
        }
    }

    private void bluetoothOff(){
        connectionBtn.setBackgroundColor(0xFFFF000);
        connectionBtn.setText("Enable bluetooth | connect the gateway");
    }

    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        bluetoothOff();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                    case BluetoothAdapter.STATE_ON:
                        bluetoothOn();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }

            }
        }
    };




    private void onCompletion(){
        mySwipeRefreshLayout.setRefreshing(false);
    }
}
