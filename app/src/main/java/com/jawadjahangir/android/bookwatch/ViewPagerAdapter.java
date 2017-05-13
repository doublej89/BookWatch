package com.jawadjahangir.android.bookwatch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
 
    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    String book_id;

    int _id;
 
 
    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb, String book_id) {
        super(fm);
 
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.book_id = book_id;
    }

    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb, int _id) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this._id = _id;
    }
 
    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
 
        if(position == 0 && book_id != null) // if the position is 0 we are returning the First tab
        {
            DetailTab detailTab = new DetailTab();
            Bundle bundle = new Bundle();
            if (book_id != null) {
                bundle.putString(SearchedBookDetailFragment.ARG_ITEM_ID, book_id);
                detailTab.setArguments(bundle);
            }
            return detailTab;
        } else if (position == 0) {
            LocalDetailTab detailTab = new LocalDetailTab();
            Bundle bundle = new Bundle();

            bundle.putInt(LocalBookDetailFragment.ARG_ITEM_ID, _id);
            detailTab.setArguments(bundle);

            return detailTab;
        } else if (book_id == null) {
            LocalUseTab localUseTab = new LocalUseTab();
            Bundle bundle = new Bundle();

            bundle.putInt(LocalBookDetailFragment.ARG_ITEM_ID, _id);
            localUseTab.setArguments(bundle);

            return localUseTab;
        }
        else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            UseTab useTab = new UseTab();
            return useTab;
        }
 
 
    }
 
    // This method return the titles for the Tabs in the Tab Strip
 
    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }
 
    // This method return the Number of tabs for the tabs Strip
 
    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}