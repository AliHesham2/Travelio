package com.aly.travelio.ui.orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aly.travelio.R
import com.aly.travelio.databinding.CustomItemDashboardHotelsBinding
import com.aly.travelio.databinding.CustomItemTransMainBinding
import com.aly.travelio.databinding.CustomItemTripMainBinding
import com.aly.travelio.model.Reservation
import com.aly.travelio.util.toDisplayDate
import com.aly.travelio.util.toDisplayPrice
import com.bumptech.glide.Glide
import java.util.Locale

class ReservationAdapter(
    private val onClick: (Reservation) -> Unit
) : ListAdapter<Reservation, ReservationAdapter.BaseViewHolder>(DiffCallback) {

    companion object {
        private const val TYPE_HOTEL     = 0
        private const val TYPE_TRIP      = 1
        private const val TYPE_TRANSPORT = 2

        val DiffCallback = object : DiffUtil.ItemCallback<Reservation>() {
            override fun areItemsTheSame(old: Reservation, new: Reservation) = old.id == new.id
            override fun areContentsTheSame(old: Reservation, new: Reservation) = old == new
        }
    }

    override fun getItemViewType(position: Int): Int = when (
        getItem(position).type.uppercase(Locale.US)
    ) {
        "HOTEL"     -> TYPE_HOTEL
        "TRIP"      -> TYPE_TRIP
        else        -> TYPE_TRANSPORT
    }

    abstract class BaseViewHolder(binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(item: Reservation, onClick: (Reservation) -> Unit)
    }

    // Hotel card
    class HotelViewHolder(val binding: CustomItemDashboardHotelsBinding) : BaseViewHolder(binding) {
        override fun bind(item: Reservation, onClick: (Reservation) -> Unit) {
            binding.hotelName.text  = item.title
            binding.location.text   = item.location
            binding.price.text      = item.totalPrice.toDisplayPrice()
            binding.ratingText.text = if (item.startDate > 0) item.startDate.toDisplayDate() else "Booked"
            Glide.with(binding.hotelImage)
                .load(item.image).placeholder(R.color.card_1).centerCrop().into(binding.hotelImage)
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    // Trip card
    class TripViewHolder(val binding: CustomItemTripMainBinding) : BaseViewHolder(binding) {
        override fun bind(item: Reservation, onClick: (Reservation) -> Unit) {
            binding.name.text     = item.title
            binding.location.text = item.location
            binding.price.text    = item.totalPrice.toDisplayPrice()
            Glide.with(binding.tripImage)
                .load(item.image).placeholder(R.color.card_1).centerCrop().into(binding.tripImage)
            binding.tripDetailBtn.setOnClickListener { onClick(item) }
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    // Transport card
    class TransportViewHolder(val binding: CustomItemTransMainBinding) : BaseViewHolder(binding) {
        override fun bind(item: Reservation, onClick: (Reservation) -> Unit) {
            val parts = item.title.split("→", "->").map { it.trim() }
            binding.startAirPort.text           = parts.getOrElse(0) { item.title }.take(3).uppercase(Locale.US)
            binding.destinationAirPort.text     = parts.getOrElse(1) { "" }.take(3).uppercase(Locale.US)
            binding.startAirPortName.text       = item.location.uppercase(Locale.US)
            binding.destinationAirPortName.text = ""
            binding.startDate.text              = if (item.startDate > 0) item.startDate.toDisplayDate() else "--"
            binding.duration.text               = "${item.personsCount} pax"
            binding.price.text                  = item.totalPrice.toDisplayPrice()
            binding.btnBook.visibility          = View.GONE
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inf = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HOTEL     -> HotelViewHolder(CustomItemDashboardHotelsBinding.inflate(inf, parent, false))
            TYPE_TRIP      -> TripViewHolder(CustomItemTripMainBinding.inflate(inf, parent, false))
            else           -> TransportViewHolder(CustomItemTransMainBinding.inflate(inf, parent, false))
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) =
        holder.bind(getItem(position), onClick)
}
