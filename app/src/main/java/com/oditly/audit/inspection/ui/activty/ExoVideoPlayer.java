package com.oditly.audit.inspection.ui.activty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.view.CustomVideoPlayer;


public class ExoVideoPlayer extends BaseActivity implements CustomVideoPlayer.PlaybackListener
{

    private String url;
    private String mFromWhere="";
    private CustomVideoPlayer customVideoPlayer;
    public static void start(Context context, String data,String location) {
        Intent i = new Intent(context, ExoVideoPlayer.class);
        i.putExtra(AppConstant.FILE_URL, data);
        i.putExtra(AppConstant.FROMWHERE, location);
        context.startActivity(i);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  hideSystemUI();
      //  setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_exoplayer);

        initView();
    }

    @Override
    protected void initView() {
        super.initView();
         Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = getIntent().getExtras().getString(AppConstant.FILE_URL);
            mFromWhere= getIntent().getExtras().getString(AppConstant.FROMWHERE);
        }
        TextView mTitleTV=(TextView) findViewById(R.id.tv_header_title);

        if (!TextUtils.isEmpty(mFromWhere) && mFromWhere.equalsIgnoreCase(AppConstant.ACTIONPLAN))
            mTitleTV.setText(getString(R.string.text_attachment));

        findViewById(R.id.iv_header_left).setOnClickListener(this);
        customVideoPlayer = findViewById(R.id.customVideoPlayer);
        customVideoPlayer.setMediaUrl(url);
        customVideoPlayer.enableAutoMute(false);
        customVideoPlayer.enableAutoPlay(true);    // u can set auto play true accoding need
        customVideoPlayer.setMinHeight(300);
        customVideoPlayer.hideControllers(false);
        customVideoPlayer.setOnPlaybackListener(this);
        //customVideoPlayer.setOrientation(LinearLayout.VERTICAL);
        customVideoPlayer.build();


    }
    @Override
    public void onClick(View view) {
        super.onClick(view);
        finish();
    }

    @Override
    public void onVolumeChange(boolean volumeOn)
    {


    }

    @Override
    public void onPlayEvent() {

    }

    @Override
    public void onPauseEvent() {

    }

    @Override
    public void onCompletedEvent() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(customVideoPlayer!=null)
            customVideoPlayer.play();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(customVideoPlayer!=null && customVideoPlayer.isPlaying())
            customVideoPlayer.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(customVideoPlayer!=null && customVideoPlayer.isPlaying())
            customVideoPlayer.stop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(customVideoPlayer!=null && customVideoPlayer.isPlaying())
            customVideoPlayer.stop();
    }

}
