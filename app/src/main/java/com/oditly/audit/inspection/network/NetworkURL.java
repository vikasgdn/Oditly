package com.oditly.audit.inspection.network;

/**
 * Created by Vikas on 4/31/2020.
 */

public class NetworkURL {

    //API URL'S LIVE SERVER
    public static final String API_BASE_URL_NEW= "https://api.account.oditly.com/";
    public static final String API_BASE_URL = "https://api.account.oditly.com/m/";


    //API URL'S  TEST SERVER
   // public static final String API_BASE_URL_NEW= "https://api.dev.account.oditly.com/";
   // public static final String API_BASE_URL = "https://api.dev.account.oditly.com/m/";




    public static final String URL_CHATSUPPORT ="https://go.crisp.chat/chat/embed/?website_id=e6111076-aafa-4cf4-830e-e6582700d1dc";
    public static final String SIGNIN = API_BASE_URL + "login";
    public static final String CHECK_USER = API_BASE_URL + "check-user";
    public static final String LOGOUT = API_BASE_URL + "logout";
    public static final String RESETPASSWORD = API_BASE_URL + "password/change";
    public static final String CHANGEPASSWORD = API_BASE_URL + "password";
    public static final String SENDOTP = API_BASE_URL + "send_otp";

    public static final String AUDIT_LIST = API_BASE_URL + "ia/audits";
    public static final String AUDIT_TYPE_LIST = API_BASE_URL + "ia/audit/types";

    public static final String BSEDITATTACHMENT = API_BASE_URL + "ia/brand_standard/file/description/edit";
    public static final String BSDELETEATTACHMENT = API_BASE_URL + "ia/brand_standard/file/delete";


    public static final String BSATTACHMENT = API_BASE_URL + "ia/brand_standard/file";
    public static final String BSATTACHMENT_UPDATE = API_BASE_URL + "ia/brand_standard/file/update";

    public static final String AUDIT_INTERNAL_SIGNATURE = API_BASE_URL + "ia/signature/file";

    public static final String BRANDSTANDARD = API_BASE_URL + "ia/brand_standard";
    public static final String ACTION_PLAN = API_BASE_URL + "ia/action-plans";
    public static final String ACTION_PLAN_COMPLETE= API_BASE_URL_NEW+"ia/action-plan/complete";
    public static final String APP_VERSION = API_BASE_URL + "appversion";
    public static final String GET_TEAM_LIST_ADD = API_BASE_URL_NEW+ "team/create?";
    public static final String GET_TEAM_LIST = API_BASE_URL_NEW+ "teams";
    public static final String POST_TEAM_MEMBER = API_BASE_URL_NEW+ "team";
    public static final String GET_TEAM_MEMBER = API_BASE_URL_NEW+ "team?";
    public static final String GET_FILTER_DATA= API_BASE_URL_NEW+"ia/report/dashboard/filter";
    public static final String GET_REPORT_URL=API_BASE_URL_NEW+"report/ia/audit/dashboard/pdf?audit_id=";
    public static final String GET_DASHBOARD_URL=API_BASE_URL_NEW+"ia/report/dashboard?";
    public static final String AUDIT_LOCATION_LIST =API_BASE_URL+"filter/location?";
    public static final String GET_AUDITCREATEFILTER_URL=API_BASE_URL_NEW+ "/internal/audit/create?location_id=";
    public static final String POST_AUDIT_ADD_URL=API_BASE_URL_NEW+ "internal/audit";
    public static final String GET_ACTION_FILTER_URL=API_BASE_URL_NEW+ "ia/action-plan/create?audit_id=";
    public static final String POST_ACTION_ADD_URL=API_BASE_URL_NEW+ "ia/action-plan";
  //audit_id section_group_id , section_id, title   action_id, planned_date,assigned_user_id[],action_details,cc_emails 9 params
  public static final String POST_ACTIONFILE_ADD_URL=API_BASE_URL_NEW+ "ia/action-plan/file";
    public static final String POST_ACTIONFILE_URL=API_BASE_URL_NEW+ "ia/action-plan/completion-file";
    public static final String POST_DEMO_URL="https://www.oditly.com/demo_form.php";

    // audit_id,action_plan_id,file




}
