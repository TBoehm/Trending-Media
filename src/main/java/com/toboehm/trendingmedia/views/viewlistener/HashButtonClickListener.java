package com.toboehm.trendingmedia.views.viewlistener;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.toboehm.trendingmedia.MainActivity;
import com.toboehm.trendingmedia.R;

/**
* Created by Tobias Boehm on 31.03.2015.
*/
public class HashButtonClickListener implements Button.OnClickListener{

    private MainActivity mMainActivity;

    public HashButtonClickListener(final MainActivity pMainAcitivty) {
        this.mMainActivity = pMainAcitivty;
    }

    @Override
    public void onClick(View v) {

        final Button trendButton = (Button)v;

        // toggle hashtag state and act on new state
        if(mMainActivity.getViewModel().toggleTrend(trendButton.getText().toString())){

            // if hashtag is active now -> load corresponding media
            trendButton.setTextColor(trendButton.getContext().getResources().getColor(R.color.hashtag_active));
            Toast.makeText(mMainActivity, "Load media for hashtag " + trendButton.getText(), Toast.LENGTH_SHORT).show();

        }else{

            // if hashtag is inactive now -> remove corresponding media
            trendButton.setTextColor(trendButton.getContext().getResources().getColor(R.color.hashtag_inactive));
            Toast.makeText(mMainActivity, "Remove media for hashtag " + trendButton.getText(), Toast.LENGTH_SHORT).show();
        }
    }
}
