package com.example.huski.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.huski.R;

public class LocaliseFragment extends Fragment {
        public static com.example.huski.Fragments.LocaliseFragment newInstance() {
            return (new com.example.huski.Fragments.LocaliseFragment());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_localise, container, false);
        }
}
