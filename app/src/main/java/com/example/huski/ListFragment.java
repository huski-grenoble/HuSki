package com.example.huski;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.huski.dataStructure.CardAdapter;
import com.example.huski.dataStructure.cardStruct;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class ListFragment extends Fragment {

    private String barcodeString = "";
    private static final String STATE_LIST = "State Adapter Data";
    private static Bundle savedState;
    Button connectionBtn;
    FloatingActionButton addBtn;
    AlertDialog.Builder popupAddSki;
    ImageView imBatterySki;
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
        if(savedState == null){
            arrayOfCards = new ArrayList<cardStruct>();
            adapter = new CardAdapter(getActivity(),arrayOfCards);
            adapter.notifyDataSetChanged();
        }
        else{
            arrayOfCards = savedState.getParcelableArrayList(STATE_LIST);
            if(getArguments().getString("uuidCard") != null){
                barcodeString = getArguments().getString("uuidCard");
                newCard();
            }
            adapter = new CardAdapter(getActivity(),arrayOfCards);
            adapter.notifyDataSetChanged();
        }

        //va chercher dans le stockage interne les cartes enregistrées.
        readData();

        View v = inflater.inflate(R.layout.fragment_list, container, false);

        cardList = v.findViewById(R.id.list);
        TextView emptyText = v.findViewById(android.R.id.empty);
        cardList.setEmptyView(emptyText);
        addBtn = (FloatingActionButton) v.findViewById(R.id.addBtn);
        imBatterySki = v.findViewById(R.id.batterySkiLvl);
        connectionBtn = v.findViewById(R.id.connectionBtn);
        mySwipeRefreshLayout =  v.findViewById(R.id.swiperefresh);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFragment addFragment = new AddFragment();
                addFragment.setTargetFragment(ListFragment.this,1);
                Intent i = new Intent(getActivity().getBaseContext(),addFragment.getClass());
                //startActivityForResult(i, 0);
                ((MainActivity) getContext()).startTransactionFragment(addFragment);
            }
        });

        connectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 0);
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
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter.isEnabled()){
            bluetoothOn();
        }
        else{
            bluetoothOff();
        }
        return v;
    }

    public void newCard() {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_add_ski, null);
        final cardStruct newCard = new cardStruct(barcodeString,barcodeString);
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
                //sauve la carte dans le stockage interne du téléphone
                writeData(newCard);
            }
        });

        popupAddSki.setView(view);
        popupAddSki.show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(mBroadcastReceiver1, filter1);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    protected void bluetoothOn() {
        connectionBtn.setBackgroundColor(getResources().getColor(R.color.colorOK));
        connectionBtn.setTextColor(Color.WHITE);
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        String deviceName = "";
        if (pairedDevices.size() > 0) {
            boolean isPaired = false;
            for (BluetoothDevice d : pairedDevices) {
                if (d.getBondState() == 12) {
                    isPaired = true;
                    deviceName = d.getName();
                }
            }
            if (isPaired == true) {
                connectionBtn.setText("Status : connected to " + deviceName);
            } else {
                connectionBtn.setText("No device found");
            }
        }
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
                connectionBtn.setText("Click here to enable bluetooth & connect the gateway");
                return false;
            }
            connectionBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            connectionBtn.setText("Status : connected");
            return true;
        }
    }

    private void bluetoothOff(){
        connectionBtn.setBackgroundColor(getResources().getColor(R.color.colorDanger));
        connectionBtn.setTextColor(Color.WHITE);
        connectionBtn.setText("Click here to enable bluetooth & connect the gateway");
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

    public void writeData(cardStruct card){
        try {
            // Creates a file in the primary external storage space of the
            // current application.
            // If the file does not exists, it is created.
            File testFile = new File(getContext().getFilesDir(), "CardsSaved.txt");
            if (!testFile.exists())
                testFile.createNewFile();

            // Adds a line to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(testFile, true /*append*/));
            writer.write(card.getName() + "♥" + card.getCheapID().toString());
            writer.newLine();
            writer.close();
            // Refresh the data so it can seen when the device is plugged in a
            // computer. You may have to unplug and replug the device to see the
            // latest changes. This is not necessary if the user should not modify
            // the files.
            MediaScannerConnection.scanFile(getContext(),
                    new String[]{testFile.toString()},
                    null,
                    null);
        } catch (IOException e) {
            Log.e("ReadWriteFile", "Unable to write to the CardsSaved.txt file.");
        }
    }

    public void readData(){
        String textFromFile = "";
        // Gets the file from the primary internal storage space of the
        // current application.
        File testFile = new File(getContext().getFilesDir(), "CardsSaved.txt");
        if (testFile != null) {
            // Reads the data from the file
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(testFile));
                String line;

                while ((line = reader.readLine()) != null) {
                    textFromFile = line.toString();
                    String arr[] = textFromFile.split("♥", 2);
                    final cardStruct newCard = new cardStruct(arr[0], arr[1]);
                    boolean bool = false;
                    for(int k = 0; k < adapter.getCount(); k++) {
                        if(arrayOfCards.get(k).getCheapID().equals(newCard.getCheapID())){
                            bool = true;
                        }
                    }
                    if(!bool){
                        adapter.add(newCard);
                    }
                }
                reader.close();
            } catch (Exception e) {
                Log.e("ReadWriteFile", "Unable to read the CardsSaved.txt file.");
            }
        }
    }

    private void onCompletion(){
        mySwipeRefreshLayout.setRefreshing(false);
    }
}
