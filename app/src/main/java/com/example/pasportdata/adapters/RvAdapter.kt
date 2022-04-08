package com.example.pasportdata.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.pasportdata.R
import com.example.pasportdata.databinding.ItemPersonBinding
import com.example.pasportdata.entity.Person

class RvAdapter(context: Context, val list: List<Person>, val listener: OnItemClickListener) :
    RecyclerView.Adapter<RvAdapter.Vh>() {
    inner class Vh(val itemPersonBinding: ItemPersonBinding) :
        RecyclerView.ViewHolder(itemPersonBinding.root) {


        fun onBind(person: Person, position: Int) {
            itemPersonBinding.apply {
                tvNumber.text = "${position + 1}."
                tvSurname.text = person.surname
                tvName.text = person.name
                tvPass.text = person.numberPassport

                itemView.setOnClickListener {
                    listener.onItemClick(person, position)
                }

                imgIcon.setOnClickListener {
                    listener.onEditClick(person, position)
                }

                imgIcon.setOnClickListener {
                    val popupMenu = PopupMenu(it.context, it)
                    popupMenu.inflate(R.menu.popup_menu)
                    popupMenu.show()
                    popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
                        if (item?.itemId == R.id.select_delete) {
                            listener.onDeleteClick(person, position)
                        } else if (item?.itemId == R.id.select_edit) {
                            listener.onEditClick(person, position)
                        }
                        true
                    })

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemPersonBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onItemClick(person: Person, position: Int)
        fun onEditClick(person: Person, position: Int)
        fun onDeleteClick(person: Person, position: Int)
    }


}