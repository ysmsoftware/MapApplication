package com.application.mapapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.application.mapapplication.databinding.DataItemBinding
import com.application.mapapplication.models.SendRoadData

class RoadNameListAdapter : RecyclerView.Adapter<RoadNameListAdapter.RecipeViewHolder>() {
    private var onSelectClickListener: ((SendRoadData) -> Unit)? = null
    private var onDeleteClickListener: ((SendRoadData) -> Unit)? = null
    inner class RecipeViewHolder(val binding: DataItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<SendRoadData>() {
        override fun areItemsTheSame(oldItem: SendRoadData, newItem: SendRoadData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SendRoadData, newItem: SendRoadData): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<SendRoadData>) = setData(list)
    private var list = listOf<SendRoadData>()
    private fun setData(list: List<SendRoadData>){
        this.list = list
        differ.submitList(list)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        return RecipeViewHolder(
            DataItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val roads = differ.currentList[position]

        holder.binding.apply {

            roadName.text = roads.roadName
            deleteRoad.setOnClickListener {
                onDeleteClickListener?.let { it(roads) }
            }
            llLayout.setOnClickListener {
                onSelectClickListener?.let { it(roads) }
            }

        }


    }

    override fun getItemCount() = differ.currentList.size

    fun setOnDeleteClickListener(listener: (SendRoadData) -> Unit){
        onDeleteClickListener = listener
    }
    fun setOnSelectClickListener(listener: (SendRoadData) -> Unit){
        onSelectClickListener = listener
    }
}