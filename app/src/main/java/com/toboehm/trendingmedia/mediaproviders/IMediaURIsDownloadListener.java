package com.toboehm.trendingmedia.mediaproviders;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by Tobias Boehm on 30.03.2015.
 */
public interface IMediaURIsDownloadListener {

    public void onMediaURIsDownloaded(final String pTrend, final ArrayList<Uri> pMedia);
}
