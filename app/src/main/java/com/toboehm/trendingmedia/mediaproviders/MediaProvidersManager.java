package com.toboehm.trendingmedia.mediaproviders;

import android.content.Context;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A manager class for managing all implementations of {@link AbsMediaProvider}.
 * This way a media content consumer does not need to manage media providers on its own but can query
 * the {@link com.toboehm.trendingmedia.mediaproviders.MediaProvidersManager} for media content based
 * on trends.
 *
 * At the moment it supports only one media content consumer.
 *
 * Created by Tobias Boehm on 16.04.2015.
 */
public class MediaProvidersManager implements AbsMediaProvider.IMediaURIsDownloadListener {

    private static final int MAX_PICTURES_PER_QUERY = 32;

    private final Set<AbsMediaProvider> mMediaProviders = new HashSet<>();
    private final AbsMediaProvider.IMediaURIsDownloadListener mMediaContentConsumer;


    public MediaProvidersManager(final Context pContext, final AbsMediaProvider.IMediaURIsDownloadListener pMediaContentConsumer){

        mMediaContentConsumer = pMediaContentConsumer;

        initMediaProviders(pContext);
    }

    private void initMediaProviders(final Context pContext) {

        // TODO implement/add more media providers
        mMediaProviders.add(new FlickrPictureProvider(pContext));
    }


    public void asyncRequestMedia(final String pTrend){

        for(AbsMediaProvider mediaProvider : mMediaProviders){

            mediaProvider.asyncRequestMediaForTrend(pTrend, this, MAX_PICTURES_PER_QUERY);
        }
    }

    public void cancelMediaRequests(final String pTrend){

        for(AbsMediaProvider mediaProvider : mMediaProviders){

            mediaProvider.cancelMediaRequestsForTrend(pTrend);
        }
    }

    public void cancelAllMediaRequests(){

        for(AbsMediaProvider mediaProvider : mMediaProviders){

            mediaProvider.cancelAllMediaRequests();
        }
    }

    @Override
    public void onMediaURIsDownloaded(final String pTrend, final ArrayList<Uri> pMedia) {

        mMediaContentConsumer.onMediaURIsDownloaded(pTrend, pMedia);
    }
}
