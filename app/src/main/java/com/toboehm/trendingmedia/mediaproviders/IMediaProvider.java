package com.toboehm.trendingmedia.mediaproviders;

/**
 * Created by Tobias Boehm on 30.03.2015.
 */
public interface IMediaProvider<Media> {

    public void requestMediaForTrend(final String pTrend, final IMediaDownloadListener<Media> pListener);
}
