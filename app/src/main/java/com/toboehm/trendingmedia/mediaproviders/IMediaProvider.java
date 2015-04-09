package com.toboehm.trendingmedia.mediaproviders;

/**
 * Created by Tobias Boehm on 30.03.2015.
 */
public interface IMediaProvider {

    public void asyncRequestMediaForTrend(final String pTrend, final IMediaURIsDownloadListener pListener);

    public void cancelMediaRequestsForTrend(final String pTrend);

    public void cancelAllMediaRequests();
}
