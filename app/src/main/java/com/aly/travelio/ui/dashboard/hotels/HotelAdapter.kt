package com.aly.travelio.ui.dashboard.hotels

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aly.travelio.R
import com.aly.travelio.databinding.CustomItemHotelMainBinding
import com.aly.travelio.model.Hotel
import com.aly.travelio.util.toDisplayPrice
import com.bumptech.glide.Glide

class HotelAdapter(
    private val onClick: (Hotel, android.view.View) -> Unit
) : ListAdapter<Hotel, HotelAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(val binding: CustomItemHotelMainBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Hotel) {
            binding.hotelName.text  = item.title
            binding.location.text   = item.location
            binding.guests.text     = "${item.guestsNumber} Guests"
            binding.price.text      = item.pricePerNight.toDisplayPrice()
            binding.ratingText.text = item.rate.toString()
            Glide.with(binding.shapeableImageView)
                .load(item.images.firstOrNull()?.url)
                .placeholder(R.color.card_1)
                .centerCrop()
                .into(binding.shapeableImageView)
            // Unique transition name per item for shared element
            binding.shapeableImageView.transitionName = "transition_image_${item.id}"

            binding.btnView.setOnClickListener { onClick(item, binding.shapeableImageView) }
            binding.root.setOnClickListener    { onClick(item, binding.shapeableImageView) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Hotel>() {
        override fun areItemsTheSame(old: Hotel, new: Hotel) = old.id == new.id
        override fun areContentsTheSame(old: Hotel, new: Hotel) = old == new
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CustomItemHotelMainBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.custom_item_hotel_main, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))
}