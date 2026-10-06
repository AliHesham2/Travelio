package com.aly.travelio.ui.dashboard.trips

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aly.travelio.R
import com.aly.travelio.databinding.CustomItemTripMainBinding
import com.aly.travelio.model.Trip
import com.aly.travelio.util.toDisplayPrice
import com.bumptech.glide.Glide
import com.aly.travelio.databinding.CustomItemDashboardTripsBinding

class TripsAdapter(
    private val isDashboard: Boolean = false,
    private val onClick: (Trip, View) -> Unit
) : ListAdapter<Trip, RecyclerView.ViewHolder>(DiffCallback) {

    inner class MainViewHolder(val binding: CustomItemTripMainBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Trip) {
            binding.name.text     = item.title
            binding.location.text = item.location
            binding.price.text    = item.price.toDisplayPrice()
            binding.tripImage.transitionName = "transition_image_${item.id}"
            Glide.with(binding.tripImage).load(item.images.firstOrNull()?.url)
                 .placeholder(R.color.card_1).centerCrop().into(binding.tripImage)
            binding.tripDetailBtn.setOnClickListener { onClick(item, binding.tripImage) }
            binding.root.setOnClickListener { onClick(item, binding.tripImage) }
        }
    }

    inner class DashboardViewHolder(val binding: CustomItemDashboardTripsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Trip) {
            binding.name.text     = item.title
            binding.location.text = item.location
            binding.price.text    = item.price.toDisplayPrice()
            binding.tripImage.transitionName = "transition_image_${item.id}"
            Glide.with(binding.tripImage).load(item.images.firstOrNull()?.url)
                 .placeholder(R.color.card_1).centerCrop().into(binding.tripImage)
            binding.tripDetailBtn.setOnClickListener { onClick(item, binding.tripImage) }
            binding.root.setOnClickListener { onClick(item, binding.tripImage) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Trip>() {
        override fun areItemsTheSame(old: Trip, new: Trip) = old.id == new.id
        override fun areContentsTheSame(old: Trip, new: Trip) = old == new
    }

    override fun getItemViewType(position: Int): Int = if (isDashboard) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 1) {
            DashboardViewHolder(DataBindingUtil.inflate(inflater, R.layout.custom_item_dashboard_trips, parent, false))
        } else {
            MainViewHolder(DataBindingUtil.inflate(inflater, R.layout.custom_item_trip_main, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (holder is MainViewHolder) holder.bind(item)
        else if (holder is DashboardViewHolder) holder.bind(item)
    }
}