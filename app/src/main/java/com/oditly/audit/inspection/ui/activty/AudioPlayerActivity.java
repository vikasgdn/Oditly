package com.oditly.audit.inspection.ui.activty;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.jean.jcplayer.JcPlayerManagerListener;
import com.example.jean.jcplayer.general.JcStatus;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.view.CustomVideoPlayer;

import java.util.ArrayList;


public class AudioPlayerActivity extends BaseActivity implements JcPlayerManagerListener
{

    private String url="";
    private JcPlayerView customAudioPlayer;

    public static void start(Context context, String data) {
        Intent i = new Intent(context, AudioPlayerActivity.class);
        i.putExtra(AppConstant.FILE_URL, data);
        context.startActivity(i);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audioplayer);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        initView();
    }

    @Override
    protected void initView() {
        super.initView();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = getIntent().getExtras().getString(AppConstant.FILE_URL);
        }

      //  url ="https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_700KB.mp3";
        customAudioPlayer = (JcPlayerView) findViewById(R.id.jcplayer);

        ArrayList<JcAudio> jcAudios = new ArrayList<>();
        jcAudios.add(JcAudio.createFromURL("Show How",url));
        customAudioPlayer.initPlaylist(jcAudios, this);

        //customAudioPlayer.setJcPlayerManagerListener(this);

         findViewById(R.id.iv_cancel).setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        finish();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(customAudioPlayer!=null && customAudioPlayer.isPlaying())
            customAudioPlayer.kill();
    }

    @Override
    public void onCompletedAudio() {
        Log.e("::::  ON  ","onCompletedAudio");

    }

    @Override
    public void onContinueAudio(JcStatus jcStatus) {
        Log.e("::::  ON  ","onContinueAudio ");

    }

    @Override
    public void onJcpError(Throwable throwable) {
        Log.e("::::  ON  ","onJcpError");

    }

    @Override
    public void onPaused(JcStatus jcStatus) {
        Log.e("::::  ON  ","onPaused");

    }

    @Override
    public void onPlaying(JcStatus jcStatus) {
        Log.e("::::  ON  ","onPlaying");

    }

    @Override
    public void onPreparedAudio(JcStatus jcStatus) {
        Log.e("::::  ON  ","onPreparedAudio");

    }

    @Override
    public void onStopped(JcStatus jcStatus) {
        Log.e("::::  ON STOPPED ","");

    }

    @Override
    public void onTimeChanged(JcStatus jcStatus) {
        Log.e("::::  ON  ","onTimeChanged");
    }
}
