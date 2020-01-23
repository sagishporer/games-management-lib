package com.games.management;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

public class PlayGamesHelper {
    private static final String TAG = PlayGamesHelper.class.getSimpleName();

    public static String getApplicationId(Context context) {
        Bundle metadata;
        try {
            metadata = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        String applicationId = metadata.getString("com.google.android.gms.games.APP_ID");

        if (applicationId == null)
            throw new NullPointerException("Could not find APP_ID meta-data in the Manifest.xml. Verify 'com.google.android.gms.games.APP_ID' meta-data exists in the Manifest.xml");

        Log.d(TAG, "Google Play Game - Application ID: " + applicationId);

        return applicationId;
    }
}
