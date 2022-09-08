package com.oditly.audit.inspection;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;


import com.oditly.audit.inspection.model.LanguageBeanData;
import com.oditly.audit.inspection.model.actionData.ActionInfo;
import com.oditly.audit.inspection.model.audit.AuditInfo;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardQuestion;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardSection;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardSectionNew;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class OditlyApplication extends Application {

    private List<Uri> mAttachImageList;
    private ActionInfo mActionPlanData;
    private AuditInfo mAuditData;
    private List<BrandStandardSectionNew> mSubSectionList;

    private ArrayList<BrandStandardQuestion> mSubQuestionForOptions;
    private ArrayList<BrandStandardSection> mBrandStandardSectionList;
    private Context mContext;
    private boolean isGalleryDisable;
    private List<LanguageBeanData> mLanguageList;



    public List<Uri> getmAttachImageList()
    {
        return mAttachImageList;
    }

    public void setmAttachImageList(List<Uri> mAttachImageList) {
        this.mAttachImageList = mAttachImageList;
    }

    public ArrayList<BrandStandardSection> getmBrandStandardSectionList() {
        return mBrandStandardSectionList;
    }

    public void setmBrandStandardSectionList(ArrayList<BrandStandardSection> mBrandStandardSectionList) {
        this.mBrandStandardSectionList = mBrandStandardSectionList;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
      //  handleSSLHandshake();
    }


    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession session) {
                    Log.e("host", arg0+":"+session.getCipherSuite());
                    return arg0.compareTo("api.dev.account.oditly.com")==0;

                }
            });
        } catch (Exception ignored) {
        }
    }


    public ActionInfo getmActionPlanData() {
        return mActionPlanData;
    }

    public void setmActionPlanData(ActionInfo mActionPlanData) {
        this.mActionPlanData = mActionPlanData;
    }

    public AuditInfo getmAuditData() {
        return mAuditData;
    }

    public void setmAuditData(AuditInfo mAuditData) {
        this.mAuditData = mAuditData;
    }

    public Context getmContext() {
        return mContext;
    }


    public boolean isGalleryDisable() {
        return isGalleryDisable;
    }

    public void setGalleryDisable(boolean galleryDisable) {
        isGalleryDisable = galleryDisable;
    }

    public ArrayList<BrandStandardQuestion> getmSubQuestionForOptions() {
        return mSubQuestionForOptions;
    }

    public void setmSubQuestionForOptions(ArrayList<BrandStandardQuestion> mSubQuestionForOptions) {
        this.mSubQuestionForOptions = mSubQuestionForOptions;
    }

    public List<LanguageBeanData> getmLanguageList() {
        return mLanguageList;
    }

    public void setmLanguageList(List<LanguageBeanData> mLanguageList) {
        this.mLanguageList = mLanguageList;
    }
}
