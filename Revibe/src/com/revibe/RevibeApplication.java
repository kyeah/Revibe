package com.revibe;

import android.app.Application;
import android.net.http.AndroidHttpClient;

import com.revibe.utils.BitmapManager;
import com.revibe.utils.WebHelper;

import uk.co.senab.bitmapcache.BitmapLruCache;

/**
 * Created by kyeh on 11/2/13.
 */
public class RevibeApplication extends Application {

    BitmapLruCache bitmapMap;

    @Override
    public void onCreate() {
        super.onCreate();
        bitmapMap = BitmapManager.init(this);
        WebHelper.setupCookieStore(getApplicationContext());
    }

    @Override
    public void onLowMemory() {
        bitmapMap.trimMemory();
        super.onLowMemory();
    }
}
