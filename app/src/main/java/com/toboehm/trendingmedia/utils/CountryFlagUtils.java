package com.toboehm.trendingmedia.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.toboehm.trendingmedia.R;

import java.io.IOException;

/**
 * Created by Tobias Boehm on 01.04.2015.
 */
public class CountryFlagUtils {

    private final Context mContext;


    public  CountryFlagUtils(final Context pContext){

        mContext = pContext;
    }

    public void setFlagDrawable(final String pCountryCode, final ImageView pFlagIV){

        // set flag from asset directory
        try {
            final String filename = pCountryCode.toLowerCase() + ".png";
            final Drawable flag = Drawable.createFromStream(mContext.getAssets().open(mContext.getString(R.string.flag_asset_folder_path) + filename), filename);

            pFlagIV.setImageDrawable(flag);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
