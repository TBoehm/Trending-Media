package com.toboehm.trendingmedia.activities.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.toboehm.trendingmedia.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MediaFragment extends Fragment {

    // UI elements
    @InjectView(R.id.mf_picture_grid) GridView mPictureGrid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment and init ButterKnife
        final View view =  inflater.inflate(R.layout.fragment_media, container, false);
        ButterKnife.inject(this, view);

        return view;
    }
}
