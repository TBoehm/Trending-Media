package com.toboehm.trendingmedia.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.toboehm.trendingmedia.R;
import com.toboehm.trendingmedia.activities.fragments.MediaFragment;
import com.toboehm.trendingmedia.activities.fragments.TrendsFragment;


public class MainActivity extends ActionBarActivity implements TrendsFragment.TrendStateChangedListener {

    private MediaFragment mMediaFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mMediaFragment = (MediaFragment) getFragmentManager().findFragmentById(R.id.ma_media_fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTrendActivated(String pTrend) {

        mMediaFragment.addPicturesForTrend(pTrend);
    }

    @Override
    public void onTrendDeactivated(String pTrend) {

        mMediaFragment.removePicturesForTrend(pTrend);
    }

    @Override
    public void onTrendsCleared() {

        mMediaFragment.removeAllPictures();
    }
}
