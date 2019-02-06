package com.example.huski;

import android.nfc.tech.NfcBarcode;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;


public class AddFragment extends Fragment {

    public static AddFragment newInstance() {
        return (new AddFragment());
    }

    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_add, container, false);
        return v;
    }
}
