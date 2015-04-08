package com.toboehm.trendingmedia.trendsproviders;

/**
 * Created by Tobias Boehm on 30.03.2015.
 */
public interface ITrendsProviderStatusListener {

    public void onTrendsProviderStatusChanged(final AbsTrendsProvider pTrendsProvider, AbsTrendsProvider.Status pStatus);
}
