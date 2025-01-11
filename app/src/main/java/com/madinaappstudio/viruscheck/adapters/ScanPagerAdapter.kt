package com.madinaappstudio.viruscheck.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.madinaappstudio.viruscheck.tabs.FileScanFragment
import com.madinaappstudio.viruscheck.tabs.UrlScanFragment

class ScanPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FileScanFragment()
            1 -> UrlScanFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
