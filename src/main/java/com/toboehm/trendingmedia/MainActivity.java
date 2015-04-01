package com.toboehm.trendingmedia;

import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.toboehm.trendingmedia.trendsproviders.AbsTrendsProvider;
import com.toboehm.trendingmedia.trendsproviders.ITrendsDownloadedListener;
import com.toboehm.trendingmedia.trendsproviders.ITrendsProviderStatusListener;
import com.toboehm.trendingmedia.viewmodels.MainActivityViewModel;
import com.toboehm.trendingmedia.views.viewlistener.HashButtonClickListener;
import com.toboehm.trendingmedia.views.SelectCountryDialog;

import org.apmem.tools.layouts.FlowLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity implements ITrendsDownloadedListener,
                                                                ITrendsProviderStatusListener,
                                                                SelectCountryDialog.OnCountrySelectedListener {

    // UI elements
    @InjectView(R.id.ma_current_country_iv) ImageView mCurrentCountryIB;
    @InjectView(R.id.ma_hashtag_container) FlowLayout mHashTagContainer;
    @InjectView(R.id.ma_picture_grid) GridView mPictureGrid;
    private final HashButtonClickListener mHashButtonClickListener = new HashButtonClickListener(this);

    // viewModel
    private MainActivityViewModel mViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set content view and inject views
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        // init viewModel
        mViewModel = new MainActivityViewModel(this, this, savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        mViewModel.persisState(outState);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initView();
    }

    private void initView() {

        // init country button
        try {
            final String filename = mViewModel.getCurrentCountryISO().toLowerCase() + ".png";
            final Drawable flag = Drawable.createFromStream(getAssets().open(getString(R.string.flag_asset_folder_path) + filename), filename);

            mCurrentCountryIB.setImageDrawable(flag);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.ma_current_country_iv)
    void onCountryChooserClicked(){

        new SelectCountryDialog(MainActivity.this, this).show();
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

        fixHashPrefixes(pTrends);

        // add trends to viewmodel
        mViewModel.addTrends(pTrends);

        // create new hash view entries
        for(final String trend : pTrends){

            final Button trendButton = new Button(this);
            trendButton.setTextSize(10);
            trendButton.setTextColor(getResources().getColor(R.color.primary_dark));
            trendButton.setText(trend);
            trendButton.setOnClickListener(mHashButtonClickListener);

            mHashTagContainer.addView(trendButton);
        }
    }

    private void fixHashPrefixes(final HashSet<String> pTrends) {

        final ArrayList<String> fixedTrends = new ArrayList<>();
        for(String trend : pTrends){

            // add hash if the trend does not already start with a hash
            if(trend.startsWith("#")){

                fixedTrends.add(trend);

            }else{

                fixedTrends.add("#" + trend);
            }
        }
        pTrends.clear();
        pTrends.addAll(fixedTrends);
    }

    @Override
    public void onTrendsProviderStatusChanged(AbsTrendsProvider pTrendsProvider,final AbsTrendsProvider.Status pStatus) {

        // if at least one trend provider is ready change visibility of the country spinner to "visible"
        if((pStatus == AbsTrendsProvider.Status.READY) && (mCurrentCountryIB.getVisibility() != View.VISIBLE)){

            mCurrentCountryIB.setVisibility(View.VISIBLE);

        }else{

            Toast.makeText(this, "Initialization failed for trends provider " + pTrendsProvider.getName(), Toast.LENGTH_LONG).show();
        }
    }

    public MainActivityViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onCountrySelected(final String pCountryISOcode, final Drawable pCountryFlag) {

        mViewModel.setCurrentLocation(pCountryISOcode);
        mCurrentCountryIB.setImageDrawable(pCountryFlag);

        try {
            // get address based on ISO country
            final Address currentLocation = new Geocoder(this).getFromLocationName(pCountryISOcode, 1).get(0);

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
}
