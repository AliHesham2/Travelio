package com.aly.travelio.ui.dashboard.main

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.aly.travelio.R
import com.aly.travelio.databinding.FragmentBookDialogBinding
import com.aly.travelio.ui.dashboard.DashBoardViewModel
import com.aly.travelio.util.ResultCallBack
import com.aly.travelio.util.toDisplayPrice
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookDialogFragment : DialogFragment() {

    private var _binding: FragmentBookDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashBoardViewModel by activityViewModels()

    private var guestCount = 1
    private var pricePerPerson = 0.0

    // ── Args ─────────────────────────────────────────────────────────────────
    companion object {
        private const val ARG_TYPE             = "type"
        private const val ARG_REF_ID           = "refId"
        private const val ARG_TITLE            = "title"
        private const val ARG_LOCATION         = "location"
        private const val ARG_PRICE_PER_PERSON = "pricePerPerson"
        private const val ARG_IMAGE_URL        = "imageUrl"
        private const val ARG_START_DATE       = "startDate"
        private const val ARG_END_DATE         = "endDate"

        fun newInstance(
            type: String,
            refId: String,
            title: String,
            location: String,
            pricePerPerson: Double,
            imageUrl: String = "",
            startDate: Long = 0L,
            endDate: Long = 0L
        ) = BookDialogFragment().apply {
            arguments = bundleOf(
                ARG_TYPE             to type,
                ARG_REF_ID           to refId,
                ARG_TITLE            to title,
                ARG_LOCATION         to location,
                ARG_PRICE_PER_PERSON to pricePerPerson,
                ARG_IMAGE_URL        to imageUrl,
                ARG_START_DATE       to startDate,
                ARG_END_DATE         to endDate
            )
        }
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBookDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawableResource(android.R.color.transparent)
            setWindowAnimations(R.style.DialogSlideAnimation)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args         = requireArguments()
        val type         = args.getString(ARG_TYPE, "")
        val refId        = args.getString(ARG_REF_ID, "")
        val title        = args.getString(ARG_TITLE, "")
        val location     = args.getString(ARG_LOCATION, "")
        pricePerPerson   = args.getDouble(ARG_PRICE_PER_PERSON, 0.0)
        val imageUrl     = args.getString(ARG_IMAGE_URL, "")
        val startDate    = args.getLong(ARG_START_DATE, 0L)
        val endDate      = args.getLong(ARG_END_DATE, 0L)

        // Static content
        binding.header.text   = title
        binding.location.text = location
        updatePriceDisplay()

        // Counter
        binding.btnPlus.setOnClickListener {
            guestCount++
            updatePriceDisplay()
        }
        binding.btnMinus.setOnClickListener {
            if (guestCount > 1) {
                guestCount--
                updatePriceDisplay()
            }
        }

        // Confirm
        binding.confirmUpdate.setOnClickListener {
            viewModel.addReservation(
                type          = type,
                refId         = refId,
                title         = title,
                imageUrl      = imageUrl,
                location      = location,
                pricePerPerson = pricePerPerson,
                personsCount  = guestCount,
                startDate     = startDate,
                endDate       = endDate
            )
        }

        // Cancel
        binding.cancel.setOnClickListener { dismiss() }

        // Observe result
        viewModel.reservationResult.observe(viewLifecycleOwner) { result ->
            result ?: return@observe
            when (result) {
                is ResultCallBack.Success -> {
                    viewModel.resetReservationResult()
                    showSuccessState(title)
                }
                is ResultCallBack.Error -> {
                    viewModel.showToast.value = result.message
                    viewModel.resetReservationResult()
                }
            }
        }
    }

    private fun updatePriceDisplay() {
        binding.tvCount.text    = guestCount.toString()
        val total               = pricePerPerson * guestCount
        binding.totalPrice.text = total.toDisplayPrice()
    }

    // ── Success state ─────────────────────────────────────────────────────────
    private fun showSuccessState(bookedTitle: String) {
        // Fade out the booking form section
        ObjectAnimator.ofFloat(binding.email, View.ALPHA, 1f, 0f).apply {
            duration = 250
            interpolator = DecelerateInterpolator()
            start()
        }
        ObjectAnimator.ofFloat(binding.totalMount, View.ALPHA, 1f, 0f).apply {
            duration = 250; start()
        }
        ObjectAnimator.ofFloat(binding.totalPrice, View.ALPHA, 1f, 0f).apply {
            duration = 250; start()
        }

        // Update header & subtitle with success info
        binding.header.animate()
            .alpha(0f).setDuration(200)
            .withEndAction {
                binding.header.text = getString(R.string.booking_confirmed)
                binding.header.animate().alpha(1f).setDuration(250).start()
            }.start()

        binding.location.animate()
            .alpha(0f).setDuration(200)
            .withEndAction {
                binding.location.text = bookedTitle
                binding.location.animate().alpha(1f).setDuration(250).start()
            }.start()

        // Change confirm button to "Done"
        binding.confirmUpdate.animate()
            .alpha(0f).setDuration(150)
            .withEndAction {
                binding.confirmUpdate.text = getString(R.string.done)
                binding.confirmUpdate.setOnClickListener { dismiss() }
                binding.confirmUpdate.animate().alpha(1f).setDuration(250).start()
            }.start()

        // Hide cancel link
        binding.cancel.animate().alpha(0f).setDuration(200).withEndAction {
            binding.cancel.visibility = View.GONE
        }.start()

        // Animate icon to bounce in
        binding.appCompatImageView4.animate()
            .scaleX(1.3f).scaleY(1.3f).setDuration(200)
            .withEndAction {
                binding.appCompatImageView4.animate()
                    .scaleX(1f).scaleY(1f).setDuration(200).start()
            }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
