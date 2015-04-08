package com.toboehm.trendingmedia;

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
import com.toboehm.trendingmedia.utils.CountryFlagUtils;
import com.toboehm.trendingmedia.viewmodels.MainActivityViewModel;
import com.toboehm.trendingmedia.views.SelectCountryDialogFragment;
import com.toboehm.trendingmedia.views.viewlistener.HashButtonClickListener;

import org.apmem.tools.layouts.FlowLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity implements ITrendsDownloadedListener,
                                                                ITrendsProviderStatusListener,
                                                                SelectCountryDialogFragment.OnCountrySelectedListener {

    // UI elements
    @InjectView(R.id.ma_current_country_iv) ImageView mCurrentCountryIB;
    @InjectView(R.id.ma_hashtag_container) FlowLayout mHashTagContainer;
    @InjectView(R.id.ma_picture_grid) GridView mPictureGrid;
    private final HashButtonClickListener mHashButtonClickListener = new HashButtonClickListener(this);
    private CountryFlagUtils mFlagUtils = new CountryFlagUtils(this);


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
        mFlagUtils.setFlagDrawable(mViewModel.getCurrentCountryISO().toLowerCase(), mCurrentCountryIB);

        // (re)init hashtag container
        mHashTagContainer.removeAllViews();
        for(final Map.Entry<String, Boolean> hashTagAndState : mViewModel.geCurrentTrends().entrySet()){

            final Button trendButton = new Button(this);
            trendButton.setTextSize(10);
            trendButton.setTextColor(hashTagAndState.getValue() ? getResources().getColor(R.color.hashtag_active) : getResources().getColor(R.color.hashtag_inactive));
            trendButton.setText(hashTagAndState.getKey());
            trendButton.setOnClickListener(mHashButtonClickListener);

            mHashTagContainer.addView(trendButton);
        }
    }

    @OnClick(R.id.ma_current_country_iv)
    void onCountryChooserClicked(){

        final SelectCountryDialogFragment selectCountryDialogFragment = new SelectCountryDialogFragment();

        selectCountryDialogFragment.show(getFragmentManager(), SelectCountryDialogFragment.class.getName());
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
    public void onTrendsDownloaded(final HashSet<String> pHashTags) {

        fixHashPrefixes(pHashTags);

        // filter out known hashtags
        pHashTags.removeAll(mViewModel.geCurrentTrends().keySet());

        // add new trends to viewmodel
        mViewModel.addTrends(pHashTags);

        // create new hash view entries
        for(final String trend : pHashTags){

            final Button trendButton = new Button(this);
            trendButton.setTextSize(10);
            trendButton.setTextColor(getResources().getColor(R.color.hashtag_inactive));
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

            // manually trigger on country selected
            onCountrySelected(mViewModel.getCurrentCountryISO());

        }else{

            Toast.makeText(this, "Initialization failed for trends provider " + pTrendsProvider.getName(), Toast.LENGTH_LONG).show();
        }
    }

    public MainActivityViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onCountrySelected(final String pCountryISOcode) {

        mViewModel.setCurrentLocation(pCountryISOcode);
        mFlagUtils.setFlagDrawable(pCountryISOcode, mCurrentCountryIB);

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
