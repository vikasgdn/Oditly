package com.oditly.audit.inspection.ui.activty;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;

import com.oditly.audit.inspection.util.AppUtils;
import com.oditly.audit.inspection.util.Headers;


import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageViewActivity extends BaseActivity {

    @BindView(R.id.image_view)
    ImageView imageView;
    private Context context;
    String image;
    TextView mTitalTV;
    private ProgressDialog progressDialog;
    public static final String TAG = ImageViewActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        context = this;
        ButterKnife.bind(ImageViewActivity.this);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        mTitalTV = findViewById(R.id.tv_header_title);
        findViewById(R.id.iv_header_left).setOnClickListener(this);

        imageView = findViewById(R.id.image_view);
        image = getIntent().getStringExtra("fileUrl");
        progressDialog.show();
        Glide.with(context)
                .load(Headers.getUrlWithHeaders(image, AppPreferences.INSTANCE.getAccessToken(this)))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressDialog.dismiss();
                        AppUtils.toast(ImageViewActivity.this, "Can't open this file");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressDialog.dismiss();
                        return false;
                    }
                })
                .into(imageView);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        finish();
    }
}
