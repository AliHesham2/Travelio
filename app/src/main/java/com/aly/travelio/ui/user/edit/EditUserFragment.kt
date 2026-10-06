package com.aly.travelio.ui.user.edit

import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.aly.travelio.R
import com.aly.travelio.base.BaseFragment
import com.aly.travelio.databinding.FragmentEditUserBinding
import com.aly.travelio.model.User
import com.aly.travelio.ui.dashboard.DashBoardViewModel
import com.aly.travelio.util.ResultCallBack
import com.aly.travelio.util.hideSoftKeyboard
import com.aly.travelio.util.navigateBack
import com.aly.travelio.util.setOnThrottledClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditUserFragment : BaseFragment<FragmentEditUserBinding>(FragmentEditUserBinding::inflate) {

    override val viewModel: DashBoardViewModel by activityViewModels()

    override fun FragmentEditUserBinding.initialize() {
        setupEmailField()
        setupValidation()
        handleClicks()
        handleObservers()
    }

    // ── Email is read-only ────────────────────────────────────────────────────
    private fun setupEmailField() {
        binding.etEmail.editText?.apply {
            isEnabled   = false
            isFocusable = false
        }
        // Slightly dim the disabled field so it's visually distinct
        binding.etEmail.alpha = 0.55f
    }

    // ── Validate full name as user types ──────────────────────────────────────
    private fun setupValidation() {
        binding.etFullName.editText?.doOnTextChanged { text, _, _, _ ->
            when {
                text.isNullOrEmpty()     -> {
                    binding.etFullName.editText?.error = getString(R.string.NO_NAME)
                    binding.etFullName.endIconDrawable = null
                }
                text.trim().length <= 2  -> {
                    binding.etFullName.editText?.error = getString(R.string.INVALID_NAME)
                    binding.etFullName.endIconDrawable = null
                }
                else -> {
                    binding.etFullName.editText?.error = null
                }
            }
        }
    }

    // ── Button click ──────────────────────────────────────────────────────────
    private fun handleClicks() {
        binding.signUp.setOnThrottledClickListener {
            binding.signUp.hideSoftKeyboard(requireContext())
            val name = binding.etFullName.editText?.text?.trim().toString()
            when {
                name.isEmpty()   -> viewModel.showToast.value = getString(R.string.NO_NAME)
                name.length <= 2 -> viewModel.showToast.value = getString(R.string.INVALID_NAME)
                else             -> viewModel.updateUserName(name)
            }
        }
    }

    // ── Observers ─────────────────────────────────────────────────────────────
    private fun handleObservers() {
        // Pre-fill fields once user data is available
        viewModel.currentUser.observe(viewLifecycleOwner) { result ->
            result ?: return@observe
            if (result is ResultCallBack.Success) bindUser(result.data)
        }

        // Update result
        viewModel.updateNameResult.observe(viewLifecycleOwner) { result ->
            result ?: return@observe
            when (result) {
                is ResultCallBack.Success -> {
                    viewModel.showToast.value = getString(R.string.edit_success)
                    navigateBack()
                }
                is ResultCallBack.Error -> {
                    viewModel.showErrorMessage.value = result.message
                }
            }
        }
    }

    // ── Pre-fill fields ───────────────────────────────────────────────────────
    private fun bindUser(user: User) {
        binding.etFullName.editText?.setText(user.name)
        binding.etEmail.editText?.setText(user.email)
    }

    override fun onDestroy() {
        viewModel.resetUpdateNameResult()
        super.onDestroy()
    }
}