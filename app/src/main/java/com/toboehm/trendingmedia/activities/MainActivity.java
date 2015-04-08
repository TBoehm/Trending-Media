package com.toboehm.trendingmedia.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.toboehm.trendingmedia.R;
import com.toboehm.trendingmedia.activities.fragments.TrendsFragment;

import java.util.HashSet;


public class MainActivity extends ActionBarActivity implements TrendsFragment.TrendStateChangedListener {

    private final HashSet<String> mActiveTrends = new HashSet<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
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
    public void activateTrend(String pTrend) {

        mActiveTrends.add(pTrend);
    }

    @Override
    public void deactivateTrend(String pTrend) {

        mActiveTrends.remove(pTrend);
    }
}
