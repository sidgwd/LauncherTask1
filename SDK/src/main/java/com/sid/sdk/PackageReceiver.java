package com.sid.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PackageReceiver extends BroadcastReceiver {
    private static final String TAG = PackageReceiver.class.getName();
    INotifyAppList iNotifyAppList;
    public PackageReceiver(INotifyAppList iNotifyAppList){
        this.iNotifyAppList= iNotifyAppList;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if ((intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED) ||
                    intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED))) {
                iNotifyAppList.appListUpdated();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
