package com.oditly.audit.inspection.ui.activty;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;


import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.util.AppConstant;

public class AnimationActivity extends AppCompatActivity {
    private String  mAuditTypeId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
       // mAuditTypeId=getIntent().getStringExtra(AppConstant.AUDIT_TYPE_ID);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                sentNewActivity();

            }
        }, 800);




        // sentNewActivity();

    }


    private void  sentNewActivity()
    {
        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(AppConstant.AUDIT_TYPE_ID,"0");
        startActivity(intent);
        finish();
        //overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

}
