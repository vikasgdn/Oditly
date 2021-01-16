package com.oditly.audit.inspection.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.oditly.audit.inspection.R;
import com.oditly.audit.inspection.model.audit.BrandStandard.BrandStandardSection;
import com.oditly.audit.inspection.ui.activty.ActionCreateActivity;
import com.oditly.audit.inspection.ui.activty.AddTeamMemberActivity;
import com.oditly.audit.inspection.ui.activty.AuditCreateActivity;
import com.oditly.audit.inspection.ui.activty.AuditSubmitSignatureActivity;
import com.oditly.audit.inspection.ui.activty.BrandStandardAuditActivity;
import com.oditly.audit.inspection.ui.activty.MainActivity;
import com.oditly.audit.inspection.ui.activty.ScheduleDemoActivity;
import com.oditly.audit.inspection.ui.activty.SignInEmailActivity;
import com.oditly.audit.inspection.ui.activty.SignInPasswordActivity;
import com.oditly.audit.inspection.ui.activty.SplashActivity;
import com.oditly.audit.inspection.util.AppConstant;
import com.oditly.audit.inspection.util.AppLogger;
import com.oditly.audit.inspection.util.AppUtils;

import java.util.HashSet;


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
        dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
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

    public static void showForgotPassword(final Activity activity) {
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
                            ((SignInEmailActivity)activity).setOTPServer(mEmailET.getText().toString());
                        else
                            ((SignInPasswordActivity)activity).setOTPServer(mEmailET.getText().toString());

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
    public static void languageDialog(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_language);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (activity.getResources().getDisplayMetrics().widthPixels - activity.getResources().getDimension(R.dimen.d_10dp));
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        try {


            dialog.findViewById(R.id.iv_cancel).setOnClickListener(new View.OnClickListener() {
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
        dialog.setMessage("You are missing out on some important features of Oditly, please update your application.");

        dialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
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
                    }
                    if(activity instanceof ScheduleDemoActivity)
                    {
                        Intent intent = new Intent(activity, SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

  /*  public static   void messageDialogWithNA(SubSectionTabAdapter.SubSectionTabViewHolder holder, BrandStandardSection brandStandardSection,SubSectionTabAdapter tabAdapter, final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_na);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (activity.getResources().getDisplayMetrics().widthPixels - activity.getResources().getDimension(R.dimen.d_10dp));
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        try {
            dialog.findViewById(R.id.tv_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tabAdapter.markSectionAsNotApplicable(holder,brandStandardSection);
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.tv_no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tabAdapter.markSectionAsNotApplicableCancel(holder);
                    dialog.dismiss();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();

    }*/


}
