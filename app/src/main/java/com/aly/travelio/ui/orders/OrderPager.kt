package com.aly.travelio.ui.orders

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OrderPager(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val fragments: List<Fragment> = listOf(CurrentOrderFragment.newInstance(), ExpiredFragment.newInstance())
    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment { return fragments[position] }

}