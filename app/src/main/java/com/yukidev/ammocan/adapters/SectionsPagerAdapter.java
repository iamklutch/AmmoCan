package com.yukidev.ammocan.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

import com.yukidev.ammocan.R;
import com.yukidev.ammocan.ui.AirmanBulletsActivity;
import com.yukidev.ammocan.ui.FriendsFragment;
import com.yukidev.ammocan.ui.InboxFragment;


/**
 * Created by James on 5/8/2015.
 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.  */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

    protected Context mContext;

        public SectionsPagerAdapter(Context context, FragmentManager fm) {
            super(fm);

            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //  MainActivity.PlaceholderFragment.newInstance(position + 1)

            switch(position) {
                case 0:
                    return new InboxFragment();
                case 1:
                    return new FriendsFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return mContext.getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return mContext.getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
        //sets the icons for the ActionBar
        public int getIcon(int position) {
            switch (position) {
                case 0:
                    return R.drawable.ic_action_go_to_today;
                case 1:
                    return R.drawable.ic_action_group;
            }
            return R.drawable.ic_action_maps_my_location;
        }

    }

