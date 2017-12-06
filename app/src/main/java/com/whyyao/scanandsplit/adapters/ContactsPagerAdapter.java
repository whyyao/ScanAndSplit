package com.whyyao.scanandsplit.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.whyyao.scanandsplit.models.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanyao on 12/5/17.
 */

public class ContactsPagerAdapter extends FragmentPagerAdapter {
    private  List<Fragment> sFragments = new ArrayList<>();
    private  List<Contact> sContacts = new ArrayList<>();

    public ContactsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        return sFragments.get(position);
    }

    @Override
    public int getCount() {
        return sFragments.size();
    }

    public void addFrag(Fragment fragment, Contact contact){
        sFragments.add(fragment);
        sContacts.add(contact);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return sContacts.get(position).getName();
    }
}
