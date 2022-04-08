package com.example.pasportdata

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.pasportdata.adapters.RvAdapter
import com.example.pasportdata.database.AppDatabase
import com.example.pasportdata.databinding.FragmentInfoBinding
import java.io.File

class InfoFragment : Fragment(R.layout.fragment_info) {
    private val binding: FragmentInfoBinding by viewBinding()
    private lateinit var appDatabase: AppDatabase
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDatabase = AppDatabase.getInstance(this.requireContext())
        val id = arguments?.getInt("id", 0)

        val person = id?.let { appDatabase.personDao().getPersonById(it) }

        binding.apply {
            tvInfoName.text = person?.name.toString()
            infoName.text = person?.name.toString()
            infoSurname.text = person?.surname.toString()
            infoMiddleName.text = person?.middle_name.toString()
            tvInfoMiddleName.text = person?.middle_name.toString()
            tvInfoSurname.text = person?.surname.toString()
            infoRegion.text = person?.region.toString()
            infoCity.text = person?.city.toString()
            infoAddress.text = person?.address.toString()
            infoPassport.text = person?.passport_date.toString()
            infoPasEndData.text = person?.passport_end_date.toString()
            infoPasspotNumber.text = person?.numberPassport.toString()
            imgPersonInfo.setImageURI(Uri.fromFile(File(person?.image)))
        }

        binding.backIc.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}