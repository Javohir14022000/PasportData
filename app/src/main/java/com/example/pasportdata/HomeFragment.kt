package com.example.pasportdata

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.pasportdata.databinding.FragmentHomeBinding


class HomeFragment : Fragment(R.layout.fragment_home) {
private val binding:FragmentHomeBinding by viewBinding()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            tvListPerson.setOnClickListener {
                findNavController().navigate(R.id.listPersonFragment)
            }
            tvAddPerson.setOnClickListener {
                findNavController().navigate(R.id.addPersonFragment)
            }
        }
    }
}