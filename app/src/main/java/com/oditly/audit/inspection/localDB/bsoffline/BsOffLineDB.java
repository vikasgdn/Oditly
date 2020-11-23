package com.oditly.audit.inspection.localDB.bsoffline;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by Vikas.
 */
public interface BsOffLineDB {
    long saveOffileQuestionJSONToDB(String auditid, String response);
    String getOffileQuestionJSONToDB(String auditid);
    long deleteOfflineQuestionJSON(String auditid);



    long saveAuditListJSONToDB(String type, String response);
    String getAuditListJSONFromDB(String type);
    long deleteAuditListJSONToDB(String type);

    long saveBrandSectionJSONToDB(String type, String response);
    long deleteBrandSectionJSONToDB(String type);
    String getBrandSectionJSON(String type);


}
