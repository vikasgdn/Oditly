package com.oditly.audit.inspection.ui.activty;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.ui.activty.BaseActivity;


public class ExoPlayer extends BaseActivity
{

    SimpleExoPlayerView mExoPlayerView;
    SimpleExoPlayer mExoPlayer;
    //String videoUrl = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    String videoUrl = "https://api.gdiworldwide.com/assets/ia_audit/3032_20200221_213216_753761212779489.mp4";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exoplayer);

        String videoUrl=getIntent().getStringExtra("url");

        try {
            mExoPlayerView =  findViewById(R.id.exo_playerview);
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            Uri uri = Uri.parse(videoUrl);
            DefaultHttpDataSourceFactory defaultHttpDataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
            defaultHttpDataSourceFactory.getDefaultRequestProperties().set("access-token", AppPreferences.INSTANCE.getAccessToken(this));

            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(uri, defaultHttpDataSourceFactory, extractorsFactory, null, null);
            mExoPlayerView.setPlayer(mExoPlayer);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);

        }catch (Exception e){
            Log.e("ExoPlayerActivity","exoplayer error"+e.toString());
            e.printStackTrace();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }


}
