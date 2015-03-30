package com.toboehm.trendingmedia.trendsproviders;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.AsyncTask;

import com.google.common.collect.HashMultimap;
import com.toboehm.trendingmedia.R;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Minutes;

import java.util.HashMap;
import java.util.HashSet;

import twitter4j.GeoLocation;
import twitter4j.Location;
import twitter4j.ResponseList;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Tobias Boehm on 28.03.2015.
 */
public class TwitterTrendsProvider extends AbsTrendsProvider {

    private static final String TWITTER_PREFS_FILE = "TwitterPrefs";
    private static final String TWITTER_TOKEN_TYPE = "TWITTER_TOKEN_TYPE";
    private static final String TWITTER_ACCESS_TOKEN = "TWITTER_ACCESS_TOKEN";

    /**
     * Query cool down time.
     */
    private static final Duration FIFTEEN_MINUTES = Minutes.minutes(15).toStandardDuration();

    // tools
    private Twitter mTwitterClient;
    private AsyncTask mRunningAsyncTask;

    // cached trend based on WOEID (Integer)
    private HashMap<Integer,Instant> mTimeSinceAccess = new HashMap<>();
    private HashMultimap<Integer,String> mCachedTrends = HashMultimap.create();


    public TwitterTrendsProvider(final Context pContext, final ITrendsProviderStatusListener pTrendsProviderReadyListener){
        super(pTrendsProviderReadyListener);

        // check if access token is available
        final SharedPreferences twitterPreferences = pContext.getSharedPreferences(TWITTER_PREFS_FILE, Context.MODE_PRIVATE);
        if(twitterPreferences.contains(TWITTER_TOKEN_TYPE) && twitterPreferences.contains(TWITTER_ACCESS_TOKEN)){

            // if yes, simply setup the twitter client
            setupTwitterClient(pContext, twitterPreferences);

        }else{

            cancelAnyRunningAsyncTask();


            // if not query access token via async task and then setup the twitter client
            mRunningAsyncTask = new AsyncTask<Void,Void,OAuth2Token>(){

                @Override
                protected OAuth2Token doInBackground(Void... params) {

                    // prepare twitter factory for obtaining an application-only access token
                    final ConfigurationBuilder cb = new ConfigurationBuilder();
                    cb.setOAuthConsumerKey(pContext.getString(R.string.twitter_consumer_key))
                            .setOAuthConsumerSecret(pContext.getString(R.string.twitter_consumer_secret))
                            .setApplicationOnlyAuthEnabled(true);

                    final TwitterFactory tf = new TwitterFactory(cb.build());


                    try {
                        // query access token
                        return tf.getInstance().getOAuth2Token();


                    } catch (TwitterException e) {
                        e.printStackTrace();

                        return null;
                    }
                }

                @Override
                protected void onPostExecute(final OAuth2Token pToken) {

                    if(pToken != null){

                        // save access token
                        twitterPreferences
                                .edit()
                                .putString(TWITTER_TOKEN_TYPE, pToken.getTokenType())
                                .putString(TWITTER_ACCESS_TOKEN, pToken.getAccessToken())
                                .apply();

                        // setup twitter client
                        setupTwitterClient(pContext, twitterPreferences);

                    }else{

                        mStatus = AbsTrendsProvider.Status.FAILURE;
                        mProviderIsReadyListener.onTrendsProviderStatusChanged(TwitterTrendsProvider.this, mStatus);
                    }

                    // remove own reference in parent
                    mRunningAsyncTask = null;
                }
            }.execute();
        }
    }

    private void cancelAnyRunningAsyncTask() {

        if((mRunningAsyncTask != null) && !mRunningAsyncTask.isCancelled()){

            mRunningAsyncTask.cancel(true);
        }
    }

    private void setupTwitterClient(final Context pContext, final SharedPreferences pTwitterPreferences) {

        // setup twitter  client
        final ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(pContext.getString(R.string.twitter_consumer_key))
          .setOAuthConsumerSecret(pContext.getString(R.string.twitter_consumer_secret))
          .setOAuth2TokenType(pTwitterPreferences.getString(TWITTER_TOKEN_TYPE, ""))
          .setOAuth2AccessToken(pTwitterPreferences.getString(TWITTER_ACCESS_TOKEN, ""))
          .setApplicationOnlyAuthEnabled(true);

        mTwitterClient = new TwitterFactory(cb.build()).getInstance();

        // update status
        mStatus = Status.READY;

        // inform "trend provider is ready" listener
        mProviderIsReadyListener.onTrendsProviderStatusChanged(this, mStatus);
    }


    @Override
    public String getName() {

        return  "Twitter";
    }

    @Override
    public void asyncRequestRegionTrends(final Address pPlace, final ITrendsDownloadedListener pListener) {

        mRunningAsyncTask = new AsyncTask<Void,Void,HashSet<String>>(){

            @Override
            protected HashSet<String> doInBackground(Void... params) {

                final HashSet<String> trends = new HashSet<>();

                try {
                    // Get trend regions based on given location
                    final GeoLocation geoLocation = new GeoLocation(pPlace.getLatitude(), pPlace.getLongitude());
                    final ResponseList<Location> trendsRegions = mTwitterClient.getClosestTrends(geoLocation);

                    // get trends of those regions
                    for(Location trendRegion : trendsRegions){

                        // ... and put them in our trends map
                        trends.addAll(getTrends(trendRegion.getWoeid()));
                    }
                } catch (TwitterException e) {
                    e.printStackTrace();
                }

                return trends;
            }

            @Override
            protected void onPostExecute(final HashSet<String> pTrends) {

                pListener.onTrendsDownloaded(pTrends);


                // remove own reference in parent
                mRunningAsyncTask = null;
            }
        }.execute();
    }

    @Override
    public void asyncRequestGlobalTrends(final ITrendsDownloadedListener pListener) {

        new AsyncTask<Void,Void,HashSet<String>>(){

            @Override
            protected HashSet<String> doInBackground(Void... params) {

                // World Woeid = 1
                return getTrends(1);
            }

            @Override
            protected void onPostExecute(final HashSet<String> pTrends) {

                pListener.onTrendsDownloaded(pTrends);
            }
        }.execute();


    }

    private HashSet<String> getTrends(final int pWOEID){

        final HashSet<String> trends = new HashSet<>();

        // if cache contains flag to prevent repeated twitter queries for the same WOEID
        if(mTimeSinceAccess.containsKey(pWOEID)) {

            // check if cooldown time is up
            final Duration cacheDuration = new Duration(mTimeSinceAccess.get(pWOEID), Instant.now());
            if (cacheDuration.isLongerThan(FIFTEEN_MINUTES)) {

                // clear cache
                mCachedTrends.removeAll(pWOEID);
                mTimeSinceAccess.remove(pWOEID);
            }
        }

        // if cache contains data for the given WOEID
        if(mCachedTrends.containsKey(pWOEID)){

            trends.addAll(mCachedTrends.get(pWOEID));

        }else{

            try {
                final Trends regionTrends = mTwitterClient.getPlaceTrends(pWOEID);
                for(Trend trend : regionTrends.getTrends()) {
                    trends.add(trend.getName());
                }
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }

        return trends;
    }
}
