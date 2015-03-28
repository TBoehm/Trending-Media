package com.toboehm.trendingmedia.trendsproviders;


import java.util.ArrayList;

/**
 * Created by Tobias Boehm on 28.03.2015.
 */
public interface ITrendsProvider {

    /**
     * Returns a list of (global) trends sorted by trendiness.
     * @return
     */
    public ArrayList<String> getTrends();
}
