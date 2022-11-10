package com.pi.projectinclusion.adapter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pi.projectinclusion.`interface`.ButtonAction
import com.pi.projectinclusion.fragment.*
import com.pi.projectinclusion.model.MenuModel
import com.google.android.material.tabs.TabLayout

private const val TAG = "MenuPagerAdapter"

class MenuPagerAdapter(
    fragmentActivity: FragmentActivity,
    private var dataList: ArrayList<MenuModel>,
    private var mTabLayout: TabLayout,
    private var logStatus : ButtonAction
) : FragmentStateAdapter(fragmentActivity) {
    private var currPos: Int = 0

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemId(position: Int): Long {
        currPos = position
        Log.e("Position", position.toString())
        /*if(currPos==7){
            Log.e("Log out","Pressed")
            logStatus.menuLogPressed(false)
        }*/
        return super.getItemId(position)

    }

    override fun createFragment(position: Int): Fragment {
        var fragment = Fragment()
        Log.d(TAG, "createFragment: position: " + position)
        Log.d(TAG, "createFragment: currPosition: " + mTabLayout.selectedTabPosition)
        // val currPosition = mTabLayout.selectedTabPosition
        when (position) {
            0 -> {
                Log.d(TAG, "createFragment: 0")
                fragment = CoursesFragment()
                val bundle = Bundle()
                bundle.putString("key", dataList[position].title)
                fragment.arguments = bundle
            }
            1 -> {

                Log.d(TAG, "createFragment: 1")
                fragment = ViewPagerFragment()
                val bundle = Bundle()
                bundle.putString("key", dataList[position].title)
                fragment.arguments = bundle
            }
            /*  else if (position == 2) {

                  fragment = ScreeningProfileFragment()
                  val bundle = Bundle()
                  bundle.putString("key", dataList[position].title)

                  fragment.arguments = bundle
              } */
            2 -> {

                fragment = WebinarFragment()
                val bundle = Bundle()
                bundle.putString("key", dataList[position].title)

                fragment.arguments = bundle
            }
            3 -> {

                Log.d(TAG, "createFragment: 2")
                fragment = CertificateFragment()
                val bundle = Bundle()
                bundle.putString("key", dataList[position].title)
                fragment.arguments = bundle
            }
            4 -> {

                Log.d(TAG, "createFragment: 3")
                fragment = ReferFragment()
                val bundle = Bundle()
                bundle.putString("key", dataList[position].title)
                fragment.arguments = bundle
            }
            5 -> {

                Log.d(TAG, "createFragment: 4")
                fragment = UserFragment()
                val bundle = Bundle()
                bundle.putString("key", dataList[position].title)
                fragment.arguments = bundle
            }
            /*6 -> {
                Log.e("Log out","Pressed")
                logStatus.menuLogPressed(false)
            }*/
        }



        return fragment
    }
}