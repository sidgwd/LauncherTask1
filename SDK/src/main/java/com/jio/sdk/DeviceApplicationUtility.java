package com.jio.sdk;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.WindowManager;

import com.jio.sdk.models.ApplicationModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DeviceApplicationUtility {
    public static TypefaceSpan robotoRegularSpan;
    public static ProgressDialog pds = null;

    private static DeviceApplicationUtility deviceApplicationUtility;

    public static synchronized DeviceApplicationUtility getInstance() {
        if (deviceApplicationUtility == null) {
            deviceApplicationUtility = new DeviceApplicationUtility();
        }
        return deviceApplicationUtility;
    }

    public ArrayList<ApplicationModel> getInstalledApps(PackageManager pm) {
        ArrayList<ApplicationModel> apps = new ArrayList<ApplicationModel>();
        List<PackageInfo> packs = pm.getInstalledPackages(0);
        final PackageItemInfo.DisplayNameComparator comparator = new PackageItemInfo.DisplayNameComparator(pm);
        Collections.sort(packs, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo lhs, PackageInfo rhs) {
                return comparator.compare(lhs.applicationInfo, rhs.applicationInfo);
            }
        });
        try {
            for (int i = 0; i < packs.size(); i++) {
                PackageInfo p = packs.get(i);
                String packages = p.applicationInfo.packageName;
                if ( pm.getLaunchIntentForPackage(packages) != null
                        || p.applicationInfo.loadLabel(pm).toString().equals("com.android.vending")) {
                    ApplicationModel appModel = new ApplicationModel();
                    Uri uri = Uri.parse("android.resource://" + packages + "/" + p.applicationInfo.icon);
                    appModel.setAppName(p.applicationInfo.loadLabel(pm).toString());
                    appModel.setAppPackage(packages);
                    appModel.setAppIcon(uri);
                    try {
                        appModel.setLaunchActivity(getLauncherActivityName(pm, packages));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    appModel.setVersionCode(String.valueOf(packs.get(i).versionCode));
                    appModel.setVersionName(packs.get(i).versionName);
                    appModel.setSystemApp(isSystemPackage(p));
                    if(!appModel.getAppName().equals("System Tracing")){
                        apps.add(appModel);
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return apps;
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }


    public void showPopup(Context context, String title, final String msg) {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    context);
            if (!title.isEmpty()) {
                alertDialog.setTitle(getSpannableString(title));
            }
            alertDialog.setMessage(getSpannableString(msg));

            alertDialog.setPositiveButton("DISMISS",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                        }
                    });

            try {
                alertDialog.show();
            } catch (WindowManager.BadTokenException e) {
                //use a log message
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public SpannableString getSpannableString(String txt) {
        try {

            SpannableString span_tlt = new SpannableString(txt.trim());
            span_tlt.setSpan(robotoRegularSpan, 0, span_tlt.length(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            return span_tlt;
        } catch (Exception e) {

        }

        return null;
    }

    private String getLauncherActivityName(PackageManager pm, String pkg) {
        String activityName = "";
        Intent intent = pm.getLaunchIntentForPackage(pkg);
        List<ResolveInfo> activityList = pm.queryIntentActivities(intent, 0);
        if (activityList != null) {
            activityName = activityList.get(0).activityInfo.name;
        }
        return activityName;
    }


    public void showProgressDialog(final Context context, String title,
                                   String progressMsg) {
        try {
            pds = new ProgressDialog(context);
            pds.setTitle(title);
            pds.setCancelable(false);
            pds.setMessage(progressMsg);
            pds.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideProgress(AsyncTask asd) {

        try {
            if (pds != null) {
                if (pds.isShowing()) {
                    pds.dismiss();

                }
                pds = null;
            }

            if (asd != null) {
                asd.cancel(true);
                asd = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public  boolean isMyLauncherDefault(Context context) {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);

        List<IntentFilter> filters = new ArrayList<IntentFilter>();
        filters.add(filter);

        final String myPackageName = context.getPackageName();
        List<ComponentName> activities = new ArrayList<ComponentName>();
        final PackageManager packageManager = context.getPackageManager();

        // You can use name of your package here as third argument
        packageManager.getPreferredActivities(filters, activities, null);

        for (ComponentName activity : activities) {
            if (myPackageName.equals(activity.getPackageName())) {
                return true;
            }
        }
        return false;
    }



    public  boolean isMyLauncherDefault2(Context context) {
        boolean returnValue = false;
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");

        PackageManager pm = context.getPackageManager();
        final ResolveInfo mInfo = pm.resolveActivity(intent, 0);
        if (mInfo != null) {
            if (context.getPackageName().equals(mInfo.activityInfo.packageName)) returnValue = true;
        }
        return returnValue;
    }
}