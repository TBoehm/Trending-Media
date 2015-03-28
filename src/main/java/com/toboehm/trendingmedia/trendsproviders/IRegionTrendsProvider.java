package com.toboehm.trendingmedia.trendsproviders;

import android.location.Address;

import java.util.ArrayList;

/**
 * Created by Tobias Boehm on 28.03.2015.
 */
public interface IRegionTrendsProvider extends ITrendsProvider {

    /**
     * Returns a list of trends of a specific place sorted by trendiness.
     * @return
     */
    public ArrayList<String> getRegionTrends(final Address pPlace);
}
