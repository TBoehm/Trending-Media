package com.toboehm.trendingmedia.trendsproviders;

import java.util.HashSet;

/**
 * Created by Tobias Boehm on 29.03.2015.
 */
public interface ITrendsDownloadedListener {

    public void onTrendsDownloaded(final HashSet<String> pTrends);
}
