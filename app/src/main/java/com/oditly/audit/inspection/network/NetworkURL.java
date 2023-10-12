package com.oditly.audit.inspection.network;


/**
 * Created by Vikas on 4/31/2020.
 */

public class NetworkURL {

    //API URL'S LIVE SERVER
    public static final String API_BASE_URL_NEW= "https://api.account.oditly.com/";
    public static final String API_BASE_URL = "https://api.account.oditly.com/m/";


    //API URL'S STAGE SERVER OLD
    //public static final String API_BASE_URL_NEW= "https://api.stage.account.oditly.com/";
    // public static final String API_BASE_URL = "https://api.stage.account.oditly.com/m/";


    //API URL'S STAGE SERVER NEW
    //  public static final String API_BASE_URL_NEW= "https://api.account.stage.oditly.com/";
    // public static final String API_BASE_URL = "https://api.account.stage.oditly.com/m/";



    //API URL'S  TEST SERVER
    //  public static final String API_BASE_URL_NEW= "https://api.dev.account.oditly.com/";
    //public static final String API_BASE_URL = "https://api.dev.account.oditly.com/m/";





    public static final String SIGNIN = API_BASE_URL + "login";
    public static final String LOGOUT = API_BASE_URL + "logout";
    public static final String RESETPASSWORD = API_BASE_URL + "password/change";
    public static final String CHANGEPASSWORD = API_BASE_URL + "password";
    public static final String SENDOTP = API_BASE_URL + "send_otp";
    public static final String AUDIT_TYPE_LIST = API_BASE_URL + "ia/audit/types";
    //public static final String BSEDITATTACHMENT = API_BASE_URL + "ia/brand_standard/file/description/edit";
    // public static final String BSDELETEATTACHMENT = API_BASE_URL + "ia/brand_standard/file/delete";
    //public static final String BSATTACHMENT = API_BASE_URL + "ia/brand_standard/file";
    //public static final String BSATTACHMENT_UPDATE = API_BASE_URL + "ia/brand_standard/file/update";
    public static final String BRANDSTANDARD_FINAL_SAVE = API_BASE_URL_NEW + "internal/audit/answer";
    public static final String BRANDSTANDARD_SECTION_SAVE = API_BASE_URL_NEW + "ia/section/answer";
    //public static final String GET_REPORT_URL=API_BASE_URL_NEW+"report/ia/audit/dashboard/pdf?audit_id=";
    public static final String GET_REPORT_URL_NON_COMPLANCE=API_BASE_URL_NEW+"report/ia/audit/overall/pdf?audit_id="; //for Noncompliance audit report
    public static final String GET_ACTION_FILTER_URL=API_BASE_URL_NEW+ "ia/action-plan/create?audit_id=";
    public static final String POST_ACTION_ADD_URL=API_BASE_URL_NEW+ "ia/action-plan";
    //audit_id section_group_id , section_id, title   action_id, planned_date,assigned_user_id[],action_details,cc_emails 9 params
    public static final String POST_ACTIONFILE_ADD_URL=API_BASE_URL_NEW+ "ia/action-plan/file";
    public static final String POST_ACTIONFILE_URL=API_BASE_URL_NEW+ "ia/action-plan/completion-file";
    public static final String OKTA_DEV_URL="https://oditly-local.web.app/?providerId=";






    public static final String CHECK_USER = API_BASE_URL + "check-user";

    public static final String RESET_PASSWORD_NEW= API_BASE_URL_NEW+ "reset-password";
    public static final String AUDIT_LIST = API_BASE_URL + "ia/audits";
    public static final String BS_EDIT_ATTACHMENT_NEW = API_BASE_URL_NEW + "internal-audit/question/file/description";
    public static final String BS_DELETE_ATTACHMENT_NEW = API_BASE_URL_NEW + "internal-audit/question/file/delete";


    public static final String BS_FILE_UPLOAD_LISTGET_NEW = API_BASE_URL_NEW + "internal-audit/question/file";

    public static final String BSATTACHMENT_UPDATE_NEW = API_BASE_URL_NEW + "internal-audit/question/file/update";

