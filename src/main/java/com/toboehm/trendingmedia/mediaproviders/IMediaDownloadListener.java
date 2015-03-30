package com.toboehm.trendingmedia.mediaproviders;

/**
 * Created by Tobias Boehm on 30.03.2015.
 */
public interface IMediaDownloadListener<Media> {

    public void onMediaDownloaded(final Media pMedia);
}
