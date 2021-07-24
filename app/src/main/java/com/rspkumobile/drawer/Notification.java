package com.rspkumobile.drawer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rspkumobile.R;

public class Notification extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.drawer_notification, container, false);

        changeFrame();

        return view;
    }

    public void changeFrame(){

        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container_notif);

        fm.beginTransaction()
                .add(R.id.container_notif, new com.rspkumobile.fragment.Notification())
                .commit();
    }
}