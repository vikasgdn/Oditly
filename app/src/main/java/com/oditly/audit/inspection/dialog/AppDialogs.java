package com.oditly.audit.inspection.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.oditly.audit.inspection.BuildConfig;
import com.oditly.audit.inspection.OditlyApplication;
import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.apppreferences.AppPreferences;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardSection;
import com.oditly.audit.inspection.ui.activty.AccountProfileActivity;
import com.oditly.audit.inspection.ui.activty.ActionCreateActivity;
import com.oditly.audit.inspection.ui.activty.ActionPlanLandingActivity;
import com.oditly.audit.inspection.ui.activty.AddTeamMemberActivity;
import com.oditly.audit.inspection.ui.activty.AuditCreateActivity;
import com.oditly.audit.inspection.ui.activty.AuditSubmitSignatureActivity;
import com.oditly.audit.inspection.ui.activty.BrandStandardAuditActivity;
import com.oditly.audit.inspection.ui.activty.BrandStandardAuditActivityPagingnation;
import com.oditly.audit.inspection.ui.activty.BrandStandardOptionsBasedQuestionActivity;
import com.oditly.audit.inspection.ui.activty.MainActivity;
import com.oditly.audit.inspection.ui.activty.OctaLoginActivity;
import com.oditly.audit.inspection.ui.activty.ScheduleDemoActivity;
import com.oditly.audit.inspection.ui.activty.SignInEmailActivity;
import com.oditly.audit.inspection.ui.activty.SignInPasswordActivity;
import com.oditly.audit.inspection.ui.activty.SplashActivity;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import java.util.HashSet;
import java.util.Locale;


/**
 * @author Madstech App dialog class use for application related dialog
 *         displaying feature
 */
public class AppDialogs
{


    /**
     * This method is use to show progress dialog when hit web service
     *
     * @param activity
     * @return void
     */
/*
    public static void answerShowDialog(final BrandStandardSection brandStandardSection,final BrandStandardAuditActivity activity) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle(activity.getString(R.string.app_name));
        dialog.setMessage("You have unsaved answered locally in your device. Would you like to sync them?");
        dialog.setPositiveButton("Save", new DialogInterface.OnCNetlickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                */
/*questionArrayList = brandStandardSection.getQuestions();
                subSectionArrayList = brandStandardSection.getSub_sections();*//*

                activity.setQuestionList(brandStandardSection.getQuestions());
                activity.setSubSectionQuestionList(brandStandardSection.getSub_sections());
                AppLogger.e("TAG", "Replace it in adapter");
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                activity.setQuestionList(brandStandardSection.getQuestions());
                activity.setSubSectionQuestionList(brandStandardSection.getSub_sections());
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }
*/


    public static void showOtpValidateDialog(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        // final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_bottomui);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (activity.getResources().getDisplayMetrics().widthPixels - activity.getResources().getDimension(R.dimen.d_10dp));
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        try {


            dialog.findViewById(R.id.iv_audit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent  intent =new Intent(activity, AuditCreateActivity.class);
                    activity.startActivity(intent);
                }
            });
            dialog.findViewById(R.id.iv_action).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent  intent =new Intent(activity, ActionCreateActivity.class);
                    activity.startActivity(intent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();

    }


