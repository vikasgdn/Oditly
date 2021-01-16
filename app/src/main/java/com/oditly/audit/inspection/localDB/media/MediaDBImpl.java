package com.oditly.audit.inspection.localDB.media;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.oditly.audit.inspection.localDB.LocalDB;
import java.util.ArrayList;

/**
 * Created by Vikas
 */
public class MediaDBImpl implements MediaDB {

    private static final String QUESTION_ID = "questionid";
    public static final String SECTION_GRP_ID = "sectiongrpid";
    public static final String SECTION_ID = "sectionid";
    public static final String AUDIT_ID = "aid";
    public static final String MEDIA = "mediafile";
    public static final String CREATED_AT = "createdad";

    public static final String TBL_MEDIA_DETAILS = "tblmediaDetails";
    /* Card Details Table */


    /*Create Query*/
    public static final String CREATE_TABLE_MEDIA_DETAILS = "create table " + TBL_MEDIA_DETAILS +
            "(sno integer primary key autoincrement," +
            AUDIT_ID + " text," +
            SECTION_ID + " text, " +
            SECTION_GRP_ID + " text, " +
            QUESTION_ID + " text," +
            MEDIA + " text, " +
            CREATED_AT + " text);";




    private LocalDB localDB;
    private MediaDBImpl(Context context) {
        localDB = new LocalDB(context);
    }

    private static MediaDBImpl mediaDB;

    public static MediaDBImpl getInstance(Context context) {
        if (mediaDB == null) {
            return new MediaDBImpl(context);
        } else
            return mediaDB;
    }

    @Override
    public long addMediaToDB(String auditid,String questionId, String sctionid, String sectionGroupId, String uri) {
        SQLiteDatabase db = localDB.open();

        ContentValues values = new ContentValues();
        values.put(AUDIT_ID, auditid);
        values.put(QUESTION_ID, questionId);
        values.put(SECTION_ID, sctionid);
        values.put(SECTION_GRP_ID, sectionGroupId);
        values.put(MEDIA, uri);
        long rowId = db.insert(TBL_MEDIA_DETAILS, null, values);
        localDB.close();

        return rowId;
    }

    @Override
    public long updateMediaToDB(String cardId, String name, String cardNumber, String expiryDate) {
        return 0;
    }

    @Override
    public long deleteMedia(String cardId) {
        SQLiteDatabase db = localDB.open();

        ContentValues values = new ContentValues();
        values.put(AUDIT_ID, 0);
        values.put(QUESTION_ID, System.currentTimeMillis());

        long rowId = db.update(TBL_MEDIA_DETAILS, values, AUDIT_ID+"="+cardId, null);

        localDB.close();

        return rowId;
    }

    @Override
    public ArrayList<Uri> getImageList(String auditId,String setionID,String sectionGroupID,String qstinId) {
        SQLiteDatabase db = localDB.open();
        ArrayList<Uri> mediaList = new ArrayList<>();

        String select = "SELECT * FROM " + TBL_MEDIA_DETAILS + " WHERE " + AUDIT_ID + " = ? AND " + SECTION_ID + " = ?AND " + SECTION_GRP_ID + " = ?AND " + QUESTION_ID + " = ?";

        Cursor cursor = db.rawQuery(select, new String[]{auditId, setionID,sectionGroupID,qstinId});
        if (cursor != null) {
            if (cursor.moveToFirst())
            {
                do {
                   mediaList.add(Uri.parse(cursor.getString(cursor.getColumnIndex(MEDIA))));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        localDB.close();

        return mediaList;
    }




}
