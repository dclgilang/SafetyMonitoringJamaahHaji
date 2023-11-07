package id.co.dif.smj.presentation.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import id.co.dif.smj.data.TabMenuItem

class ViewPagerAdapter(
    var context: Context,
    fm: FragmentManager,
    var menu: MutableList<TabMenuItem> = mutableListOf()
) : FragmentPagerAdapter(fm) {

    override fun getCount() = menu.size

    override fun getPageTitle(position: Int): CharSequence? {
        return menu[position].title
    }

    override fun getItem(position: Int): Fragment {
        return menu[position].fragment
    }
}