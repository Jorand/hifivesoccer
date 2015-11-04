package com.hifivesoccer.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.hifivesoccer.R;
import com.hifivesoccer.fragments.AllGamesTabActivity;
import com.hifivesoccer.fragments.NotificationsTabActivity;
import com.hifivesoccer.fragments.MyGamesTabActivity;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    Context mContext;

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

    private int[] imageResId = {

        R.drawable.ic_action_home,
        R.drawable.icon_match,
        R.drawable.icon_notif
    };

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(Context context, FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.mContext = context;
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if(position == 0) // if the position is 0 we are returning the First tab
        {
            AllGamesTabActivity tab1 = new AllGamesTabActivity();
            return tab1;
        }
        else if (position == 1) {
            MyGamesTabActivity tab2 = new MyGamesTabActivity();
            return tab2;
        }
        else
        {
            NotificationsTabActivity tab3 = new NotificationsTabActivity();
            return tab3;
        }

    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        //return Titles[position];

        Drawable image = ContextCompat.getDrawable(mContext, imageResId[position]);
        image.setBounds(0, 0, 55, 55);
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}