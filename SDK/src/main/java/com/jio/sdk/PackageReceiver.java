package com.jio.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PackageReceiver extends BroadcastReceiver {
    private static final String TAG = PackageReceiver.class.getName();
    INotifyAppList iNotifyAppList;

    public PackageReceiver(INotifyAppList iNotifyAppList) {
        this.iNotifyAppList = iNotifyAppList;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            iNotifyAppList.appListUpdated(intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED), intent.getData().getEncodedSchemeSpecificPart());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
