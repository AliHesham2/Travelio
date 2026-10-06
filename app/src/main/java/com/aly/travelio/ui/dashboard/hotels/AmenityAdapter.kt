package com.aly.travelio.ui.dashboard.hotels

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aly.travelio.R
import com.aly.travelio.databinding.CustomAmenitiesBinding
import com.aly.travelio.model.Amenity
import com.bumptech.glide.Glide

class AmenityAdapter : ListAdapter<Amenity, AmenityAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(val binding: CustomAmenitiesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Amenity) {
            binding.location.text = item.title
            // icon field may be a URL or a drawable resource name
            if (item.icon.isNotBlank()) {
                Glide.with(binding.image)
                    .load(item.icon)
                    .placeholder(R.color.card_1)
                    .error(R.color.card_1)
                    .fitCenter()
                    .into(binding.image)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Amenity>() {
        override fun areItemsTheSame(old: Amenity, new: Amenity) = old.title == new.title
        override fun areContentsTheSame(old: Amenity, new: Amenity) = old == new
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CustomAmenitiesBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.custom_amenities, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))
}
