package com.revibe;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.*;
import com.facebook.model.*;
import com.revibe.utils.AndroidProperties;
import com.revibe.utils.DialogHelper;
import com.revibe.utils.UserHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kyeh on 11/2/13.
 */
public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidProperties.init(this);

        // Set pixel density based on Android device
        DisplayMetrics metrics = new DisplayMetrics();
        try {
            WindowManager winMgr = (WindowManager)getSystemService(Context.WINDOW_SERVICE) ;
            winMgr.getDefaultDisplay().getMetrics(metrics);
        } catch (Exception e) {
            metrics.density = 1;
        }

        AndroidProperties.set("pixel_density", String.valueOf(metrics.density), this);

        loginWithFacebook();
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {

        /* KeyHash debugger
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.revibe",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        */

        return super.onCreateView(name, context, attrs);
    }

    public void loginWithFacebook() {
        // start Facebook Login
        Session currentSession = Session.getActiveSession();
        if (currentSession == null || currentSession.getState().isClosed()) {
            Session session = new Session.Builder(this).build();
            Session.setActiveSession(session);
            currentSession = session;
        }

        // Ask for username and password
        Session.OpenRequest op = new Session.OpenRequest(this);

        op.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
        op.setCallback(null);

        List<String> permissions = new ArrayList<String>();
        permissions.add("email");
        permissions.add("user_status");
        permissions.add("friends_status");
        op.setPermissions(permissions);

        Session session = new Session.Builder(LoginActivity.this).build();
        Session.setActiveSession(session);
        session.addCallback(new Session.StatusCallback() {

            // callback when session changes state
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                final String accessToken = session.getAccessToken();

                if (session.isOpened()) {
                    Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                        public void onCompleted(GraphUser user, Response response) {
                            if (user == null) {
                                DialogHelper.showRetryDialog(LoginActivity.this, getString(R.string.could_not_connect_fb_account), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) { loginWithFacebook(); }
                                }, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) { finish(); }
                                });

                            } else {
                                final String name = user.getName();
                                final String email = (String) user.getProperty("email");

                                Toast.makeText(LoginActivity.this, name + " " + email, Toast.LENGTH_LONG).show();
                                UserHelper.setCurrentUser(user, accessToken);

                                Intent i = new Intent(LoginActivity.this, RevibeActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }
                    });
                }
            }
        });

        session.openForRead(op);
    }

    // callback to return focus from FB login session to Revibe
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

}
