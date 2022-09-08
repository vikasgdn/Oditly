package com.oditly.audit.inspection.apppreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;



/**
 * @author Madstech enum for SharedPreference of application because it will
 *         use through out the app.
 */
public enum AppPreferences
{
    INSTANCE;
    private static final String SHARED_PREFERENCE_NAME = "oditlyAppPreference";
    private SharedPreferences mPreferences;
    private Editor mEditor;

    /**
     * private constructor for singleton class
     *
     * @param context
     */
    public void initAppPreferences(Context context)
    {
        mPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public String getSelectedLang()
    {
        return mPreferences.getString(SharedPreferencesKeys.language.toString(), "en");
    }

    public void setSelectedLang(String value)
    {
        mEditor.putString(SharedPreferencesKeys.language.toString(), value);
        mEditor.commit();
    }


    public String getProviderName()
    {
        return mPreferences.getString(SharedPreferencesKeys.provider_name.toString(), "");
    }

    public void setProviderName(String value)
    {
        mEditor.putString(SharedPreferencesKeys.provider_name.toString(), value);
        mEditor.commit();
    }

    public int getUserId(Context context)
    {
        if(mPreferences==null)
            mPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

        return mPreferences.getInt(SharedPreferencesKeys.userId.toString(), 0);
    }

    public void setUserId(int value,Context context)
    {
        if(mEditor==null)
            initAppPreferences(context);
        mEditor.putInt(SharedPreferencesKeys.userId.toString(), value);
        mEditor.commit();
    }
    public String getUserEmail(Context context)
    {
        if(mPreferences==null)
            mPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return mPreferences.getString(SharedPreferencesKeys.email.toString(), "");    }

    public void setUserEmail(String value)
    {
        mEditor.putString(SharedPreferencesKeys.email.toString(), value);
        mEditor.commit();
    }

    public String getUserFname(Context context)
    {
        return mPreferences.getString(SharedPreferencesKeys.fname.toString(), "");
    }

    public void setUserFName(String value)
    {
        mEditor.putString(SharedPreferencesKeys.fname.toString(), value);
        mEditor.commit();
    }


    public String getUserLName(Context context)
    {
        if(mPreferences==null)
            mPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return mPreferences.getString(SharedPreferencesKeys.lname.toString(), "");
    }

    public void setUserLName(String value,Context context)
    {
        if(mEditor==null)
            initAppPreferences(context);
        mEditor.putString(SharedPreferencesKeys.lname.toString(), value);
        mEditor.commit();
    }


    public boolean isLogin(Context context)
    {
        if(mPreferences==null)
            mPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return mPreferences.getBoolean(SharedPreferencesKeys.isLogin.toString(), false);
    }

    public void setLogin(boolean isLogin,Context context)
    {
        if(mEditor==null)
            initAppPreferences(context);

        mEditor.putBoolean(SharedPreferencesKeys.isLogin.toString(), isLogin);
        mEditor.commit();
    }


    public void setLastHitTime(Context context, long currentTimeMillis) {
        if(mEditor==null)
            initAppPreferences(context);
        mEditor.putLong(SharedPreferencesKeys.last_hit_time.toString(), currentTimeMillis);
        mEditor.commit();
    }

    public long getLastHitTime(Context context) {
        if(mPreferences==null)
            mPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return mPreferences.getLong(SharedPreferencesKeys.last_hit_time.toString(), 0);

    }

    public void setFirebaseAccessToken(String accessToken,Context context)
    {
        if(mEditor==null)
            initAppPreferences(context);

        mEditor.putString(SharedPreferencesKeys.firebase_accessToken.toString(), accessToken);
        mEditor.commit();
    }

    public String getAccessToken(Context context)
    {
        if(mPreferences==null)
            mPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return mPreferences.getString(SharedPreferencesKeys.accessToken.toString(), "");
    }
    public void setAccessToken(String accessToken,Context context)
    {
        if(mEditor==null)
            initAppPreferences(context);
        mEditor.putString(SharedPreferencesKeys.accessToken.toString(), accessToken);
        mEditor.commit();
    }

    public String getOktaToken(Context context)
    {
        if(mPreferences==null)
            mPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return mPreferences.getString(SharedPreferencesKeys.oktaToken.toString(), "");
    }
    public void setOktaToken(String accessToken,Context context)
    {
        if(mEditor==null)
            initAppPreferences(context);
        mEditor.putString(SharedPreferencesKeys.oktaToken.toString(), accessToken);
        mEditor.commit();
    }
    public long getOktaTokenExpireTime(Context context)
    {
        if(mPreferences==null)
            mPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return mPreferences.getLong(SharedPreferencesKeys.oktaToken_expire_time.toString(), System.currentTimeMillis());
    }
    public void setOktaTokenExpireTime(long accessToken,Context context)
    {
        if(mEditor==null)
            initAppPreferences(context);
        mEditor.putLong(SharedPreferencesKeys.oktaToken_expire_time.toString(), accessToken);
        mEditor.commit();
    }


    public  void setRefreshTokenOkta(String val) {
        mEditor.putString(SharedPreferencesKeys.refresh_token.toString(), val);
        mEditor.commit();
    }
    public  String geRefreshTokenOkta()
    {
        return mPreferences.getString(SharedPreferencesKeys.refresh_token.toString(), "");
    }

    public  void setFCMToken(String val) {
        mEditor.putString(SharedPreferencesKeys.fcm_token.toString(), val);
        mEditor.commit();
    }
    public  String getFCMToken()
    {
        return mPreferences.getString(SharedPreferencesKeys.fcm_token.toString(), "");

    }




    public int getUserRole(Context context)
    {
        if(mPreferences==null)
            mPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return mPreferences.getInt(SharedPreferencesKeys.user_role.toString(),0);
    }

    public void setUserRole(int userrole,Context context)
    {
        if(mEditor==null)
            initAppPreferences(context);
        mEditor.putInt(SharedPreferencesKeys.user_role.toString(), userrole);
        mEditor.commit();
    }



    /**
     * Used to clear all the values stored in preferences
     *
     * @return void
     */

    public void clearPreferences()
    {
        mEditor.clear();
        mEditor.commit();
    }




    /**
     * Enum for shared preferences keys to store various values
     *
     * @author Madstech
     */

    public enum SharedPreferencesKeys
    {
        userId,
        email,
        isLogin,
        dsdatabase,
        deviceToken,
        accessToken,
        provider_name,
        user_role,
        client_role_id,
        mobile,
        username,
        refresh_token,
        oktaToken_expire_time,
        pincode,
        city,
        dob,
        userpic,
        client_role_name,
        address,
        language,
        local_db,
        fname,
        lname,
        last_hit_time,
        esdatabase,
        fcm_token,
        oktaToken,
        firebase_accessToken,
        app_version
        ;


    }
}
