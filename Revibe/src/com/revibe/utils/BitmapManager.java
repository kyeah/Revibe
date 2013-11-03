package com.revibe.utils;

import android.app.Application;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Gallery;
import android.widget.ImageView;

import com.revibe.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import uk.co.senab.bitmapcache.BitmapLruCache;
import uk.co.senab.bitmapcache.CacheableBitmapDrawable;

public class BitmapManager {

    static private final String TAG = "BitmapManager";
    static private int PIXEL_DENSITY = -1;
    static private final int MAX_CONSEC_OOM_ERRORS = 5;
    static private final int INPUTSTREAM_MARK_LIMIT = 1 << 24;

    static private final boolean ENABLE_FADEIN = true;

    static private BitmapLruCache bitmapMap;
    static private File cacheLocation;

    static private boolean paused = false;
    static private int num_consec_oom_errors = 0;

    static public boolean started() { return bitmapMap != null; }
    static public void pause() { paused = true; }
    static public void resume() { paused = false; }

    static public BitmapLruCache init(Application app) {

        // If we have external storage use it for the disk cache. Otherwise we use
        // the cache dir
        //if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
        //    cacheLocation = new File(Environment.getExternalStorageDirectory() + "/Android-BitmapCache");
        //} else {
        //    cacheLocation = new File(app.getFilesDir() + "/Android-BitmapCache");
        //}

        cacheLocation = new File(app.getCacheDir() + "/Android-BitmapCache");
        cacheLocation.mkdirs();

        BitmapLruCache.Builder builder = new BitmapLruCache.Builder(app);
        builder.setMemoryCacheEnabled(true).setMemoryCacheMaxSizeUsingHeapSize();
        builder.setDiskCacheEnabled(true).setDiskCacheLocation(cacheLocation);

        bitmapMap = builder.build();
        return bitmapMap;
    }

    static public void clearMap() {
        if (cacheLocation != null && cacheLocation.listFiles() != null) {
            for (File child : cacheLocation.listFiles())
                child.delete();
            cacheLocation.delete();
        }
    }

    static private CacheableBitmapDrawable findBitmap(final String urlString) {
        try {
            return bitmapMap.get(urlString);
        } catch (OutOfMemoryError e) {
            bitmapMap.trimMemory();
        }
        return null;
    }

    static public void fetchBitmapOnThread(final String urlString, final ImageView imageView, final Context context) {
        imageView.setVisibility(View.INVISIBLE);

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                imageView.setVisibility(View.VISIBLE);
                if (message.obj != null) {
                    imageView.setImageDrawable((CacheableBitmapDrawable) message.obj);

                    // Fade in if bitmap was not found in cache.
                    if (message.what == 1 && ENABLE_FADEIN && !(imageView.getParent() instanceof Gallery)) {
                        Animation myFadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fadein);
                        imageView.startAnimation(myFadeInAnimation);
                    }
                } else {
                    // Set default image
                    imageView.setImageResource(R.drawable.ic_launcher);
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                }
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                if (PIXEL_DENSITY == -1) PIXEL_DENSITY = (int) AndroidProperties.getFloat("pixel_density", context);

                final WeakReference<ImageView> mImageViewRef = new WeakReference<ImageView>(imageView);
                final int width = imageView.getLayoutParams().width;
                final int height = imageView.getLayoutParams().height;

                CacheableBitmapDrawable bitmap = findBitmap(urlString);

                Message message;
                if (bitmap == null || bitmap.getIntrinsicWidth() < width) {
                    if (null == mImageViewRef.get()) return;

                    if (paused) {
                        return;
                    } else {
                        bitmap = decodeSampledBitmapFromStream(urlString, mImageViewRef, width, height);
                        message = handler.obtainMessage(1, bitmap);
                    }
                } else {
                    message = handler.obtainMessage(2, bitmap);
                }

                if (null == mImageViewRef.get()) return;
                handler.sendMessage(message);
            }
        };
        thread.start();
    }

    /** Efficient loading of scaled image into memory **/
    static public CacheableBitmapDrawable decodeSampledBitmapFromStream(String url, WeakReference<ImageView> mImageViewRef, int reqWidth, int reqHeight) {
        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest;
        try {
            getRequest = new HttpGet(TextUtils.htmlEncode(url).replace("%.", "."));
        } catch (Exception e) {
            return null;
        }

        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w(TAG, "Error " + statusCode + " while retrieving bitmap from " + getRequest.toString());
                return null;
            }

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                BufferedInputStream bis = null;
                try {
                    inputStream = entity.getContent();
                    bis = new BufferedInputStream(inputStream);

                    // Make sure imageview is still there before decoding
                    if (mImageViewRef.get() == null) return null;

                    // First decode with inJustDecodeBounds=true to check dimensions
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    bis.mark(INPUTSTREAM_MARK_LIMIT);
                    BitmapFactory.decodeStream(bis, null, options);

                    // Reset the stream
                    try {
                        bis.reset();
                    } catch (IOException e) {

                        // Mark was invalidated, new http request needs to be made to reset inputstream
                        if (inputStream != null) {
                            inputStream.close();
                            bis.close();
                        }
                        entity.consumeContent();
                        response = client.execute(getRequest);
                        final int newStatusCode = response.getStatusLine().getStatusCode();
                        if (newStatusCode != HttpStatus.SC_OK) {
                            Log.w(TAG, "Error " + newStatusCode + " while retrieving bitmap from " + getRequest.toString());
                            return null;
                        }

                        entity = response.getEntity();
                        if (entity != null) {
                            inputStream = entity.getContent();
                            bis = new BufferedInputStream(inputStream);
                        }
                    }

                    // Calculate inSampleSize
                    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

                    // Decode bitmap with inSampleSize set
                    options.inJustDecodeBounds = false;

                    num_consec_oom_errors = 0;
                    if (mImageViewRef.get() == null) return null;
                    return bitmapMap.put(url, bis, options);
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (bis != null) {
                        bis.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            getRequest.abort();
            Log.w("ImageDownloader", "Error while retrieving bitmap from " + url, e);
        } catch (OutOfMemoryError oom) {
            bitmapMap.trimMemory();
            getRequest.abort();

            Log.w("ImageDownloader", "Error while retrieving bitmap from " + url, oom);

            num_consec_oom_errors++;
            if (num_consec_oom_errors >= MAX_CONSEC_OOM_ERRORS)
                throw new RuntimeException("ImageDownloader: Out of Memory error");
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return null;
    }

    static public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

}
