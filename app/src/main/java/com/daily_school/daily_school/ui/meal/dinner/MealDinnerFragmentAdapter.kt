package com.daily_school.daily_school.ui.meal.dinner

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MealDinnerFragmentAdapter (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity){

    var fragments : ArrayList<Fragment> = ArrayList()

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun addFragment(fragment : Fragment){
        fragments.add(fragment)
        notifyItemInserted(fragments.size - 1)
    }

    fun removerFragment(){
        fragments.removeLast()
        notifyItemRemoved(fragments.size)
    }

}