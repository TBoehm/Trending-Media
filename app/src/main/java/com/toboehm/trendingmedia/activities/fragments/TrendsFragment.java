package com.toboehm.trendingmedia.activities.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.primitives.Booleans;
import com.toboehm.trendingmedia.R;
import com.toboehm.trendingmedia.activities.dialogs.SelectCountryDialogFragment;
import com.toboehm.trendingmedia.trendsproviders.AbsTrendsProvider;
import com.toboehm.trendingmedia.trendsproviders.ITrendsDownloadedListener;
import com.toboehm.trendingmedia.trendsproviders.ITrendsProviderStatusListener;
import com.toboehm.trendingmedia.trendsproviders.TwitterTrendsProvider;
import com.toboehm.trendingmedia.utils.CountryFlagUtils;

import org.apmem.tools.layouts.FlowLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * A fragment for displaying current trends world wide in form of hashtags.
 *
 * Created by Tobias Boehm on 31.03.2015.
 */
public class TrendsFragment extends Fragment implements ITrendsProviderStatusListener, ITrendsDownloadedListener,
                SelectCountryDialogFragment.OnCountrySelectedListener {

    private static final String COUNTRY_STATE = "COUNTRY_STATE";
    private static final String HASHTAGS_STATE = "HASHTAGS_STATE";
    private static final String HASHTAGS_ACTIVE_STATE = "HASHTAGS_ACTIVE_STATE";

    public static final int REQUEST_COUNTRY_SELECTION = 100;



    // UI
    @InjectView(R.id.tf_current_country_iv) ImageView mCurrentCountryIB;
    @InjectView(R.id.tf_hashtag_container)  FlowLayout mHashTagContainer;
    private final HashButtonClickListener mHashButtonClickListener = new HashButtonClickListener();


    // Model
    /**
     * Two letter ISO code of the current country
     */
    private String mCurrentCountryCode;
    private final HashMap<String, Boolean> mCurrentTrends = new HashMap<>();
    private final ArrayList<AbsTrendsProvider> mTrendsProviders = new ArrayList<>();
    private CountryFlagUtils mFlagUtils;
    private TrendStateChangedListener mTrendStateChangedListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check if there is a saved state
        if(savedInstanceState != null){

            if(savedInstanceState.containsKey(COUNTRY_STATE)){

                mCurrentCountryCode = savedInstanceState.getString(COUNTRY_STATE);
            }
            if(savedInstanceState.containsKey(HASHTAGS_STATE) && savedInstanceState.containsKey(HASHTAGS_ACTIVE_STATE)){

                final String[] hashtags = savedInstanceState.getStringArray(HASHTAGS_STATE);
                final boolean[] hashtags_state = savedInstanceState.getBooleanArray(HASHTAGS_ACTIVE_STATE);
                for(int pos = 0; pos < hashtags.length; pos ++){

                    mCurrentTrends.put(hashtags[pos], hashtags_state[pos]);
                }
            }
        }else{

            // set default locale if we have a flag for it. else set USA code
            mCurrentCountryCode = Locale.getDefault().getCountry().toLowerCase();
            final HashSet<String> excludedCountryCodes = new HashSet<>(Arrays.asList(getResources().getStringArray(R.array.excluded_country_codes)));
            if(excludedCountryCodes.contains(mCurrentCountryCode)){

                mCurrentCountryCode = Locale.US.getCountry().toLowerCase();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mTrendStateChangedListener = (TrendStateChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCountrySelectedListener");
        }

        mFlagUtils = new CountryFlagUtils(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment and init ButterKnife
        final View view =  inflater.inflate(R.layout.fragment_trends, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // init trends provider
        mTrendsProviders.add(new TwitterTrendsProvider(getActivity(), this));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString(COUNTRY_STATE, mCurrentCountryCode);

        final ArrayList<String> hashtags = new ArrayList<>();
        final ArrayList<Boolean> hashtags_state = new ArrayList<>();
        for(Map.Entry<String, Boolean> entry : mCurrentTrends.entrySet()){

            hashtags.add(entry.getKey());
            hashtags_state.add(entry.getValue());
        }
        outState.putStringArray(HASHTAGS_STATE, hashtags.toArray(new String[0]));
        outState.putBooleanArray(HASHTAGS_ACTIVE_STATE, Booleans.toArray(hashtags_state));

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();

        initView();
    }

    private void initView() {

        // init country button
        mFlagUtils.setFlagDrawable(mCurrentCountryCode, mCurrentCountryIB);

        // (re)init hashtag container
        mHashTagContainer.removeAllViews();
        for(final Map.Entry<String, Boolean> hashTagAndState : mCurrentTrends.entrySet()){

            final Button trendButton = new Button(getActivity());
            trendButton.setTextSize(10);
            trendButton.setTextColor(hashTagAndState.getValue() ? getResources().getColor(R.color.hashtag_active) : getResources().getColor(R.color.hashtag_inactive));
            trendButton.setText(hashTagAndState.getKey());
            trendButton.setOnClickListener(mHashButtonClickListener);

            mHashTagContainer.addView(trendButton);
        }
    }

    @OnClick(R.id.tf_current_country_iv)
    void onCountryChooserClicked(){

        final SelectCountryDialogFragment selectCountryDialogFragment = new SelectCountryDialogFragment();
        selectCountryDialogFragment.setTargetFragment(this, REQUEST_COUNTRY_SELECTION);

        selectCountryDialogFragment.show(getFragmentManager(), SelectCountryDialogFragment.class.getName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_COUNTRY_SELECTION && resultCode == Activity.RESULT_OK){



        }else{

            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public boolean toggleTrend(final String pTrend){

        if(mCurrentTrends.containsKey(pTrend)){

            // update trend state in fragment model
            final boolean stateNew = !mCurrentTrends.get(pTrend);
            mCurrentTrends.put(pTrend, stateNew);

            // inform activity about trend changes
            if(stateNew){

                mTrendStateChangedListener.activateTrend(pTrend);

            }else{

                mTrendStateChangedListener.deactivateTrend(pTrend);
            }

            return stateNew;

        }else{

            throw new IllegalStateException("Trend '" + pTrend + "' is not part of the current model state.");
        }
    }

    @Override
    public void onTrendsProviderStatusChanged(AbsTrendsProvider pTrendsProvider,final AbsTrendsProvider.Status pStatus) {

        // if at least one trend provider is ready change visibility of the country spinner to "visible"
        if((pStatus == AbsTrendsProvider.Status.READY) && (mCurrentCountryIB.getVisibility() != View.VISIBLE)){

            mCurrentCountryIB.setVisibility(View.VISIBLE);

            // manually trigger on country selected
            onCountrySelected(mCurrentCountryCode);

        }else{

            Toast.makeText(getActivity(),
                            "Initialization failed for trends provider " + pTrendsProvider.getName(),
                            Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTrendsDownloaded(final HashSet<String> pHashTags) {

        fixHashPrefixes(pHashTags);

        // filter out known hashtags
        pHashTags.removeAll(mCurrentTrends.keySet());

        // create new hash view entries and add new trends to viewmodel
        for(final String trend : pHashTags){

            // add trend to model
            mCurrentTrends.put(trend, false);

            // add trend to view
            final Button trendButton = new Button(getActivity());
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
    public void onCountrySelected(final String pCountryISOcode) {

        mCurrentCountryCode = pCountryISOcode;
        mFlagUtils.setFlagDrawable(pCountryISOcode, mCurrentCountryIB);

        try {
            // get address based on ISO country
            final Address currentLocation = new Geocoder(getActivity()).getFromLocationName(pCountryISOcode, 1).get(0);

            // clear old trends
            mCurrentTrends.clear();
            mHashTagContainer.removeAllViews();

            // request trends based on current location and add them to view model
            for(AbsTrendsProvider regionTrendProvider : mTrendsProviders){

                if(regionTrendProvider.getStatus() == AbsTrendsProvider.Status.READY){

                    regionTrendProvider.asyncRequestRegionTrends(currentLocation, this);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
    * Created by Tobias Boehm on 31.03.2015.
    */
    private class HashButtonClickListener implements Button.OnClickListener{

        @Override
        public void onClick(View v) {

            final Button trendButton = (Button)v;

            // toggle hashtag state and act on new state
            if(toggleTrend(trendButton.getText().toString())){

                // if hashtag is active now -> load corresponding media
                trendButton.setTextColor(trendButton.getContext().getResources().getColor(R.color.hashtag_active));
                Toast.makeText(getActivity(), "Load media for hashtag " + trendButton.getText(), Toast.LENGTH_SHORT).show();

            }else{

                // if hashtag is inactive now -> remove corresponding media
                trendButton.setTextColor(trendButton.getContext().getResources().getColor(R.color.hashtag_inactive));
                Toast.makeText(getActivity(), "Remove media for hashtag " + trendButton.getText(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface TrendStateChangedListener {

        public void activateTrend(final String pTrend);

        public void deactivateTrend(final String pTrend);
    }
}