    public static void passwordResetMessageDialog(final Activity activity,String message) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_password_rule);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (activity.getResources().getDisplayMetrics().widthPixels - activity.getResources().getDimension(R.dimen.d_10dp));
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView textView=dialog.findViewById(R.id.tv_dialog_message);
        dialog.findViewById(R.id.tv_pass_rule).setVisibility(View.GONE);
        textView.setText(""+message);
        try {

            dialog.findViewById(R.id.tv_no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.tv_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();

    }

    public static void brandstandardTitleMessageDialog(final Activity activity,String title,String location,String checklist) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_location_title_checklist);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (activity.getResources().getDisplayMetrics().widthPixels - activity.getResources().getDimension(R.dimen.d_10dp));
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView titleTV=(TextView)dialog.findViewById(R.id.tv_titleheader);
        TextView locationTV=(TextView)dialog.findViewById(R.id.tv_location);
        TextView checklistTV=(TextView)dialog.findViewById(R.id.tv_checklist);

        try {

            titleTV.setText(""+title);
            locationTV.setText(":"+location);
            checklistTV.setText(""+checklist);

            dialog.findViewById(R.id.iv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();

    }

    public static void showForgotPassword(String email,final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_forgotpass);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (activity.getResources().getDisplayMetrics().widthPixels - activity.getResources().getDimension(R.dimen.d_10dp));
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final EditText mEmailET=(EditText)dialog.findViewById(R.id.et_email);
        final TextView mEmailErrorTv=(TextView)dialog.findViewById(R.id.tv_emailerror);
        if (!TextUtils.isEmpty(email))
            mEmailET.setText(email);

        try {


            dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.tv_send).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TextUtils.isEmpty(mEmailET.getText().toString()) || !AppUtils.isValidEmail(mEmailET.getText().toString()))
                        mEmailErrorTv.setVisibility(View.VISIBLE);
                    else
                    {
                        // AppUtils.hideKeyboard(activity);
                        if (activity instanceof SignInEmailActivity)
                            ((SignInEmailActivity)activity).resetPasswordServerData(mEmailET.getText().toString());
                        else
                            ((SignInPasswordActivity)activity).resetPasswordServerData(mEmailET.getText().toString());

                        dialog.dismiss();
                    }

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();

    }


    public static void showTeamNameDialog(final AddTeamMemberActivity activity, final HashSet<Integer> mTeamMember) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_team_name);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (activity.getResources().getDisplayMetrics().widthPixels - activity.getResources().getDimension(R.dimen.d_10dp));
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final EditText mNameET=(EditText)dialog.findViewById(R.id.et_team_name);
        final TextView mNameErrorTv=(TextView)dialog.findViewById(R.id.tv_nameerror);

        try {
            dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.tv_send).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TextUtils.isEmpty(mNameET.getText().toString()))
                        mNameErrorTv.setVisibility(View.VISIBLE);
                    else
                    {// AppUtils.hideKeyboard(activity);
                        if (activity instanceof AddTeamMemberActivity)
                            ((AddTeamMemberActivity)activity).postTeamMemberToServer(mNameET.getText().toString(),mTeamMember);
                        dialog.dismiss();
                    }

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();

    }


    public static void exitDialog(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_exit);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (activity.getResources().getDisplayMetrics().widthPixels - activity.getResources().getDimension(R.dimen.d_10dp));
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        try {


            dialog.findViewById(R.id.tv_no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.tv_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                    homeIntent.addCategory( Intent.CATEGORY_HOME );
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(homeIntent);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();

    }


    public static void versionDialog(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_version);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (activity.getResources().getDisplayMetrics().widthPixels - activity.getResources().getDimension(R.dimen.d_10dp));
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView textView=dialog.findViewById(R.id.tv_dialog_message);

        textView.setText(BuildConfig.VERSION_NAME);

        try {


            dialog.findViewById(R.id.iv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();

    }
    public static void languageDialog(final AccountProfileActivity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_language);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (activity.getResources().getDisplayMetrics().widthPixels - activity.getResources().getDimension(R.dimen.d_10dp));
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        try {
            RadioGroup radioGroup=dialog.findViewById(R.id.radioGroup_lang);

            setLangDefaultSelection(radioGroup,activity);

            dialog.findViewById(R.id.iv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.tv_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int selectedId = radioGroup.getCheckedRadioButtonId();

                   RadioButton radioButton = (RadioButton) dialog.findViewById(selectedId);
                    switch (radioButton.getText().toString()) {
                        case "English":
                            AppPreferences.INSTANCE.setSelectedLang("en");
                            Locale locale = new Locale("en");
                            Locale.setDefault(locale);
                            Configuration config = new Configuration();
                            config.locale = locale;
                            activity.getResources().updateConfiguration(config, activity.getResources().getDisplayMetrics());
                            Toast.makeText(activity, "Locale in English !", Toast.LENGTH_LONG).show();
                            break;
                        case "Russian":
                            AppPreferences.INSTANCE.setSelectedLang("ru");
                            Locale locale2 = new Locale("ru");
                            Locale.setDefault(locale2);
                            Configuration config2 = new Configuration();
                            config2.locale = locale2;
                            activity.getResources().updateConfiguration(config2, activity.getResources().getDisplayMetrics());
                            Toast.makeText(activity, "Locale in Russian !", Toast.LENGTH_LONG).show();
                            break;

                        case "Polish":
                            AppPreferences.INSTANCE.setSelectedLang("pl");
                            Locale locale3 = new Locale("pl");
                            Locale.setDefault(locale3);
                            Configuration config3 = new Configuration();
                            config3.locale = locale3;
                            activity.getResources().updateConfiguration(config3, activity.getResources().getDisplayMetrics());
                            Toast.makeText(activity, "Locale in Polish !", Toast.LENGTH_LONG).show();
                            break;
                        case "Czech":
                            AppPreferences.INSTANCE.setSelectedLang("cs");
                            Locale locale4 = new Locale("cs");
                            Locale.setDefault(locale4);
                            Configuration config4 = new Configuration();
                            config4.locale = locale4;
                            activity.getResources().updateConfiguration(config4, activity.getResources().getDisplayMetrics());
                            Toast.makeText(activity, "Locale in Czech !", Toast.LENGTH_LONG).show();
                            break;
                        case "Korean":
                            AppPreferences.INSTANCE.setSelectedLang("ko");
                            Locale locale5 = new Locale("ko");
                            Locale.setDefault(locale5);
                            Configuration config5 = new Configuration();
                            config5.locale = locale5;
                            activity.getResources().updateConfiguration(config5, activity.getResources().getDisplayMetrics());
                            Toast.makeText(activity, "Locale in Korean !", Toast.LENGTH_LONG).show();
                            break;
                        case "Dutch":
                            AppPreferences.INSTANCE.setSelectedLang("nl");
                            Locale locale6 = new Locale("nl");
                            Locale.setDefault(locale6);
                            Configuration config6 = new Configuration();
                            config6.locale = locale6;
                            activity.getResources().updateConfiguration(config6, activity.getResources().getDisplayMetrics());
                            Toast.makeText(activity, "Locale in Dutch !", Toast.LENGTH_LONG).show();
                            break;

                        case "German":
                            AppPreferences.INSTANCE.setSelectedLang("de");
                            Locale locale7 = new Locale("de");
                            Locale.setDefault(locale7);
                            Configuration config7 = new Configuration();
                            config7.locale = locale7;
                            activity.getResources().updateConfiguration(config7, activity.getResources().getDisplayMetrics());
                            Toast.makeText(activity, "Locale in German !", Toast.LENGTH_LONG).show();
                            break;

                        case "Spanish":
                            AppPreferences.INSTANCE.setSelectedLang("es");
                            Locale locale8 = new Locale("es");
                            Locale.setDefault(locale8);
                            Configuration config8 = new Configuration();
                            config8.locale = locale8;
                            activity.getResources().updateConfiguration(config8, activity.getResources().getDisplayMetrics());
                            Toast.makeText(activity, "Locale in Spanish !", Toast.LENGTH_LONG).show();
                            break;
                        case "Thai":
                            AppPreferences.INSTANCE.setSelectedLang("th");
                            Locale locale9 = new Locale("th");
                            Locale.setDefault(locale9);
                            Configuration config9 = new Configuration();
                            config9.locale = locale9;
                            activity.getResources().updateConfiguration(config9, activity.getResources().getDisplayMetrics());
                            Toast.makeText(activity, "Locale in Thai !", Toast.LENGTH_LONG).show();
                            break;
                    }



                    activity.setUpdateLanguageToServer();

                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.tv_no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();

    }

    private static void setLangDefaultSelection(RadioGroup radioGroup, Context context) {
        switch (AppPreferences.INSTANCE.getSelectedLang(context))
        {
            case "en":
                radioGroup.check(R.id.rd_english);
                break;
            case "th":
                radioGroup.check(R.id.rd_thai);
                break;
            case "nl":
                radioGroup.check(R.id.rd_dutch);
                break;
            case "pl":
                radioGroup.check(R.id.rd_polish);
                break;
            case "ru":
                radioGroup.check(R.id.rd_russian);
                break;
            case "de":
                radioGroup.check(R.id.rd_german);
                break;
            case "cs":
                radioGroup.check(R.id.rd_czech);
                break;
            case "ko":
                radioGroup.check(R.id.rd_korean);
                break;
            case "es":
                radioGroup.check(R.id.rd_spanish);
                break;
        }
    }

    public static void passwordAlgoDialog(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_password_rule);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (activity.getResources().getDisplayMetrics().widthPixels - activity.getResources().getDimension(R.dimen.d_10dp));
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        try {
            dialog.findViewById(R.id.tv_no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.tv_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();

    }


    public static void openPlayStoreDialog(final Activity context)
    {

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setCancelable(false);

        dialog.setTitle("Oditly");
        dialog.setMessage(context.getResources().getString(R.string.text_appupdate));

        dialog.setPositiveButton(context.getResources().getString(R.string.text_update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                dialog.dismiss();
                context.finish();
            }
        });

        dialog.create().show();
    }


    public static void openUpdatePopUpDialog(final Activity context)
    {

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setCancelable(false);

        dialog.setTitle("Oditly");
        dialog.setMessage(context.getResources().getString(R.string.text_app_uptodate));

        dialog.setPositiveButton(context.getResources().getString(R.string.text_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.create().show();
    }



    public static   void messageDialogWithOKButton(final Activity activity,String message) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_with_okbutton);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (activity.getResources().getDisplayMetrics().widthPixels - activity.getResources().getDimension(R.dimen.d_10dp));
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView textView=dialog.findViewById(R.id.tv_dialog_message);
        textView.setText(""+message);
        TextView textYes=dialog.findViewById(R.id.tv_yes);
        if (activity instanceof OctaLoginActivity)
            textYes.setText("Log Out");

        try {
            dialog.findViewById(R.id.tv_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity instanceof AuditCreateActivity)
                    {
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.putExtra(AppConstant.FROMWHERE, AppConstant.AUDIT);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                    if(activity instanceof AuditSubmitSignatureActivity)
                    {
                       Intent intent = new Intent(activity,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                        activity.finish();

                       // Intent data = new Intent();
                       // activity.setResult(Activity.RESULT_OK,data);
                        //activity.finish();

                    }
                    if(activity instanceof ScheduleDemoActivity)
                    {
                        Intent intent = new Intent(activity, SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                        activity.finish();
                    }

                    if(activity instanceof ActionCreateActivity)
                    {
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.putExtra(AppConstant.FROMWHERE, AppConstant.AUDIT);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                    if(activity instanceof OctaLoginActivity)
                    {
                        AppPreferences.INSTANCE.clearPreferences();
                        Intent intent = new Intent(activity, SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                    if(activity instanceof ActionPlanLandingActivity)
                    {
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                    dialog.dismiss();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();

    }

    public static   void messageDialogWithYesNo(final Activity activity,String message) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_na);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (activity.getResources().getDisplayMetrics().widthPixels - activity.getResources().getDimension(R.dimen.d_10dp));
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView textView=dialog.findViewById(R.id.tv_dialog_message);
        textView.setText(message+"\n "+activity.getResources().getString(R.string.text_douwant_to_continue));

        try {
            dialog.findViewById(R.id.tv_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.tv_no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity instanceof BrandStandardAuditActivity) {
                        ((BrandStandardAuditActivity) activity).isDialogSaveClicked=true;
                        ((BrandStandardAuditActivity) activity).saveBrandStandardQuestion();
                    }
                    else if (activity instanceof BrandStandardAuditActivityPagingnation)
                        ((BrandStandardAuditActivityPagingnation)activity).saveBrandStandardQuestion();
                    else
                    {
                        ((BrandStandardOptionsBasedQuestionActivity)activity).finish();
                    }
                    dialog.dismiss();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();

    }


}
