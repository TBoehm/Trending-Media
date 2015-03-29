package com.toboehm.trendingmedia.viewmodels;

import android.content.Context;
import android.location.Address;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.toboehm.trendingmedia.trendsproviders.ITrendsProvider;
import com.toboehm.trendingmedia.trendsproviders.TwitterTrendsProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

/**
 * Created by Tobias Boehm on 28.03.2015.
 */
public class MainActivityViewModel {

    private Address mCurrentLocation = new Address(Locale.getDefault());
    private final HashSet<String> mCurrentTrends = new HashSet<>();
    private final ArrayList<ITrendsProvider> mTrendsProviders = new ArrayList<>();


    public  MainActivityViewModel(final Context pContext){

        // init trends provider
        mTrendsProviders.add(new TwitterTrendsProvider(pContext));
    }

    public Address getCurrentLocation() {
        return mCurrentLocation;
    }

    public void setCurrentLocation(Address mCurrentLocation) {
        this.mCurrentLocation = mCurrentLocation;
    }

    public ImmutableList<ITrendsProvider> getTrendsProviders(){

        return ImmutableList.copyOf(mTrendsProviders);
    }

    public ImmutableSet<String> geCurrentTrends() {

        return ImmutableSet.copyOf(mCurrentTrends);
    }

    public void addCurrentTrends(final HashSet<String> pTrends){

        mCurrentTrends.addAll(pTrends);
    }

    public void clearTrends(){

        mCurrentTrends.clear();
    }
}
