package com.toboehm.trendingmedia.activities.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.squareup.picasso.Picasso;
import com.toboehm.trendingmedia.R;
import com.toboehm.trendingmedia.mediaproviders.AbsMediaProvider;
import com.toboehm.trendingmedia.mediaproviders.MediaProvidersManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MediaFragment extends Fragment implements AbsMediaProvider.IMediaURIsDownloadListener {

    private static final String TRENDS_URIS_STATE = "TRENDS_URIS_STATE";


    // UI elements
    @InjectView(R.id.mf_picture_grid) GridView mPictureGrid;
    private UriArrayAdapter mURIarrayAdapter;

    // Model
    /**
     * A multimap for tracing media content related to the trends used to query them.
     */
    private final Multimap<String, Uri> mTrendsPictures = Multimaps.synchronizedMultimap(HashMultimap.<String, Uri>create());

    // Utils
    private MediaProvidersManager mMediaProvidersManager;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mURIarrayAdapter = new UriArrayAdapter(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init media provider manager
        mMediaProvidersManager = new MediaProvidersManager(getActivity(), this);


        // if there is a saved state which contains a trends -> content URIs map; load that state
        if((savedInstanceState != null) && savedInstanceState.containsKey(TRENDS_URIS_STATE)){

            final HashMap<String,Collection<Uri>> trendsMediaURIs = (HashMap<String, Collection<Uri>>) savedInstanceState.getSerializable(TRENDS_URIS_STATE);
            for(String trend : trendsMediaURIs.keySet()){

                mTrendsPictures.putAll(trend, trendsMediaURIs.get(trend));
            }

            // filter duplicate URIs and update GUI array adapter with them
            final HashSet<Uri> filteredURIs = new HashSet<>(mTrendsPictures.values());
            mURIarrayAdapter.addAll(filteredURIs);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        // save the trends -> content URIs state in form of a simple hashmap
        final HashMap<String,Collection<Uri>> trendsPicturesMap = new HashMap(mTrendsPictures.asMap());
        outState.putSerializable(TRENDS_URIS_STATE,trendsPicturesMap);

        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment and init ButterKnife
        final View view =  inflater.inflate(R.layout.fragment_media, container, false);
        ButterKnife.inject(this, view);

        // Init Picture Grid
        mPictureGrid.setAdapter(mURIarrayAdapter);

        return view;
    }

    public void addPicturesForTrend(final String pTrend) {

        mMediaProvidersManager.asyncRequestMedia(pTrend);
    }

    public void removePicturesForTrend(final String pTrend) {

        mMediaProvidersManager.cancelMediaRequests(pTrend);

        // remove media URIs related to that trend from model and remove them from the adapter as well
        for(Uri mediaURI : mTrendsPictures.removeAll(pTrend)){

            mURIarrayAdapter.remove(mediaURI);
        }
    }

    public void removeAllPictures() {

        mMediaProvidersManager.cancelAllMediaRequests();

        mTrendsPictures.clear();
        mURIarrayAdapter.clear();
    }

    @Override
    public void onMediaURIsDownloaded(final String pTrend, final ArrayList<Uri> pMediaURIs) {

        mTrendsPictures.putAll(pTrend, pMediaURIs);
        // TODO filter URIs which are already managed by the array adapter
        mURIarrayAdapter.addAll(pMediaURIs);
    }

    private class UriArrayAdapter extends ArrayAdapter<Uri> {

        public UriArrayAdapter(final Context pContext) {
            super(pContext, R.layout.support_simple_spinner_dropdown_item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ImageView view = (ImageView) convertView;
            if (view == null) {
                view = new ImageView(getContext());
            }

            final Uri mediaURI = getItem(position);
            Picasso.with(getContext()).load(mediaURI).placeholder(R.mipmap.ic_image_placeholder)
                    .error(R.mipmap.ic_image_load_error).resize(180, 180).centerCrop().into(view);

            return view;
        }
    }
}
