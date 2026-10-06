package com.aly.travelio.ui.dashboard.transport

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aly.travelio.R
import com.aly.travelio.databinding.CustomItemDashboardTransBinding
import com.aly.travelio.databinding.CustomItemTransMainBinding
import com.aly.travelio.model.Transportation
import com.aly.travelio.util.toDisplayDate
import com.aly.travelio.util.toDisplayPrice
import java.util.Locale

class TransportAdapter(
    private val isDashboard: Boolean = false,
    private val onClick: (Transportation) -> Unit
) : ListAdapter<Transportation, RecyclerView.ViewHolder>(DiffCallback) {

    // ── Vehicle type theme ────────────────────────────────────────────────────
    private data class TypeTheme(
        @DrawableRes val iconBg: Int,
        @DrawableRes val iconSrc: Int,
        @DrawableRes val horLineBg: Int,
        @DrawableRes val transIconSrc: Int,
        @ColorRes val priceColor: Int
    )

    private fun themeFor(type: String): TypeTheme = when (type.lowercase(Locale.US)) {
        "car" -> TypeTheme(
            iconBg       = R.drawable.custom_trans_card_car,
            iconSrc      = R.drawable.trans_car,
            horLineBg    = R.drawable.custom_car_hor_line,
            transIconSrc = R.drawable.trans_car_des,
            priceColor   = R.color.trans_card_car_text
        )
        "bus" -> TypeTheme(
            iconBg       = R.drawable.custom_trans_card_boat_bus,
            iconSrc      = R.drawable.trans_bus,
            horLineBg    = R.drawable.custom_boat_bus_hor_line,
            transIconSrc = R.drawable.trans_bus_des,
            priceColor   = R.color.trans_card_boat_bus_text
        )
        "boat" -> TypeTheme(
            iconBg       = R.drawable.custom_trans_card_boat_bus,
            iconSrc      = R.drawable.trans_boat,
            horLineBg    = R.drawable.custom_boat_bus_hor_line,
            transIconSrc = R.drawable.trans_boat_des,
            priceColor   = R.color.trans_card_boat_bus_text
        )
        else -> TypeTheme( // "plane" / default
            iconBg       = R.drawable.custom_trans_card_plan,
            iconSrc      = R.drawable.trans_plan,
            horLineBg    = R.drawable.custom_plane_hor_line,
            transIconSrc = R.drawable.trans_plane_des,
            priceColor   = R.color.trans_card_plan_text
        )
    }

    // ── ViewHolders ───────────────────────────────────────────────────────────
    inner class MainViewHolder(val binding: CustomItemTransMainBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Transportation) {
            val ctx   = binding.root.context
            val theme = themeFor(item.transportType)
            binding.appCompatImageButton.setBackgroundResource(theme.iconBg)
            binding.appCompatImageButton.setImageResource(theme.iconSrc)
            binding.startAirPort.text            = item.startAirportShort
            binding.startAirPortName.text        = item.startAirportFull.uppercase(Locale.US)
            binding.destinationAirPort.text      = item.destinationAirportShort
            binding.destinationAirPortName.text  = item.destinationAirportFull.uppercase(Locale.US)
            binding.startDate.text = if (item.departDate > 0) item.departDate.toDisplayDate() else "--"
            binding.duration.text  = item.duration.ifBlank { "--" }
            binding.price.text = item.pricePerPerson.toDisplayPrice()
            binding.price.setTextColor(ContextCompat.getColor(ctx, theme.priceColor))
            binding.horLine.setBackgroundResource(theme.horLineBg)
            binding.transIcon.setImageResource(theme.transIconSrc)
            binding.btnBook.setOnClickListener { onClick(item) }
            binding.root.setOnClickListener    { onClick(item) }
        }
    }

    inner class DashboardViewHolder(val binding: CustomItemDashboardTransBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Transportation) {
            val ctx   = binding.root.context
            val theme = themeFor(item.transportType)
            binding.appCompatImageButton.setBackgroundResource(theme.iconBg)
            binding.appCompatImageButton.setImageResource(theme.iconSrc)
            binding.startAirPort.text            = item.startAirportShort
            binding.startAirPortName.text        = item.startAirportFull.uppercase(Locale.US)
            binding.destinationAirPort.text      = item.destinationAirportShort
            binding.destinationAirPortName.text  = item.destinationAirportFull.uppercase(Locale.US)
            binding.startDate.text = if (item.departDate > 0) item.departDate.toDisplayDate() else "--"
            binding.duration.text  = item.duration.ifBlank { "--" }
            binding.price.text = item.pricePerPerson.toDisplayPrice()
            binding.price.setTextColor(ContextCompat.getColor(ctx, theme.priceColor))
            binding.horLine.setBackgroundResource(theme.horLineBg)
            binding.transIcon.setImageResource(theme.transIconSrc)
            binding.btnBook.setOnClickListener { onClick(item) }
            binding.root.setOnClickListener    { onClick(item) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Transportation>() {
        override fun areItemsTheSame(old: Transportation, new: Transportation) = old.id == new.id
        override fun areContentsTheSame(old: Transportation, new: Transportation) = old == new
    }

    override fun getItemViewType(position: Int): Int = if (isDashboard) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 1) {
            DashboardViewHolder(DataBindingUtil.inflate(inflater, R.layout.custom_item_dashboard_trans, parent, false))
        } else {
            MainViewHolder(DataBindingUtil.inflate(inflater, R.layout.custom_item_trans_main, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (holder is MainViewHolder) holder.bind(item)
        else if (holder is DashboardViewHolder) holder.bind(item)
    }
}