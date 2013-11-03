package com.revibe.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

import com.revibe.R;

/**
 * Created by kyeh on 11/2/13.
 */
public class DialogHelper {

    public static Dialog showDialog(Context context, String message) {
        return showDialog(context, null, message);
    }

    public static Dialog showDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("Ok", null);
        if (title != null) builder.setTitle(title);
        builder.setMessage(message);
        Dialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public static Dialog showRetryDialog(Context context, String message, OnClickListener retryListener, OnClickListener exitListener) {
        return showRetryDialog(context, null, message, retryListener, exitListener);
    }
    public static Dialog showRetryDialog(Context context, String title, String message, OnClickListener retryListener, OnClickListener exitListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(context.getString(R.string.retry), retryListener);
        builder.setNegativeButton(context.getString(R.string.exit), exitListener);
        if (title != null) builder.setTitle(title);
        builder.setMessage(message);
        Dialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

}
