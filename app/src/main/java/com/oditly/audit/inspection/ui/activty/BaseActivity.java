package com.oditly.audit.inspection.ui.activty;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.util.AppUtils;


public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected Toolbar mToolbar;
    protected ProgressDialog mProgressDialog;
    public static final int READ_WRITE_STORAGE = 52;
    private ProgressBar progressBar;

    public void initToolbar(Toolbar toolbar) {
        mToolbar = toolbar;
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

    }
    @Override
    protected void onStart()
    {
        super.onStart();
        AppUtils.setApplicationLanguage(this,AppPreferences.INSTANCE.getSelectedLang());
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser!=null) {
            mUser.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                String idToken = task.getResult().getToken();
                                Log.e("======TOKEN START ", "" + idToken);
                                AppPreferences.INSTANCE.setFirebaseAccessToken("Bearer " + idToken, getApplicationContext());// ...
                            } else {
                                Log.e("======TOKEN ", "ErROR");
                            }
                        }
                    });
        }
    }

    protected void initView(){


    }

    protected void initVar() {

    }

    @Override
    public void onClick(View view) {

    }

    public void enableBackPressed() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //startActivity(new Intent(BaseActivity.this, MainActivity.class));
            }
        });
    }

    public void enableBack(boolean goBack) {
        if (mToolbar == null) {
            return;
        }
        if (goBack) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
            }
        }
    }

    protected void setTitle(String title) {
        if (mToolbar != null && title != null) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(title);
            }
        }
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void showProgressDialog(String title, String message, boolean cancelable) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (!isFinishing()) {

            mProgressDialog = ProgressDialog.show(this, title, message, false, cancelable);
        }
    }

    public void showProgressDialog() {
        showProgressDialog(null, getString(R.string.default_progress_dialog_message), true);
    }

    public void showAppProgressDialog(String title, String message, boolean cancelable) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (!isFinishing()) {

            mProgressDialog = ProgressDialog.show(getApplicationContext(), title, message, false, cancelable);

        }
    }

    public void showAppProgressDialog() {
        showProgressDialog(null, getString(R.string.default_progress_dialog_message), false);
    }


    public boolean requestPermission(String permission) {
        boolean isGranted = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        if (!isGranted) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, READ_WRITE_STORAGE);
        }
        return isGranted;
    }


    public void isPermissionGranted(boolean isGranted, String permission) {

    }

    public  void setStatusBarColor(String color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = ((Activity) this).getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }
}
