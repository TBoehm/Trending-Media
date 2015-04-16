package com.toboehm.trendingmedia.mediaproviders;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by Tobias Boehm on 30.03.2015.
 */
public abstract class AbsMediaProvider {

    public abstract void asyncRequestMediaForTrend(final String pTrend,
                                                   final IMediaURIsDownloadListener pListener,
                                                   final int pMaxPicturesPerQuery);

    public abstract void cancelMediaRequestsForTrend(final String pTrend);

    public abstract void cancelAllMediaRequests();

    /**
     * Created by Tobias Boehm on 30.03.2015.
     */
    public interface IMediaURIsDownloadListener {

        public void onMediaURIsDownloaded(final String pTrend, final ArrayList<Uri> pMedia);
    }
}
