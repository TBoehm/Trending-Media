package com.toboehm.trendingmedia.mediaproviders;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.SearchParameters;
import com.toboehm.trendingmedia.R;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by Tobias Boehm on 30.03.2015.
 */
public class FlickrPictureProvider implements IMediaProvider<BitmapDrawable> {

    private final Flickr mFlickr;

    public FlickrPictureProvider(final Context pContext){

        mFlickr = new Flickr(pContext.getString(R.string.flickr_consumer_key), pContext.getString(R.string.flickr_consumer_secret));
    }


    @Override
    public void requestMediaForTrend(String pTrend, IMediaDownloadListener<BitmapDrawable> pListener) {


        new AsyncTask<Void,Void,>()

        final SearchParameters searchParameters = new SearchParameters();
        searchParameters.setText(pTrend);
        searchParameters.setAccuracy(Flickr.ACCURACY_WORLD);
        searchParameters.setSafeSearch(Flickr.SAFETYLEVEL_MODERATE);
        searchParameters.setSort(SearchParameters.RELEVANCE);
        try {
            searchParameters.setMedia("photos");
        } catch (FlickrException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            final PhotoList photoList = mFlickr.getPhotosInterface().search(searchParameters, 12, 1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FlickrException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
