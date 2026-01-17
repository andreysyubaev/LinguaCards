package com.example.linguacards.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.linguacards.allPacks
import com.example.linguacards.favoritePacks
import com.example.linguacards.recentPacks

class HomePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> allPacks()
            1 -> favoritePacks()
            else -> Fragment()
        }
    }
}