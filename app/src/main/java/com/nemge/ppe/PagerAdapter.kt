package com.nemge.ppe

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class PagerAdapter(fm: FragmentManager, internal var mNumOfTabs: Int) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {

        when (position) {
            0 -> return DosesFragment()
            1 -> return ConsumptionFragment()
            else -> return null
        }
    }

    override fun getCount(): Int {
        return mNumOfTabs
    }
}