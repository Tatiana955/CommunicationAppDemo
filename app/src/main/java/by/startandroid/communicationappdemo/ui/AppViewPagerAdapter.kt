package by.startandroid.communicationappdemo.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.startandroid.communicationappdemo.ui.userpage.UserPageFragment
import by.startandroid.communicationappdemo.ui.chat.ChatFragment

class AppViewPagerAdapter(activity: MainActivity): FragmentStateAdapter(activity) {

    private val listFrag = listOf(
            UserPageFragment(),
            ChatFragment()
    )

    override fun getItemCount(): Int {
        return listFrag.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                listFrag[position]
            }
            1 -> {
                listFrag[position]
            }
            else -> ChatFragment()
        }
    }
}