package com.oditly.audit.inspection.localDB.media;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by Vengat(TechFidElite).
 */
public interface MediaDB {
    long addMediaToDB(String auditid, String questionId, String sctionid, String sectionGroupId, String media);

    long updateMediaToDB(String cardId, String name, String cardNumber, String expiryDate);

    long deleteMedia(String cardId);

    ArrayList<Uri> getImageList(String auditId, String setionID, String sectionGroupID, String qstinId);


}
