package com.example.huski;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ToolFragment extends Fragment {

    public static ToolFragment newInstance() {
        return ( new ToolFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tool, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        Button settingBtn = view.findViewById(R.id.settingButton);

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 0);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
