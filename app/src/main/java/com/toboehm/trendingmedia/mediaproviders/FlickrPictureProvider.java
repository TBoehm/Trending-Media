package com.toboehm.trendingmedia.mediaproviders;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.SearchParameters;
import com.toboehm.trendingmedia.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tobias Boehm on 30.03.2015.
 */
public class FlickrPictureProvider implements IMediaProvider {

    private static final int MAX_PICTURES_PER_QUERY = 512;


    private final Map<String, AsyncTask<Void,Void,ArrayList<Uri>>> mAsyncMediaRequestTasks = Collections.synchronizedMap(new HashMap<String, AsyncTask<Void, Void, ArrayList<Uri>>>());
    private final Flickr mFlickr;


    public FlickrPictureProvider(final Context pContext){

        mFlickr = new Flickr(pContext.getString(R.string.flickr_consumer_key), pContext.getString(R.string.flickr_consumer_secret));
    }


    @Override
    public synchronized void asyncRequestMediaForTrend(final String pTrend, final IMediaURIsDownloadListener pListener) {

        // check if there is already a running task for that trend.
        if(mAsyncMediaRequestTasks.containsKey(pTrend)){

            // if yes -> do nothing/let the running task finish
            return;

        }else{

            // if not -> create and start a new task
            final AsyncTask<Void,Void,ArrayList<Uri>> asyncMediaRequest = new AsyncTask<Void, Void, ArrayList<Uri>>() {

                @Override
                protected ArrayList<Uri> doInBackground(Void... params) {

                    final ArrayList<Uri> mediaURIs = new ArrayList<>();

                    try {
                        final SearchParameters searchParameters = new SearchParameters();
                        searchParameters.setText(pTrend);
                        searchParameters.setAccuracy(Flickr.ACCURACY_WORLD);
                        searchParameters.setSafeSearch(Flickr.SAFETYLEVEL_MODERATE);
                        searchParameters.setSort(SearchParameters.RELEVANCE);
                        searchParameters.setMedia("photos");

                        final PhotoList photoList = mFlickr.getPhotosInterface().search(searchParameters, MAX_PICTURES_PER_QUERY, 1);

                        // extract URIs from photoList
                        for(int pos = 0; pos < photoList.size(); pos++){

                            mediaURIs.add(Uri.parse(photoList.get(pos).getMediumUrl()));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (FlickrException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return mediaURIs;
                }

                @Override
                protected void onPostExecute(final ArrayList<Uri> pMediaURIs) {

                    pListener.onMediaURIsDownloaded(pTrend, pMediaURIs);

                    // removes itself from request task pool
                    mAsyncMediaRequestTasks.remove(pTrend);
                }
            };

            // remember task
            mAsyncMediaRequestTasks.put(pTrend, asyncMediaRequest);

            // start task
            asyncMediaRequest.execute();
        }
    }

    @Override
    public void cancelMediaRequestsForTrend(String pTrend) {

        if(mAsyncMediaRequestTasks.containsKey(pTrend)){

            mAsyncMediaRequestTasks.get(pTrend).cancel(true);

            mAsyncMediaRequestTasks.remove(pTrend);
        }
    }

    @Override
    public void cancelAllMediaRequests() {

        for(String trend : mAsyncMediaRequestTasks.keySet()){

            cancelMediaRequestsForTrend(trend);
        }
    }
}
