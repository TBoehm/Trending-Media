package com.toboehm.trendingmedia.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.toboehm.trendingmedia.R;

import java.io.IOException;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Tobias Boehm on 31.03.2015.
 */
public class SelectCountryDialog extends Dialog{

    @InjectView(R.id.scd_gridview) GridView mCountriesGV;

    private final OnCountryFlagClickedListener mCountryFlagClickedListener = new OnCountryFlagClickedListener();
    private final OnCountrySelectedListener mOnCountrySelectedListener;


    public SelectCountryDialog(Context context, final OnCountrySelectedListener pOnCountrySelectedListener) {
        super(context);

        mOnCountrySelectedListener = pOnCountrySelectedListener;

        setContentView(R.layout.dialog_select_country);
        ButterKnife.inject(this);

        // dialog will fill the screen horizontally
        getWindow().setLayout(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);

        setTitle(R.string.select_country_dialog_title);

        initGrid();
    }

    private void initGrid() {

        mCountriesGV.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.abc_list_menu_item_icon, Locale.getISOCountries()){

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
                try {
                    final String filename = countryISOcode.toLowerCase() + ".png";
                    final Drawable flag = Drawable.createFromStream(getContext().getAssets().open(getContext().getString(R.string.flag_asset_folder_path) + filename), filename);

                    ((ImageView) convertView).setImageDrawable(flag);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return convertView;
            }
        });
    }

    private class OnCountryFlagClickedListener implements View.OnClickListener {

        @Override
        public void onClick(final View pCountryIB) {

            mOnCountrySelectedListener.onCountrySelected((String)pCountryIB.getTag(), ((ImageView)pCountryIB).getDrawable());
            dismiss();
        }
    }

    public interface OnCountrySelectedListener {

        public void onCountrySelected(final String pCountryISOcode, final Drawable pCountryFlag);
    }
}
