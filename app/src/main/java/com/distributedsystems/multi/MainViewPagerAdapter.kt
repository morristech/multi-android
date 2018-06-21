package com.distributedsystems.multi

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.SparseArray
import android.view.ViewGroup
import com.distributedsystems.multi.browse.BrowseFragment
import com.distributedsystems.multi.profile.ProfileFragment
import com.distributedsystems.multi.transactions.TransactionsFragment

class MainViewPagerAdapter(private val fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private val ITEM_COUNT = 4
    private val registeredFragments : SparseArray<Fragment> = SparseArray()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        registeredFragments.put(position, fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        registeredFragments.remove(position)
        super.destroyItem(container, position, `object`)
    }

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> BrowseFragment.newInstance()
            1 -> ProfileFragment.newInstance()
            2 -> TransactionsFragment.newInstance()
            3 -> ProfileFragment.newInstance()
            else -> ProfileFragment.newInstance()
        }
    }

    override fun getCount(): Int {
        return ITEM_COUNT
    }

}

