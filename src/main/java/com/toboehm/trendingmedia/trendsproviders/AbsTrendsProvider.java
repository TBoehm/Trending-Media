package com.toboehm.trendingmedia.trendsproviders;

import android.location.Address;

/**
 * Created by Tobias Boehm on 28.03.2015.
 */
public abstract class AbsTrendsProvider {

    public enum Status{

        PENDING,
        READY,
        FAILURE
    }

    protected final ITrendsProviderStatusListener mProviderIsReadyListener;
    protected Status mStatus = Status.PENDING;

    public AbsTrendsProvider(final ITrendsProviderStatusListener pProviderReadyListener){

        mProviderIsReadyListener = pProviderReadyListener;
    }

    /**
     *
     * @return A human readable name for the provider.
     */
    public abstract String getName();

    /**
     * Asynchronously requests a set of trends of a specific place.
     * @return
     */
    public abstract void asyncRequestRegionTrends(final Address pPlace, final ITrendsDownloadedListener pListener);

    /**
     * Asynchronously requests a set of (global) trends.
     * @return
     */
    public abstract void asyncRequestGlobalTrends(final ITrendsDownloadedListener pListener);

    /**
     *
     * @return true if the provider is completely initialized
     */
    public Status getStatus(){

        return mStatus;
    }
}
