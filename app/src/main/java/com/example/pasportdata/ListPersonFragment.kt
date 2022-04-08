package com.example.pasportdata

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.pasportdata.adapters.RvAdapter
import com.example.pasportdata.database.AppDatabase
import com.example.pasportdata.databinding.FragmentListPersonBinding
import com.example.pasportdata.entity.Person


class ListPersonFragment : Fragment(R.layout.fragment_list_person) {

    private val binding: FragmentListPersonBinding by viewBinding()
    private lateinit var rvAdapter: RvAdapter
    private lateinit var appDatabase: AppDatabase

    private lateinit var list: ArrayList<Person>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            appDatabase = AppDatabase.getInstance(requireContext())
            list = appDatabase.personDao().getAllPerson() as ArrayList<Person>
            val persons = ArrayList<Person>(list)

            binding.backIc.setOnClickListener {
                findNavController().popBackStack()
            }

            icSearch.setOnQueryTextFocusChangeListener { v, hasFocus ->
              if (textTitle.visibility == View.VISIBLE){
                  textTitle.visibility = View.INVISIBLE
              }else{
                  textTitle.visibility = View.VISIBLE
              }
            }

            icSearch.setOnCloseListener {
                binding.textTitle.visibility = View.VISIBLE
                false
            }

            icSearch.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    binding.textTitle.visibility = View.INVISIBLE
                    val sortedList = ArrayList<Person>()
                    for (i in persons.indices) {
                        val s: String = persons[i].name.lowercase()
                        val f: String = persons[i].surname.lowercase()
                        if (newText != null) {
                            if (s.contains(newText.lowercase()) || f.contains(newText.lowercase())) {
                                sortedList.add(persons[i])
                            }
                        }
                    }
                    setAdapterBySearch(sortedList)
                    return false
                }
            })
            setView(list)
        }

    }

    fun setView(list: ArrayList<Person>) {
        rvAdapter = RvAdapter(requireContext(), list, object : RvAdapter.OnItemClickListener {

            override fun onItemClick(person: Person, position: Int) {
                val bundle = Bundle()
                bundle.putSerializable("id", person.id)
                findNavController().navigate(R.id.infoFragment, bundle)
            }

            override fun onEditClick(person: Person, position: Int) {
                val bundle = Bundle()
                bundle.putSerializable("id", person.id)
                findNavController().navigate(R.id.editFragment)
            }

            override fun onDeleteClick(person: Person, position: Int) {
                list.remove(person)
                rvAdapter.notifyItemRemoved(position)
                rvAdapter.notifyItemRangeRemoved(position, list.size)
                appDatabase.personDao().deletePerson(person)
            }


        })
        binding.rv.adapter = rvAdapter
    }

    fun setAdapterBySearch(listBySearch: ArrayList<Person>) {
        rvAdapter = RvAdapter(requireContext(), listBySearch, object : RvAdapter.OnItemClickListener {

                override fun onItemClick(person: Person, position: Int) {
                    val bundle = Bundle()
                    bundle.putSerializable("id", person.id)
                    findNavController().navigate(R.id.infoFragment, bundle)
                }

                override fun onEditClick(person: Person, position: Int) {
                    val bundle = Bundle()
                    bundle.putInt("id", person.id)
                    findNavController().navigate(R.id.editFragment)
                }

                override fun onDeleteClick(person: Person, position: Int) {
                    listBySearch.remove(person)
                    rvAdapter.notifyItemRemoved(position)
                    rvAdapter.notifyItemRangeRemoved(position, listBySearch.size)
                    appDatabase.personDao().deletePerson(person)
                }

            })
        binding.rv.adapter = rvAdapter
    }

}