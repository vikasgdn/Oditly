package com.oditly.audit.inspection.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.network.apirequest.ActionCompleteRequestBean;
import com.oditly.audit.inspection.util.AppConstant;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkServiceMultipartActionComplete {
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public ArrayList<File> mMediaFileList;
    private String mMediaType = "image/jpeg";
    /* access modifiers changed from: private */
    public ActionCompleteRequestBean mRequestBean;
    /* access modifiers changed from: private */
    public INetworkEvent networkEvent;
    /* access modifiers changed from: private */
    public String service;

    public NetworkServiceMultipartActionComplete(String serviceName, ActionCompleteRequestBean bean, ArrayList<File> file, INetworkEvent networkEvent2, Context context) {
        this.service = serviceName;
        this.networkEvent = networkEvent2;
        this.mContext = context;
        this.mRequestBean = bean;
        this.mMediaFileList = file;
    }

    public void call(NetworkModel input) {
        new NetworkTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new NetworkModel[]{input});
    }

    private class NetworkTask extends AsyncTask<NetworkModel, Void, String> {
        boolean isError;
        String message;

        private NetworkTask() {
            this.isError = false;
            this.message = "";
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            if (NetworkServiceMultipartActionComplete.this.networkEvent != null) {
                NetworkServiceMultipartActionComplete.this.networkEvent.onNetworkCallInitiated(NetworkServiceMultipartActionComplete.this.service);
            }
        }

        /* access modifiers changed from: protected */
        public String doInBackground(NetworkModel... networkModels) {
            try {
                OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(40, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build();
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                builder.addFormDataPart(NetworkConstant.REQ_PARAM_MOBILE, NetworkServiceMultipartActionComplete.this.mRequestBean.getMobile());
                builder.addFormDataPart(NetworkConstant.REQ_PARAM_AUDIT_ID, NetworkServiceMultipartActionComplete.this.mRequestBean.getAudit_id());
                builder.addFormDataPart(NetworkConstant.REQ_PARAM_ACTION_PLANID, NetworkServiceMultipartActionComplete.this.mRequestBean.getAction_plan_id());
                builder.addFormDataPart(NetworkConstant.REQ_PARAM_COMMENT, NetworkServiceMultipartActionComplete.this.mRequestBean.getComplete_comment());
                for (int j = 0; j < NetworkServiceMultipartActionComplete.this.mMediaFileList.size(); j++) {
                    builder.addFormDataPart("files[]", "Oditly_" + System.currentTimeMillis() + AppConstant.IMAGE_EXTENSION, RequestBody.create(MediaType.parse("application/octet-stream"), (File) NetworkServiceMultipartActionComplete.this.mMediaFileList.get(j)));
                }
                RequestBody body = builder.build();
                Request request = new Request.Builder()
                        .url(NetworkURL.ACTION_PLAN_COMPLETE)
                        .method("POST", body)
                        .addHeader("access-token", AppPreferences.INSTANCE.getAccessToken(NetworkServiceMultipartActionComplete.this.mContext))
                        .addHeader("Authorization",AppPreferences.INSTANCE.getFirebaseAccessToken(mContext))
                        .build();
                Log.e("MULTIPART=====> ", "" + body.toString());
               // Log.e("MULTIPART=====> ", "https://api.account.oditly.com/ia/action-plan");
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    this.isError = false;
                    return response.body().string();
                }
                this.isError = true;
                this.message = "response error";
                return -1 + " " + this.message;
            } catch (Exception e) {
                this.isError = true;
                this.message = e.getMessage();
                e.printStackTrace();
                return -1 + " " + this.message;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String s) {
            if (NetworkServiceMultipartActionComplete.this.networkEvent == null) {
                return;
            }
            if (this.isError) {
                NetworkServiceMultipartActionComplete.this.networkEvent.onNetworkCallError(NetworkServiceMultipartActionComplete.this.service, this.message);
            } else {
                NetworkServiceMultipartActionComplete.this.networkEvent.onNetworkCallCompleted("", NetworkServiceMultipartActionComplete.this.service, s);
            }
        }
    }
}
