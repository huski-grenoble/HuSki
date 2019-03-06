package com.example.huski;

import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.huski.dataStructure.CardAdapter;
import com.example.huski.dataStructure.cardStruct;
import com.example.huski.dataStructure.gpsStruct;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;


public class ListFragment extends Fragment {

    //Variable Definitions
    private String barcodeString = "";
    private static final String STATE_LIST = "State Adapter Data";
    private static final String TAG = "debugging";
    private static Bundle savedState;
    private Button connectionBtn;
    private FloatingActionButton addBtn;
    private AlertDialog.Builder popupAddSki;
    private ImageView imBatteryGW,imBatterySki;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    public static ArrayList<cardStruct> arrayOfCards;
    public static CardAdapter adapter;
    private ListView cardList;
    private BluetoothAdapter mBluetoothAdapter;
    public static Peripherique periph;
    public int batteryGW = 0;
    public static Boolean isConnectedToGW = false;
    public BluetoothDevice bDevice;

    public static ListFragment newInstance() {
        return (new ListFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Restore saving data
        if(savedState == null){
            arrayOfCards = new ArrayList<cardStruct>();
            adapter = new CardAdapter(getActivity(),arrayOfCards);
            adapter.notifyDataSetChanged();
        }
        else{
            arrayOfCards = savedState.getParcelableArrayList(STATE_LIST);
            //If  barecode has been scanned
           if(getArguments()!= null){
                barcodeString = getArguments().getString("uuidCard");
                newCard();
                setArguments(null);
                Log.d(TAG,"arguments:"+getArguments());
            }
            adapter = new CardAdapter(getActivity(),arrayOfCards);
            adapter.notifyDataSetChanged();
        }

        //read saved cards from the intern file (restore data)
        readData();

        View v = inflater.inflate(R.layout.fragment_list, container, false);

        //link to XML
        cardList = v.findViewById(R.id.list);
        TextView emptyText = v.findViewById(android.R.id.empty);
        cardList.setEmptyView(emptyText);
        addBtn = (FloatingActionButton) v.findViewById(R.id.addBtn);
        //testBtn = v.findViewById(R.id.testBtn);
        imBatterySki = v.findViewById(R.id.batterySkiLvl);
        imBatteryGW = v.findViewById(R.id.batteryGWLvl);
        connectionBtn = v.findViewById(R.id.connectionBtn);
        mySwipeRefreshLayout =  v.findViewById(R.id.swiperefresh);

        String lvl2 = "battery" + this.batteryGW;
        imBatteryGW.setImageResource(getContext().getResources().getIdentifier(lvl2, "drawable", "com.example.huski"));
        if(imBatteryGW.getDrawable().getConstantState() == getContext().getResources().getDrawable(R.drawable.battery0).getConstantState()){
            Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
            animation.setDuration(500); //1 second duration for each animation cycle
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
            animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
            imBatteryGW.startAnimation(animation); //to start animation
        }
        //add a card -> link to AddFragment
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFragment addFragment = new AddFragment();
                ((MainActivity) getContext()).startTransactionFragment(addFragment);
            }
        });


        //Open Bluetooth settings
        connectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 0);
            }
        });

        //Refresh the page on vertical swipe
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        adapter.notifyDataSetChanged();
                        onCompletion();
                    }
                }
        );

        //Set list & bluetooth adapter
        cardList.setAdapter(adapter);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Check Bluetooth
        if(mBluetoothAdapter.isEnabled()){
            bluetoothOn();
        }
        else{
            bluetoothOff();
        }
        return v;
    }

    /**
     * Creates a new card in the list with a popup
     * Allow user to name the card
     */
    public void newCard() {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_add_ski, null);
        final cardStruct newCard = new cardStruct(barcodeString,barcodeString,0);
        final EditText nameInput = (EditText) view.findViewById(R.id.initName);
        //Create Popup to rename the card
        popupAddSki = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom));
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

    /**
     * Instance of bluetooth listener
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Bluetooth Listener
        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        if(getActivity() != null)
            getActivity().registerReceiver(mBroadcastReceiver1, filter1);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Checks which bluetooth devices are available
     * Changes design if any device is connected to the mobile
     * Starts connection with gateway ( instance of bluetooth periph)
     */
    protected void bluetoothOn() {
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        String deviceName = "";
        if (pairedDevices.size() > 0) {
            boolean isPaired = false;
            for (BluetoothDevice d : pairedDevices) {
                if (d.getBondState() == BluetoothDevice.BOND_BONDED) {
                    isPaired = true;
                    deviceName = d.getName();
                    this.bDevice = d;
                }
            }
            if (isPaired == true) {
                connectionBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                connectionBtn.setTextColor(Color.WHITE);
                connectionBtn.setText("Status : Paired with " + deviceName);
                periph = new Peripherique(this.bDevice, mHandler);
                periph.connecter();
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

    /**
     * Called when bluetooth is turned off
     * Changes design of bluetooth button
     */
    private void bluetoothOff(){
        connectionBtn.setBackgroundColor(getResources().getColor(R.color.colorDanger));
        connectionBtn.setTextColor(Color.WHITE);
        connectionBtn.setText("Click here to enable bluetooth & connect the gateway");
        if(periph != null){
            periph.deconnecter();
        }
    }

    //Bluetooth listener
    /**
     * Blueooth listener checks state of bluetooth
     * Trigger appropriate function
     */
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

    /**
     * Saves data (ski card) into a local file of the app to allow restoration
     * @param card the card to save
     */
    public void writeData(cardStruct card){
        try {
            if(isConnectedToGW) {
                periph.envoyer(card.getChipId() + "2");
            }
            // Creates a file in the primary external storage space of the
            // current application.
            // If the file does not exists, it is created.
            File testFile = new File(getContext().getFilesDir(), "CardsSaved.txt");
            if (!testFile.exists())
                testFile.createNewFile();

            // Adds a line to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(testFile, true /*append*/));
            writer.write(card.getName() + "♥" + card.getChipId().toString());
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

    /**
     * Restores data from the local file to update cardList
     */
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
                    textFromFile = line;
                    String arr[] = textFromFile.split("♥", 2);
                    final cardStruct newCard = new cardStruct(arr[0], arr[1], 0);
                    boolean bool = false;
                    for(int k = 0; k < adapter.getCount(); k++) {
                        if(arrayOfCards.get(k).getChipId().equals(newCard.getChipId())){
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

    /**
     * Refreshes fragment on vertical swipe
     */
    private void onCompletion(){
        mySwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Handles message send by the bluetooth periph
     *
     */
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.arg1){
                case Peripherique.CODE_CONNEXION:
                    Log.d("Handler", "connected to " + msg.getData().toString());
                    connectionBtn.setBackgroundColor(getResources().getColor(R.color.colorOK));
                    connectionBtn.setTextColor(Color.WHITE);
                    connectionBtn.setText("Status : connection opened with " + bDevice.getName());
                    isConnectedToGW = true;
                    break;
                case Peripherique.CODE_DECONNEXION:
                    isConnectedToGW = false;
                    Log.d("Handler", "Disconnected");
                    break;
                case Peripherique.CODE_RECEPTION:
                    //Parse data received
                    Log.d("Handler", "Message received : " + msg.getData().toString());
                    parseData(msg.obj.toString());
                    break;
            }
        }
    };

    /**
     * Parses data received and updates the current card according so
     * @param msg the message received
     */
    public void parseData(String msg){
        String data[] = msg.split(" ", 7);
        Boolean isInList = false;
        cardStruct foundCard = null;
        for(int k = 0; k < adapter.getCount(); k++) {
            if(arrayOfCards.get(k).getChipId().equals(data[0])){
                isInList = true;
                foundCard = arrayOfCards.get(k);
                break;
            }
        }
        if(isInList && data.length == 7){
            foundCard.setGps(new gpsStruct(Double.parseDouble(data[2]), Double.parseDouble(data[1]), Double.parseDouble(data[3])));
            foundCard.setBatteryLvl(Integer.parseInt(data[4]));
            this.batteryGW = Integer.parseInt(data[5]);
            String lvl2 = "battery" + this.batteryGW;
            imBatteryGW.setImageResource(getContext().getResources().getIdentifier(lvl2, "drawable", "com.example.huski"));
            if(imBatteryGW.getDrawable().getConstantState() == getContext().getResources().getDrawable(R.drawable.battery0).getConstantState()){
                Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
                animation.setDuration(500); //1 second duration for each animation cycle
                animation.setInterpolator(new LinearInterpolator());
                animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
                animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
                imBatteryGW.startAnimation(animation); //to start animation
            }
            else{
                imBatteryGW.clearAnimation();
            }
            foundCard.setRSSI(Integer.parseInt(data[6]));
        }
    }

    public static void sendFromList(String data){
        if(isConnectedToGW) {
            periph.envoyer(data);
        }
    }
}
