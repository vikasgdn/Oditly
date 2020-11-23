package com.oditly.audit.inspection.localDB;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.oditly.audit.inspection.localDB.bsoffline.BsOfflineDBImpl;
import com.oditly.audit.inspection.localDB.media.MediaDBImpl;

public class LocalDB {
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "AppLocalDB";



    public LocalDB(Context context) {
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try
            {
                db.execSQL(MediaDBImpl.CREATE_TABLE_MEDIA_DETAILS);
                db.execSQL(BsOfflineDBImpl.CREATE_TABLE_AUDIT_LIST);
                db.execSQL(BsOfflineDBImpl.CREATE_TABLE_AUDIT_QUESTION);
                db.execSQL(BsOfflineDBImpl.CREATE_TABLE_BRANDSECTION);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP table if exists " + MediaDBImpl.TBL_MEDIA_DETAILS);
            db.execSQL("DROP table if exists " + BsOfflineDBImpl.TBL_AUDIT_LIST);
            db.execSQL("DROP table if exists " + BsOfflineDBImpl.TBL_AUDIT_QUESTION);
            db.execSQL("DROP table if exists " + BsOfflineDBImpl.TBL_BRAND_SECTION);
            onCreate(db);
        }

    }

    public SQLiteDatabase open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return db;
    }

    public void close() {
        DBHelper.close();
    }






}