package com.example.huski;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BluetoothConnectionReceiver extends BroadcastReceiver {
    public BluetoothConnectionReceiver(){
        //No initialisation code needed
    }

    @Override
    public void onReceive(Context context, Intent intent){
        Toast.makeText(context, "BIITE BORDEL DE MERDE", Toast.LENGTH_SHORT).show();
        if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(intent.getAction())){

        }
        else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(intent.getAction())){
        }
    }
}
