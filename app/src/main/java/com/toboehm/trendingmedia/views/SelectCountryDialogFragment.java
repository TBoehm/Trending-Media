package com.toboehm.trendingmedia.views;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.toboehm.trendingmedia.R;
import com.toboehm.trendingmedia.utils.CountryFlagUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Tobias Boehm on 31.03.2015.
 */
public class SelectCountryDialogFragment extends DialogFragment{

    @InjectView(R.id.scd_gridview) GridView mCountriesGV;

    private final OnCountryFlagClickedListener mCountryFlagClickedListener = new OnCountryFlagClickedListener();
    private OnCountrySelectedListener mOnCountrySelectedListener;
    private CountryFlagUtils mFlagUtils;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFlagUtils = new CountryFlagUtils(getActivity());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.setTitle(R.string.select_country_dialog_title);

        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();

        // dialog will fill the screen horizontally
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mOnCountrySelectedListener = (OnCountrySelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCountrySelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View dialogView = inflater.inflate(R.layout.dialog_select_country, container);
        ButterKnife.inject(this,dialogView);

        initGrid();

        return dialogView;
    }

    private void initGrid() {

        // prepare locale liste which contains only ISO country codes for countries we have flags for
        final HashSet<String> filteredCountryCodes = new HashSet<>(Arrays.asList(Locale.getISOCountries()));
        for(String lowerCaseCountryCode : getResources().getStringArray(R.array.excluded_country_codes)){

            filteredCountryCodes.remove(lowerCaseCountryCode.toUpperCase());
        }

        // configure grid adapter
        mCountriesGV.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.abc_list_menu_item_icon, new ArrayList<String>(filteredCountryCodes)){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if((convertView == null) || !(convertView instanceof ImageView)){

                    convertView = new ImageView(getContext());
                    convertView.setOnClickListener(mCountryFlagClickedListener);

                    final int flagSizeInDIP = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getContext().getResources().getDisplayMetrics()));
                    convertView.setMinimumWidth(flagSizeInDIP);
                    convertView.setMinimumHeight(flagSizeInDIP);
                }

                final String countryISOcode = getItem(position);

                // set ISO country code as tag
                convertView.setTag(countryISOcode);

                // set flag from asset directory
                mFlagUtils.setFlagDrawable(countryISOcode, (ImageView)convertView);

                return convertView;
            }
        });
    }

    private class OnCountryFlagClickedListener implements View.OnClickListener {

        @Override
        public void onClick(final View pCountryIB) {

            mOnCountrySelectedListener.onCountrySelected((String)pCountryIB.getTag());
            dismiss();
        }
    }

    public interface OnCountrySelectedListener {

        public void onCountrySelected(final String pCountryISOcode);
    }
}
