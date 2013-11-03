package com.revibe;

import android.app.Application;

import com.revibe.utils.BitmapManager;

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
    }

    @Override
    public void onLowMemory() {
        bitmapMap.trimMemory();
        super.onLowMemory();
    }
}
