package com.aly.travelio.ui.dashboard.trips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aly.travelio.R
import com.aly.travelio.databinding.CustomListExpBinding
import com.aly.travelio.model.Experience
import com.bumptech.glide.Glide

class ExperienceAdapter : ListAdapter<Experience, ExperienceAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(val binding: CustomListExpBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Experience) {
            binding.expName.text  = item.title
            // duration stored as e.g. "2" → display as "2 Hours"
            val durationText = item.duration.trim()
            binding.time.text = if (durationText.contains("hour", ignoreCase = true) ||
                durationText.contains("min", ignoreCase = true)) {
                durationText           // already formatted
            } else {
                "$durationText Hours"
            }
            Glide.with(binding.tripImage)
                .load(item.image)
                .placeholder(R.color.card_1)
                .centerCrop()
                .into(binding.tripImage)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Experience>() {
        override fun areItemsTheSame(old: Experience, new: Experience) = old.title == new.title
        override fun areContentsTheSame(old: Experience, new: Experience) = old == new
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CustomListExpBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.custom_list_exp, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))
}
