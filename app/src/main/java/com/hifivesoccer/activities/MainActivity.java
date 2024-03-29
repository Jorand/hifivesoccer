package com.hifivesoccer.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hifivesoccer.R;
import com.hifivesoccer.adapters.ViewPagerAdapter;
import com.hifivesoccer.libs.SlidingTabLayout;
import com.hifivesoccer.utils.MySelf;

public class MainActivity extends AppActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final Context context = this;

    Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Tous les matchs","Mes matchs", "Notifications"};
    int Numboftabs = 3;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(this, getSupportFragmentManager(),Titles,Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setCustomTabView(R.layout.custom_tab, 0);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.primary_dark);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        // FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                //Snackbar.make(coordinatorLayout, "FAB Clicked", Snackbar.LENGTH_SHORT).show();
                Intent intent = new Intent(context, NewGameActivity.class);
                startActivity(intent);
            }
        });

        if(MySelf.getSelf() != null){
            Log.d(TAG, MySelf.getSelf().get_id());
            username = MySelf.getSelf().getUsername();
            Snackbar.make(coordinatorLayout, "Bienvenue " + username + " !", Snackbar.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "MySelf is null");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem profileMenuItem = menu.findItem(R.id.action_settings);
        if (username != null)
            profileMenuItem.setTitle(username);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(context, UserSettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_logout) {
            confirmExit();
        }

        return super.onOptionsItemSelected(item);
    }

    public void openProfil(View view) {
        String user_id = (String) view.getTag();
        if (user_id != null) {
            Intent intent = new Intent(context, ProfilActivity.class);
            intent.putExtra("USER_ID", user_id);
            startActivity(intent);
        }
    }

}
