package com.aly.travelio.ui.registration


import android.content.Intent
import androidx.fragment.app.activityViewModels
import com.aly.travelio.base.BaseFragment
import com.aly.travelio.databinding.FragmentRegistrationBinding
import com.aly.travelio.util.navigateToSignIn
import com.aly.travelio.util.setOnThrottledClickListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegistrationFragment : BaseFragment<FragmentRegistrationBinding>(FragmentRegistrationBinding::inflate) {
    override val viewModel: RegistrationViewModel by activityViewModels()
    
    override fun FragmentRegistrationBinding.initialize() {
        handleClicks()
    }

    private fun handleClicks(){
        binding.getStarted.setOnThrottledClickListener { navigateToSignIn() }
    }
}