    public static final String AUDIT_INTERNAL_SIGNATURE = API_BASE_URL + "ia/signature/file";
    public static final String BRANDSTANDARD = API_BASE_URL_NEW + "internal-audit/questions";
    public static final String BRANDSTANDARD_FINAL_SAVE_NEW = API_BASE_URL_NEW + "internal-audit/submit";
    public static final String BRANDSTANDARD_SECTION_SAVE_NEW = API_BASE_URL_NEW + "internal-audit/section/submit";


    public static final String ACTION_PLAN = API_BASE_URL_NEW+ "ia/action-plans";   //remove new as discuss with Manish
    public static final String ACTION_PLAN_COMPLETE= API_BASE_URL_NEW+"ia/action-plan/complete";


    public static final String APP_VERSION = API_BASE_URL + "appversion";
    public static final String GET_TEAM_LIST_ADD = API_BASE_URL_NEW+ "team/create?";
    public static final String GET_TEAM_LIST = API_BASE_URL_NEW+ "teams";
    public static final String POST_TEAM_MEMBER = API_BASE_URL_NEW+ "team";
    public static final String GET_TEAM_MEMBER = API_BASE_URL_NEW+ "team?";
    public static final String GET_FILTER_DATA= API_BASE_URL_NEW+"ia/report/dashboard/filter";
    public static final String GET_REPORT_URL=API_BASE_URL_NEW+"report/ia/audit/overall/pdf?audit_id="; //for complete audit report
    public static final String GET_DASHBOARD_URL=API_BASE_URL_NEW+"ia/report/dashboard?";
    public static final String AUDIT_LOCATION_LIST =API_BASE_URL+"filter/location?";
    public static final String GET_AUDITCREATEFILTER_URL=API_BASE_URL_NEW+ "internal/audit/create?location_id=";
    public static final String POST_AUDIT_ADD_URL=API_BASE_URL_NEW+ "internal/audit";
    public static final String GET_ACTIONCREATE_USING_LOCATION_API=API_BASE_URL_NEW+"ia/action-plan/create?location_id=";
    public static final String GET_ACTIONCREATE_USING_AUDIT_API=API_BASE_URL_NEW+"ia/action-plan/create?audit_id=";
    public static final String ACTION_PLAN_ADD_ADHOC = API_BASE_URL_NEW + "ia/action-plan";
    public static final String ACTION_PLAN_ADD_NEW = API_BASE_URL_NEW +"internal-audit/action-plan";

    public static final String ACTION_PLAN_ADD_COMMENT = API_BASE_URL_NEW + "ia/action-plan/update-comment";
    public static final String ACTION_PLAN_COMMENT_LIST = API_BASE_URL_NEW + "ia/action-plan/update-comments?action_plan_id=";

    public static final String POST_FCM_TOKEN = API_BASE_URL_NEW+"user/notification-token";
    public static final String GET_PROFILE_DATA = API_BASE_URL_NEW+"profile";
    public static final String POST_UPDATE_PROFILE_LANG = API_BASE_URL_NEW+"profile";
    public static final String GET_LANGUAGE_LIST = API_BASE_URL_NEW+"user/languages";



    public static final String GET_ACTIONPLAN_DATA = API_BASE_URL_NEW+"ia/action-plan?action_plan_id=";
    public static final String BRANDSTANDARD_QUESTIONWISE_ANSWER = API_BASE_URL_NEW+"internal-audit/question";



    public static final String GET_RefreshToke_OKTA_URL="https://securetoken.googleapis.com/v1/token?key=";
    public static final String OKTA_LIVE_URL="https://oditly.firebaseapp.com/?providerId=";
    public static final String URL_CHATSUPPORT ="https://go.crisp.chat/chat/embed/?website_id=e6111076-aafa-4cf4-830e-e6582700d1dc";
    public static final String URL_PRIVACY_POLICY="https://www.oditly.com/privacy_policy";
    public static final String URL_TERM_AND_CONDITION="https://www.oditly.com/terms_of_use";
    public static final String POST_DEMO_URL="https://www.oditly.com/demo_form.php";




    // audit_id,action_plan_id,file





}