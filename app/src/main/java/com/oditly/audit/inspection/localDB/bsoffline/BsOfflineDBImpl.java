package com.oditly.audit.inspection.localDB.bsoffline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.oditly.audit.inspection.localDB.LocalDB;
import com.oditly.audit.inspection.localDB.media.MediaDB;

import java.util.ArrayList;

/**
 * Created by Vikas
 */
public class BsOfflineDBImpl implements BsOffLineDB {

    public static final String AUDIT_ID = "auditid";
    public static final String JSON_QUESTION = "json";
    public static final String CREATED_AT = "createdat";

    public static final String AUDIT_TYPE = "audittype";
    public static final String JSON_AUDIT = "jsonaudit";


    public static final String TBL_AUDIT_LIST = "tblauditlist";
    public static final String TBL_AUDIT_QUESTION = "tblauditquestion";
    public static final String TBL_BRAND_SECTION = "tblbrandsection";

    /* Card Details Table */

    /*Create Query*/
    public static final String CREATE_TABLE_AUDIT_LIST = "create table " + TBL_AUDIT_LIST +
            "(sno integer primary key autoincrement," +
            AUDIT_TYPE + " text," +
            JSON_AUDIT + " text, " +
            CREATED_AT + " text);";

    /*Create Query*/
    public static final String CREATE_TABLE_BRANDSECTION = "create table " + TBL_BRAND_SECTION +
            "(sno integer primary key autoincrement," +
            AUDIT_TYPE + " text, " +
            JSON_AUDIT + " text);";

    /*Create Query*/
    public static final String CREATE_TABLE_AUDIT_QUESTION = "create table " + TBL_AUDIT_QUESTION +
            "(sno integer primary key autoincrement," +
            AUDIT_ID + " text," +
            JSON_QUESTION + " text, " +
            CREATED_AT + " text);";


    private LocalDB localDB;
    private BsOfflineDBImpl(Context context) {
        localDB = new LocalDB(context);
    }

    private static BsOfflineDBImpl mediaDB;

    public static BsOfflineDBImpl getInstance(Context context) {
        if (mediaDB == null) {
            return new BsOfflineDBImpl(context);
        } else
            return mediaDB;
    }

    @Override
    public long saveOffileQuestionJSONToDB(String auditid, String response) {
        SQLiteDatabase db = localDB.open();

        ContentValues values = new ContentValues();
        values.put(AUDIT_ID, auditid);
        values.put(JSON_QUESTION, response);
        long rowId = db.insert(TBL_AUDIT_QUESTION, null, values);
        localDB.close();

        return rowId;
    }

    @Override
    public String getOffileQuestionJSONToDB(String auditid) {
        SQLiteDatabase db = localDB.open();
        String jsonResponse = "";
        String select = "SELECT * FROM " + TBL_AUDIT_QUESTION+ " WHERE " + AUDIT_ID + " = ?";
        Cursor cursor = db.rawQuery(select, new String[]{auditid});
        if (cursor != null) {
            if (cursor.moveToFirst())
              jsonResponse=  cursor.getString(cursor.getColumnIndex(JSON_QUESTION));
            cursor.close();
        }
        localDB.close();
        System.out.println("QUESTION FROM DB "+jsonResponse);
        return jsonResponse;
    }

    @Override
    public long deleteOfflineQuestionJSON(String auditid) {
        SQLiteDatabase db = localDB.open();
      //  long rowId = db.delete(TBL_AUDIT_QUESTION, AUDIT_ID+"="+auditid, null);
        long rowId =  db.delete(TBL_AUDIT_QUESTION, AUDIT_ID + "=?", new String[]{auditid});
        localDB.close();
        return rowId;
    }

    @Override
    public long saveAuditListJSONToDB(String type, String response) {
        SQLiteDatabase db = localDB.open();
        long rowId=0;
        try {
            ContentValues values = new ContentValues();
            values.put(AUDIT_TYPE, type);
            values.put(JSON_AUDIT, response);
             rowId = db.insert(TBL_AUDIT_LIST, null, values);
            localDB.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return rowId;
    }

    @Override
    public String getAuditListJSONFromDB(String type) {
        SQLiteDatabase db = localDB.open();
        String jsonResponse = "";
        String select = "SELECT * FROM " + TBL_AUDIT_LIST+ " WHERE " + AUDIT_TYPE + " = ?";
        Cursor cursor = db.rawQuery(select, new String[]{type});
        if (cursor != null) {
            if (cursor.moveToFirst())
                jsonResponse=  cursor.getString(cursor.getColumnIndex(JSON_AUDIT));
            cursor.close();
        }
        localDB.close();
        System.out.println("QUESTION FROM DB "+jsonResponse);
        return jsonResponse;
    }

    @Override
    public long deleteAuditListJSONToDB(String type) {
        SQLiteDatabase db = localDB.open();
        try {
            long rowId = db.delete(TBL_AUDIT_LIST, AUDIT_TYPE + "=?", new String[]{type});
            localDB.close();
            return rowId;
        }
        catch (Exception e)
        {
            localDB.close();
            return 0;
        }
    }

    @Override
    public long saveBrandSectionJSONToDB(String type, String response) {
        SQLiteDatabase db = localDB.open();
        long rowId=0;
        try {
            ContentValues values = new ContentValues();
            values.put(AUDIT_TYPE, type);
            values.put(JSON_AUDIT, response);
            rowId = db.insert(TBL_BRAND_SECTION, null, values);
            localDB.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return rowId;
    }

    @Override
    public long deleteBrandSectionJSONToDB(String type) {
        SQLiteDatabase db = localDB.open();
        try {
            long rowId = db.delete(TBL_BRAND_SECTION, AUDIT_TYPE + "=?", new String[]{type});
            localDB.close();
            return rowId;
        }
        catch (Exception e)
        {
            localDB.close();
            return 0;
        }
    }

    @Override
    public String getBrandSectionJSON(String type) {
        SQLiteDatabase db = localDB.open();
        String jsonResponse = "";
        String select = "SELECT * FROM " + TBL_BRAND_SECTION+ " WHERE " + AUDIT_TYPE + " = ?";
        Cursor cursor = db.rawQuery(select, new String[]{type});
        if (cursor != null) {
            if (cursor.moveToFirst())
                jsonResponse=  cursor.getString(cursor.getColumnIndex(JSON_AUDIT));
            cursor.close();
        }
        localDB.close();
        System.out.println("QUESTION FROM DB "+jsonResponse);
        return jsonResponse;
    }
}
