package com.toboehm.trendingmedia.trendsproviders;

import android.location.Address;

/**
 * Created by Tobias Boehm on 28.03.2015.
 */
public interface ITrendsProvider {

    /**
     * Asynchronously requests a set of trends of a specific place.
     * @return
     */
    public void asyncRequestRegionTrends(final Address pPlace, final ITrendsDownloadedListener pListener);

    /**
     * Asynchronously requests a set of (global) trends.
     * @return
     */
    public void asyncRequestGlobalTrends(final ITrendsDownloadedListener pListener);

    /**
     *
     * @return true if the provider is completely initialized
     */
    public boolean isReady();
}
