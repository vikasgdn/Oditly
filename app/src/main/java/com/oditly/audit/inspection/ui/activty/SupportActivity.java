package com.oditly.audit.inspection.ui.activty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.ui.activty.BaseActivity;
import com.oditly.audit.inspection.util.AppUtils;

public class SupportActivity extends BaseActivity {

    private TextView mTitleTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        AppPreferences.INSTANCE.initAppPreferences(this);
        initView();
        initVar();

    }
    @Override
    protected void initView() {
        super.initView();

        findViewById(R.id.iv_header_left).setOnClickListener(this);
        findViewById(R.id.tv_callus).setOnClickListener(this);
        findViewById(R.id.tv_email).setOnClickListener(this);
        findViewById(R.id.fb_chatsupport).setOnClickListener(this);
        TextView textView=(TextView)findViewById(R.id.tv_header_title);
        textView.setText(getResources().getString(R.string.s_support));

    }
    @Override
    protected void initVar() {
        super.initVar();

    }



    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {

            case R.id.iv_header_left:
                finish();
                break;
            case R.id.tv_callus:
                 openDialPad(this);
                break;

            case R.id.fb_chatsupport:
                Intent callIntent = new Intent(this, ChatSupportActivity.class);
                startActivity(callIntent);
                break;

        }


    }


    public static void openDialPad(Context context)
    {
        try {
            Uri number = Uri.parse("tel:9818857837");
            Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
            context.startActivity(callIntent);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void openEmailComposer(Activity context) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@oditly.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "App feedback");
            context.startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            AppUtils.toastDisplayForLong(context, "There are no email client installed on your device.");
        }
    }

    public void support(View view) {
        Intent callIntent = new Intent(this, ChatSupportActivity.class);
        startActivity(callIntent);
    }
}
