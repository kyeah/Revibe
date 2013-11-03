package com.revibe.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class is used to access all program variables/properties, which come from "application.properties".
 * 
 * Note: It was determined from 1 error report that the Activities of the app can be loaded in different
 * orders, therefore callers of this class must try to pass the Activity/Context if it is available...  This
 * is really most important on the first call, so that the "application.properties" file can be retrieved
 * from the assets and read into memory.  Certain classes might not have access to the current Activity/Context
 * but we can know that if these classes are used, that the properties have already been read.  In these cases,
 * simpy pass null for the Activity/Context...
 */
public class AndroidProperties {

	static private final String TAG = "AndroidProperties";
	
	static private AssetManager assetManager = null;
	static private Properties properties = null;
	
	static public void init(Context activity) {
		assetManager = activity.getAssets();
	}
	
	static public String get(String key, Context activity) {
		initProperties(activity);
		if (properties != null)
			return properties.getProperty(key);
		return null;
	}
		
	static public int getInt(String key, Context activity) { return Integer.parseInt(get(key, activity)); }
	static public float getFloat(String key, Context activity) { return Float.parseFloat(get(key, activity)); }
	static public boolean getBoolean(String key, Context activity) { return "true".equalsIgnoreCase(get(key, activity)); }
	
	static public int getSize(String key, Context activity) {
		initProperties(activity);
		if (properties != null) {
			float pixelDensity = 1.0f;
			if (properties.get("pixel_density") != null)
				pixelDensity = getFloat("pixel_density", activity);
			float scaledSize = pixelDensity * getInt(key, activity);
			return Math.round(scaledSize);
		}
		return 0;
	}
	
	static public void set(String key, String value, Context activity) {
		initProperties(activity);
		if (properties != null)
			properties.setProperty(key, value);
	}
	
	static private void initProperties(Context activity) {
		if (properties == null) {
			try {
				if ((assetManager == null) && (activity != null))
					assetManager = activity.getAssets();				
				if (assetManager != null) {
					InputStream inputStream = assetManager.open("application.properties");
					properties = new Properties();
					properties.load(inputStream);
				}
			} catch (IOException e) {
				Log.e(TAG, "get(): IO exception", e);
			} catch (Exception e) {
				Log.e(TAG, "get(): error", e);
			}
		}		
	}
	
}
