package com.toboehm.trendingmedia.viewmodels;

import android.location.Address;

import com.google.common.collect.ImmutableSet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

/**
 * Created by Tobias Boehm on 28.03.2015.
 */
public class MainActivityViewModel {

    private Address mCurrentLocation = new Address(Locale.getDefault());
    private final HashSet<String> mCurrentTrends = new HashSet<>();


    public Address getCurrentLocation() {
        return mCurrentLocation;
    }

    public void setCurrentLocation(Address mCurrentLocation) {
        this.mCurrentLocation = mCurrentLocation;
    }

    public ImmutableSet<String> geCurrentTrends() {

        return ImmutableSet.copyOf(mCurrentTrends);
    }

    public void addCurrentTrends(final String... pTrends){

        mCurrentTrends.addAll(Arrays.asList(pTrends));
    }

    public void clearTrends(){

        mCurrentTrends.clear();
    }
}
