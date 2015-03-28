package com.toboehm.trendingmedia.trendsproviders;

import android.content.Context;
import android.location.Address;

import com.toboehm.trendingmedia.R;

import java.util.ArrayList;

import twitter4j.GeoLocation;
import twitter4j.Location;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * Created by Tobias Boehm on 28.03.2015.
 */
public class TwitterTrendsProvider implements IRegionTrendsProvider {

    private final Twitter mTwitterClient;

    public TwitterTrendsProvider(final Context pContext){

        mTwitterClient = TwitterFactory.getSingleton();
        mTwitterClient.setOAuthConsumer(pContext.getString(R.string.twitter_consumer_key),pContext.getString(R.string.twitter_consumer_secret));
    }


    @Override
    public ArrayList<String> getRegionTrends(final Address pPlace) {

        final ArrayList<String> trends = new ArrayList<>();

        try {
            // Get trends based on given location
            final GeoLocation geoLocation = new GeoLocation(pPlace.getLatitude(), pPlace.getLongitude());
            final ResponseList<Location> twitterTrends = mTwitterClient.getClosestTrends(geoLocation);

            
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        
        return trends;
    }

    @Override
    public ArrayList<String> getTrends() {
        return null;
    }
}
