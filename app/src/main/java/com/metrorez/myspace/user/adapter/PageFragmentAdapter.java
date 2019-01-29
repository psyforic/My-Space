package com.metrorez.myspace.user.adapter;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.metrorez.myspace.user.fragment.StepOneFragment;
import com.metrorez.myspace.user.model.Inventory;

import java.util.ArrayList;
import java.util.List;

public class PageFragmentAdapter extends FragmentPagerAdapter implements StepOneFragment.OnInventoryDataListener {

    private final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitles = new ArrayList<>();

    public void addFragment(Fragment fragment, String title) {
        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }

    public PageFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    public String getTitle(int position) {
        return mFragmentTitles.get(position);
    }

    @Override
    public void onInventoryDataReceived(ArrayList<Inventory> inventoryData) {

    }
    @Override
    public Parcelable saveState()
    {
        Bundle bundle = (Bundle) super.saveState();
        if (bundle != null)
        {
            // Never maintain any states from the base class, just null it out
            bundle.putParcelableArray("states", null);
        } else
        {
            // do nothing
        }
        return bundle;
    }

}
