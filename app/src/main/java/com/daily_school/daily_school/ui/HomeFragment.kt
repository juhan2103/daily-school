package com.daily_school.daily_school.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.daily_school.daily_school.R
import com.daily_school.daily_school.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.bottomNavPlan.setOnClickListener {

            it.findNavController().navigate(R.id.action_homeFragment_to_planFragment)
        }

        binding.bottomNavMeal.setOnClickListener {

            it.findNavController().navigate(R.id.action_homeFragment_to_mealFragment)
        }

        binding.bottomNavSchedule.setOnClickListener {

            it.findNavController().navigate(R.id.action_homeFragment_to_scheduleFragment)
        }

        binding.bottomNavProfile.setOnClickListener {

            it.findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        return binding.root

    }

}