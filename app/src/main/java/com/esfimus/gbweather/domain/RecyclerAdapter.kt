package com.esfimus.gbweather.domain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esfimus.gbweather.R
import com.esfimus.gbweather.databinding.RecyclerviewItemBinding
// TODO check
class RecyclerAdapter(private val itemsList: List<List<String?>>) :
    RecyclerView.Adapter<RecyclerAdapter.CustomViewHolder>() {

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = RecyclerviewItemBinding.bind(itemView)
        fun bind(favouriteLocation: List<String?>) {
            binding.cardLocationName.text = favouriteLocation[0]
            binding.cardTemperature.text = favouriteLocation[1]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recyclerview_item, parent, false)
        return  CustomViewHolder(itemView)
    }

    override fun getItemCount() = itemsList.size

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(itemsList[position])
    }

}