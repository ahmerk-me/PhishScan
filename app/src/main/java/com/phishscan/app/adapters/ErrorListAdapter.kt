package com.phishscan.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phishscan.app.databinding.RowErrorListBinding
import com.phishscan.app.view.activity.MainActivity

class ErrorListAdapter(val act: MainActivity, private val itemsData: ArrayList<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView =
            RowErrorListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    inner class MyViewHolder(b: RowErrorListBinding) : RecyclerView.ViewHolder(b.root) {

        var binding: RowErrorListBinding = b

        fun bind(position: Int) {

            act.setTextFonts(binding.root)

//            binding.tvTitle.setTypeface(act.tf, Typeface.BOLD)

        }

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as MyViewHolder).bind(holder.adapterPosition)

        itemsData[holder.adapterPosition].let {

            with(holder.binding) {

                tvTitle.text = it
            }

        }

    }


    override fun getItemCount(): Int {

        return itemsData.size

    }

    fun updateList(newList: ArrayList<String>) {

        var list = newList.clone() as ArrayList<String>

        itemsData.clear()

        itemsData.addAll(list)

        notifyDataSetChanged()

    }

}