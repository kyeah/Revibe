package com.revibe;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.revibe.facebook.FacebookFragment;
import com.revibe.utils.UserHelper;

public class RevibeActivity extends ActionBarActivity {

    private static String navItems[] = {"Home", "Facebook", "Google+", "Twitter"};

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View mDrawerView;
    private ListView mDrawerList;

    private ProfilePictureView profileImage;
    private TextView nameText, emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout)          findViewById(R.id.drawer_layout);
        mDrawerView =   (View)                  findViewById(R.id.left_drawer);
        mDrawerList =   (ListView)              findViewById(R.id.drawer_list);
        profileImage =  (ProfilePictureView)    findViewById(R.id.profile_picture);;
        nameText =      (TextView)              findViewById(R.id.name);
        emailText =     (TextView)              findViewById(R.id.email);

        profileImage.setProfileId(UserHelper.getCurrentUser().getId());
        nameText.setText(UserHelper.getCurrentUser().getName());
        emailText.setText((String) UserHelper.getCurrentUser().getProperty("email"));

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, navItems));

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                selectNavItem(position);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        );

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        selectNavItem(0);
    }

    private void selectNavItem(int position) {
        Fragment f;
        switch (position) {
            case 0: f = new HomeFragment(); break;
            default: f = new FacebookFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, f).commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(navItems[position]);
        mDrawerLayout.closeDrawer(mDrawerView);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.revibe, menu);
        return true;
    }
    
}
