package com.oditly.audit.inspection.network;

/**
 * Created by Nitish on 3/31/2018.
 */

public class NetworkURL {

    //API URL'S
    public static final String API_BASE_URL_NEW= "https://api.account.oditly.com/";
    public static final String API_BASE_URL = "https://api.account.oditly.com/m/";



    public static final String SIGNIN = API_BASE_URL + "login";
    public static final String CHECK_USER = API_BASE_URL + "check-user";
    public static final String LOGOUT = API_BASE_URL + "logout";
    public static final String RESETPASSWORD = API_BASE_URL + "password/change";
    public static final String CHANGEPASSWORD = API_BASE_URL + "password";
    public static final String SENDOTP = API_BASE_URL + "send_otp";

    public static final String AUDIT_LIST = API_BASE_URL + "ia/audits";
    public static final String AUDIT_TYPE_LIST = API_BASE_URL + "ia/audit/types";


    public static final String AUDITDETAILEDSUMMARY = API_BASE_URL + "ia/detailed_summary";
    public static final String AUDITEXECUTIVESUMMARY = API_BASE_URL + "ia/executive_summary";


    public static final String BSEDITATTACHMENT = API_BASE_URL + "ia/brand_standard/file/description/edit";
    public static final String DSEDITATTACHMENT = API_BASE_URL + "ia/detailed_summary/file/description/edit";
    public static final String ESEDITATTACHMENT = API_BASE_URL + "ia/executive_summary/file/description/edit";

    public static final String BSDELETEATTACHMENT = API_BASE_URL + "ia/brand_standard/file/delete";
    public static final String DSDELETEATTACHMENT = API_BASE_URL + "ia/detailed_summary/file/delete";
    public static final String ESDELETEATTACHMENT = API_BASE_URL + "ia/executive_summary/file/delete";


    public static final String BSATTACHMENT = API_BASE_URL + "ia/brand_standard/file";
    public static final String DSATTACHMENT = API_BASE_URL + "ia/detailed_summary/file";
    public static final String ESATTACHMENT = API_BASE_URL + "ia/executive_summary/file";

    public static final String AUDIT_INTERNAL_SIGNATURE = API_BASE_URL + "ia/signature/file";

    public static final String BRANDSTANDARD = API_BASE_URL + "ia/brand_standard";
    public static final String ACTION_PLAN = API_BASE_URL + "ia/action-plans";
    public static final String ACTION_PLAN_COMPLETE= "https://api.account.oditly.com/ia/action-plan/complete";
    public static final String APP_VERSION = API_BASE_URL + "appversion";
    public static final String GET_TEAM_LIST_ADD = API_BASE_URL_NEW+ "team/create?";
    public static final String GET_TEAM_LIST = API_BASE_URL_NEW+ "teams";
    public static final String POST_TEAM_MEMBER = API_BASE_URL_NEW+ "team";
    public static final String GET_TEAM_MEMBER = API_BASE_URL_NEW+ "team?";
    public static final String GET_FILTER_DATA= API_BASE_URL_NEW+"ia/report/dashboard/filter";
    public static final String GET_REPORT_URL=API_BASE_URL_NEW+"report/ia/audit/dashboard/pdf?audit_id=";





}
