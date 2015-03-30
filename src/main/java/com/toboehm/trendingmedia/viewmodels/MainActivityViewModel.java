package com.toboehm.trendingmedia.viewmodels;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.toboehm.trendingmedia.trendsproviders.AbsTrendsProvider;
import com.toboehm.trendingmedia.trendsproviders.ITrendsProviderStatusListener;
import com.toboehm.trendingmedia.trendsproviders.TwitterTrendsProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

/**
 * Created by Tobias Boehm on 28.03.2015.
 */
public class MainActivityViewModel {

    private Address mCurrentLocation = new Address(Locale.getDefault());
    private final HashMap<String, Boolean> mCurrentTrends = new HashMap<>();
    private final ArrayList<AbsTrendsProvider> mTrendsProviders = new ArrayList<>();

    private final HashMultimap<String, BitmapDrawable> mTrendsMedia = HashMultimap.create();


    public  MainActivityViewModel(final Context pContext, final ITrendsProviderStatusListener pTrendsProviderReadyListener){

        // init trends provider
        mTrendsProviders.add(new TwitterTrendsProvider(pContext, pTrendsProviderReadyListener));
    }

    public Address getCurrentLocation() {
        return mCurrentLocation;
    }

    public void setCurrentLocation(Address mCurrentLocation) {
        this.mCurrentLocation = mCurrentLocation;
    }

    public ImmutableList<AbsTrendsProvider> getTrendsProviders(){

        return ImmutableList.copyOf(mTrendsProviders);
    }

    public ImmutableMap<String, Boolean> geCurrentTrends() {

        return ImmutableMap.copyOf(mCurrentTrends);
    }

    public void addTrends(final HashSet<String> pTrends){

        for(String trend : pTrends){

            // add hash if the trend does not already start with a hash
            if(trend.startsWith("#")){

                mCurrentTrends.put(trend, false);

            }else{

                mCurrentTrends.put("#" + trend, false);
            }
        }
    }

    public boolean toggleTrend(final String pTrend){

        if(mCurrentTrends.containsKey(pTrend)){

            final boolean stateNew = !mCurrentTrends.get(pTrend);

            mCurrentTrends.put(pTrend, stateNew);

            return stateNew;

        }else{

            throw new IllegalStateException("Trend " + pTrend + " is not part of the current model state.");
        }
    }

    public void clearTrends(){

        mCurrentTrends.clear();
    }
}
