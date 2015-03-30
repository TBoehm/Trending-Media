package com.toboehm.trendingmedia;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.toboehm.trendingmedia.trendsproviders.ITrendsDownloadedListener;
import com.toboehm.trendingmedia.trendsproviders.AbsTrendsProvider;
import com.toboehm.trendingmedia.trendsproviders.ITrendsProviderStatusListener;
import com.toboehm.trendingmedia.viewmodels.MainActivityViewModel;

import org.apmem.tools.layouts.FlowLayout;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemSelected;


public class MainActivity extends ActionBarActivity implements ITrendsDownloadedListener, ITrendsProviderStatusListener {

    // UI elements
    @InjectView(R.id.ma_country_sp) Spinner mCountrySP;
    @InjectView(R.id.ma_hashtag_container) FlowLayout mHashTagContainer;
    @InjectView(R.id.ma_picture_grid) GridView mPictureGrid;

    private final HashButtonClickListener mHashButtonClickListener = new HashButtonClickListener();

    // viewModel
    private MainActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set content view and inject views
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        // init viewModel
        mViewModel = new MainActivityViewModel(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    private void initView() {

        // init countries spinner
        mCountrySP.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, Locale.getISOCountries()));
    }

    @OnItemSelected(R.id.ma_country_sp)
    void onCountryChosen(final int pPosition){

        try {
            // get address based on ISO country
            final String isoCountry = (String) mCountrySP.getAdapter().getItem(pPosition);
            final Address currentLocation = new Geocoder(this).getFromLocationName(isoCountry, 1).get(0);

            // set current location in viewmodel
            mViewModel.setCurrentLocation(currentLocation);

            // clear old trends
            mViewModel.clearTrends();
            mHashTagContainer.removeAllViews();

            // request trends based on current location and add them to view model
            for(AbsTrendsProvider regionTrendProvider : mViewModel.getTrendsProviders()){

               if(regionTrendProvider.getStatus() == AbsTrendsProvider.Status.READY){

                   regionTrendProvider.asyncRequestRegionTrends(currentLocation, this);
               }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public void onTrendsDownloaded(final HashSet<String> pTrends) {

        // add trends to viewmodel
        mViewModel.addTrends(pTrends);

        // create new hash view entries
        for(final String trend : mViewModel.geCurrentTrends().keySet()){

            final Button trendButton = new Button(this);
            trendButton.setTextSize(10);
            trendButton.setTextColor(getResources().getColor(R.color.primary_dark));
            trendButton.setText(trend);
            trendButton.setOnClickListener(mHashButtonClickListener);

            mHashTagContainer.addView(trendButton);
        }
    }

    @Override
    public void onTrendsProviderStatusChanged(AbsTrendsProvider pTrendsProvider,final AbsTrendsProvider.Status pStatus) {

        // if at least one trend provider is ready change visibility of the country spinner to "visible"
        if((pStatus == AbsTrendsProvider.Status.READY) && (mCountrySP.getVisibility() != View.VISIBLE)){

            mCountrySP.setVisibility(View.VISIBLE);

        }else{

            Toast.makeText(this, "Initialization failed for trends provider " + pTrendsProvider.getName(), Toast.LENGTH_LONG).show();
        }
    }

    private class HashButtonClickListener implements Button.OnClickListener{

        @Override
        public void onClick(View v) {

            final Button hashButton = (Button)v;

            // toggle hashtag state and act on new state
            if(mViewModel.toggleTrend(hashButton.getText().toString())){

                // if hashtag is active now -> load corresponding media
                Toast.makeText(MainActivity.this, "Load media for hashtag " + hashButton.getText(), Toast.LENGTH_SHORT).show();

            }else{

                // if hashtag is inactive now -> remove corresponding media
                Toast.makeText(MainActivity.this, "Remove media for hashtag " + hashButton.getText(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
