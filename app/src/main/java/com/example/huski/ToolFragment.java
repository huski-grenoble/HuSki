package com.example.huski;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
