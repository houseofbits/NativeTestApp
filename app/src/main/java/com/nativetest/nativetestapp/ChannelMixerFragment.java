package com.nativetest.nativetestapp;

import android.content.Context;

import android.os.Bundle;
import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChannelMixerFragment extends Fragment {

    public ChannelMixerFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ChannelMixerFragment newInstance(String param1, String param2) {
        ChannelMixerFragment fragment = new ChannelMixerFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.channel_mixer, container, false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
