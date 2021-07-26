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

    public String getLocalDB()
    {
        return mPreferences.getString(SharedPreferencesKeys.local_db.toString(), "");
    }

    public void setLocalDB(String value)
    {
        mEditor.putString(SharedPreferencesKeys.local_db.toString(), value);
        mEditor.commit();
    }

    public String getUserPic()
    {
        return mPreferences.getString(SharedPreferencesKeys.userpic.toString(), "");
    }

    public void setUserPic(String value)
    {
        mEditor.putString(SharedPreferencesKeys.userpic.toString(), value);
        mEditor.commit();
    }

    public String getUserMob()
    {
        return mPreferences.getString(SharedPreferencesKeys.mobile.toString(), "");
    }

    public void setUserMob(String value)
    {
        mEditor.putString(SharedPreferencesKeys.mobile.toString(), value);
        mEditor.commit();
    }

    public String getAddress()
    {
        return mPreferences.getString(SharedPreferencesKeys.address.toString(), "");
    }

    public void setAddress(String value)
    {
        mEditor.putString(SharedPreferencesKeys.address.toString(), value);
        mEditor.commit();
    }




    public String getCity()
    {
        return mPreferences.getString(SharedPreferencesKeys.city.toString(), "");
    }

    public void setCity(String value)
    {
        mEditor.putString(SharedPreferencesKeys.city.toString(), value);
        mEditor.commit();
    }



    public String getPincode()
    {
        return mPreferences.getString(SharedPreferencesKeys.pincode.toString(), "");
    }

    public void setPincode(String value)
    {
        mEditor.putString(SharedPreferencesKeys.pincode.toString(), value);
        mEditor.commit();
    }



    public String getDOB()
    {
        return mPreferences.getString(SharedPreferencesKeys.dob.toString(), "");
    }

    public void setDOB(String value)
    {
        mEditor.putString(SharedPreferencesKeys.dob.toString(), value);
        mEditor.commit();
    }


    public String getClientRoleName()
    {
        return mPreferences.getString(SharedPreferencesKeys.client_role_name.toString(), "");
    }

    public void setClientRoleName(String value)
    {
        mEditor.putString(SharedPreferencesKeys.client_role_name.toString(), value);
        mEditor.commit();
    }

    public int getClientRoleId(Context context)
    {
        if(mPreferences==null)
            mPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

        return mPreferences.getInt(SharedPreferencesKeys.client_role_id.toString(), 0);
    }

    public void setClientRoleId(int value)
    {
        mEditor.putInt(SharedPreferencesKeys.client_role_id.toString(), value);
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
/*
    public String getFirebaseAccessToken(Context context)
    {
        if(mPreferences==null)
            mPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return mPreferences.getString(SharedPreferencesKeys.firebase_accessToken.toString(), "");
    }*/

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


    public  String getDSLocalDB()
    {
        return mPreferences.getString(SharedPreferencesKeys.dsdatabase.toString(), "");

    }

    public  void setFCMToken(String val) {
        mEditor.putString(SharedPreferencesKeys.fcm_token.toString(), val);
        mEditor.commit();
    }
    public  String getFCMToken()
    {
        return mPreferences.getString(SharedPreferencesKeys.fcm_token.toString(), "");

    }

    public  void setESLocalDB(String val) {
        mEditor.putString(SharedPreferencesKeys.esdatabase.toString(), val);
        mEditor.commit();
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
        sessionId,
        userId,
        email,
        isLogin,
        dsdatabase,
        deviceToken,
        accessToken,
        appVersion,
        user_role,
        client_role_id,
        mobile,
        username,
        pincode,
        city,
        dob,
        userpic,
        client_role_name,
        address,
        local_db,
        fname,
        lname,
        last_hit_time,
        esdatabase,
        fcm_token,
        firebase_accessToken
        ;


    }
}
