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

import com.squareup.picasso.Picasso;
import com.toboehm.trendingmedia.R;
import com.toboehm.trendingmedia.mediaproviders.FlickrPictureProvider;
import com.toboehm.trendingmedia.mediaproviders.IMediaProvider;
import com.toboehm.trendingmedia.mediaproviders.IMediaURIsDownloadListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MediaFragment extends Fragment implements IMediaURIsDownloadListener {

    // UI elements
    @InjectView(R.id.mf_picture_grid) GridView mPictureGrid;
    private UriArrayAdapter mURIarrayAdapter;

    // Model
    private final ArrayList<IMediaProvider> mMediaProviders = new ArrayList<>();
    private final List<Uri> mPictureURIs = Collections.synchronizedList(new ArrayList<Uri>());


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mURIarrayAdapter = new UriArrayAdapter(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // add media providers
        mMediaProviders.add(new FlickrPictureProvider(getActivity()));
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

        for(IMediaProvider mediaProvider : mMediaProviders){

            mediaProvider.asyncRequestMediaForTrend(pTrend, this);
        }
    }

    public void removePicturesForTrend(final String pTrend) {

        for(IMediaProvider mediaProvider : mMediaProviders){

            mediaProvider.cancelMediaRequestsForTrend(pTrend);
        }

        // TODO remove specific pictures
        mURIarrayAdapter.notifyDataSetChanged();
    }

    public void removeAllPictures() {

        for(IMediaProvider mediaProvider : mMediaProviders){

            mediaProvider.cancelAllMediaRequests();
        }

        mPictureURIs.clear();
        mURIarrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMediaURIsDownloaded(String pTrend, ArrayList<Uri> pMedia) {

        // TODO save pictures based on trends
        mPictureURIs.addAll(pMedia);
        mURIarrayAdapter.notifyDataSetChanged();
    }

    private class UriArrayAdapter extends ArrayAdapter<Uri> {

        public UriArrayAdapter(final Context pContext) {
            super(pContext, R.layout.support_simple_spinner_dropdown_item, MediaFragment.this.mPictureURIs);
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
