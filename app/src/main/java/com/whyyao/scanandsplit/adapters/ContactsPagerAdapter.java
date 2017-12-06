package com.whyyao.scanandsplit.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.whyyao.scanandsplit.UI.ContactFragment;
import com.whyyao.scanandsplit.models.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanyao on 12/5/17.
 */

public class ContactsPagerAdapter extends FragmentPagerAdapter {
    private  List<ContactFragment> sFragments = new ArrayList<>();
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

    public void addFrag(ContactFragment fragment, Contact contact){
        sFragments.add(fragment);
        sContacts.add(contact);
    }

    //getting the Name of the title
    @Override
    public CharSequence getPageTitle(int position) {
        return sContacts.get(position).getName();
    }

    public ArrayList<Contact> updateContacts(){
        ArrayList<Contact> result = new ArrayList<>();
        for (int i=0; i< sFragments.size(); i++){
            result.add(sFragments.get(i).generateUpdatedContact());
        }

        return result;
    }

}
