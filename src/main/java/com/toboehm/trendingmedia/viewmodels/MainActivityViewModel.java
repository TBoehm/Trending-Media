package com.toboehm.trendingmedia.viewmodels;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Booleans;
import com.toboehm.trendingmedia.R;
import com.toboehm.trendingmedia.trendsproviders.AbsTrendsProvider;
import com.toboehm.trendingmedia.trendsproviders.ITrendsProviderStatusListener;
import com.toboehm.trendingmedia.trendsproviders.TwitterTrendsProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Tobias Boehm on 28.03.2015.
 */
public class MainActivityViewModel {

    private static final String COUNTRY_STATE = "COUNTRY_STATE";
    private static final String HASHTAGS_STATE = "HASHTAGS_STATE";
    private static final String HASHTAGS_ACTIVE_STATE = "HASHTAGS_ACTIVE_STATE";


    private String mCurrentCountryISO;
    private final HashMap<String, Boolean> mCurrentTrends = new HashMap<>();
    private final ArrayList<AbsTrendsProvider> mTrendsProviders = new ArrayList<>();

    private final HashMultimap<String, BitmapDrawable> mTrendsMedia = HashMultimap.create();


    public  MainActivityViewModel(final Context pContext,
                                  final ITrendsProviderStatusListener pTrendsProviderReadyListener,
                                  final Bundle pSavedInstanceState){

        // check if there is a saved state
        if(pSavedInstanceState != null){

            if(pSavedInstanceState.containsKey(COUNTRY_STATE)){

                mCurrentCountryISO = pSavedInstanceState.getString(COUNTRY_STATE);
            }
            if(pSavedInstanceState.containsKey(HASHTAGS_STATE) && pSavedInstanceState.containsKey(HASHTAGS_ACTIVE_STATE)){

                final String[] hashtags = pSavedInstanceState.getStringArray(HASHTAGS_STATE);
                final boolean[] hashtags_state = pSavedInstanceState.getBooleanArray(HASHTAGS_ACTIVE_STATE);
                for(int pos = 0; pos < hashtags.length; pos ++){

                    mCurrentTrends.put(hashtags[pos], hashtags_state[pos]);
                }
            }
        }else{

            // set default locale if we have a flag for it. else set USA code
            mCurrentCountryISO = Locale.getDefault().getCountry().toLowerCase();
            final HashSet<String> excludedCountryCodes = new HashSet<>(Arrays.asList(pContext.getResources().getStringArray(R.array.excluded_country_codes)));
            if(excludedCountryCodes.contains(mCurrentCountryISO)){

                mCurrentCountryISO = Locale.US.getCountry().toLowerCase();
            }
        }

        // init trends provider
        mTrendsProviders.add(new TwitterTrendsProvider(pContext, pTrendsProviderReadyListener));
    }

    public void persisState(final Bundle pInstanceState){

        pInstanceState.putString(COUNTRY_STATE, mCurrentCountryISO);

        final ArrayList<String> hashtags = new ArrayList<>();
        final ArrayList<Boolean> hashtags_state = new ArrayList<>();
        for(Map.Entry<String, Boolean> entry : mCurrentTrends.entrySet()){

            hashtags.add(entry.getKey());
            hashtags_state.add(entry.getValue());
        }
        pInstanceState.putStringArray(HASHTAGS_STATE, hashtags.toArray(new String[0]));
        pInstanceState.putBooleanArray(HASHTAGS_ACTIVE_STATE, Booleans.toArray(hashtags_state));
    }

    /**
     * Returns the current country as ISO (2-letter) code.
     * @return
     */
    public String getCurrentCountryISO() {
        return mCurrentCountryISO;
    }

    public void setCurrentLocation(final String pCurrentCountryISO) {
        this.mCurrentCountryISO = pCurrentCountryISO;
    }

    public ImmutableList<AbsTrendsProvider> getTrendsProviders(){

        return ImmutableList.copyOf(mTrendsProviders);
    }

    public ImmutableMap<String, Boolean> geCurrentTrends() {

        return ImmutableMap.copyOf(mCurrentTrends);
    }

    public void addTrends(final HashSet<String> pTrends){

        for(String trend : pTrends){

            // if the trend does not start with a hash
            if(!trend.startsWith("#")){

                throw new IllegalArgumentException("Trend string '" + trend + "' was not fixed.");
            }

            mCurrentTrends.put(trend, false);
        }
    }

    public boolean toggleTrend(final String pTrend){

        if(mCurrentTrends.containsKey(pTrend)){

            final boolean stateNew = !mCurrentTrends.get(pTrend);

            mCurrentTrends.put(pTrend, stateNew);

            return stateNew;

        }else{

            throw new IllegalStateException("Trend '" + pTrend + "' is not part of the current model state.");
        }
    }

    public void clearTrends(){

        mCurrentTrends.clear();
    }
}
