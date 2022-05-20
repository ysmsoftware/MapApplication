package com.application.mapapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.application.mapapplication.databinding.ShowLatlngItemBinding
import com.application.mapapplication.models.SendRoadData

class LatlngListAdapter(private val roadType: List<String>,private val roadSubType: List<String>) : RecyclerView.Adapter<LatlngListAdapter.RecipeViewHolder>() {
    inner class RecipeViewHolder(val binding: ShowLatlngItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<String>) = setData(list)
    private var list = listOf<String>()
    private fun setData(list: List<String>){
        this.list = list
        differ.submitList(list)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        return RecipeViewHolder(
            ShowLatlngItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val roadDetail = differ.currentList[position]

        holder.binding.apply {

            latlngName.text = roadDetail
            typeName.text = roadType[position]
            subtypeName.text = roadSubType[position]

        }


    }

    override fun getItemCount() = differ.currentList.size

}