package com.aly.travelio.ui.dashboard.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aly.travelio.R
import com.aly.travelio.databinding.CustomItemDashboardHotelsBinding
import com.aly.travelio.model.Hotel
import com.aly.travelio.util.toDisplayPrice
import com.bumptech.glide.Glide

class DashboardHotelsAdapter(
    private val onClick: (Hotel, View) -> Unit
) : ListAdapter<Hotel, DashboardHotelsAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(val binding: CustomItemDashboardHotelsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Hotel) {
            binding.hotelName.text  = item.title
            binding.location.text   = "${item.location} • ${item.guestsNumber} Guests"
            binding.price.text      = item.pricePerNight.toDisplayPrice()
            binding.ratingText.text = item.rate.toString()

            // Unique transition name per item for shared element
            binding.hotelImage.transitionName = "transition_image_${item.id}"

            Glide.with(binding.hotelImage)
                .load(item.images.firstOrNull()?.url)
                .placeholder(R.color.card_1)
                .centerCrop()
                .into(binding.hotelImage)

            binding.root.setOnClickListener { onClick(item, binding.hotelImage) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Hotel>() {
        override fun areItemsTheSame(old: Hotel, new: Hotel) = old.id == new.id
        override fun areContentsTheSame(old: Hotel, new: Hotel) = old == new
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CustomItemDashboardHotelsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.custom_item_dashboard_hotels, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))
}
