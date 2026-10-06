package com.aly.travelio.ui.registration

import androidx.fragment.app.activityViewModels
import com.aly.travelio.R
import com.aly.travelio.base.BaseFragment
import com.aly.travelio.databinding.FragmentSignUpBinding
import com.aly.travelio.util.ResultCallBack
import com.aly.travelio.util.hideSoftKeyboard
import com.aly.travelio.util.navigateToSignIn
import com.aly.travelio.util.setOnThrottledClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>(FragmentSignUpBinding::inflate) {
    override val viewModel: RegistrationViewModel by activityViewModels()
    
    override fun FragmentSignUpBinding.initialize() {
        handleClicks()
        handleObservers()
    }

    private fun handleClicks(){
        binding.signUp.setOnThrottledClickListener {
            binding.signUp.hideSoftKeyboard(requireContext())
            
            val name = binding.etFullName.editText?.text?.toString()?.trim()
            val email = binding.etEmail.editText?.text?.toString()?.trim()
            val password = binding.etPassword.editText?.text?.toString()
            
            if (email.isNullOrEmpty() || password.isNullOrEmpty() || name.isNullOrEmpty()) {
                viewModel.showToast.value = getString(R.string.NO_DATA)
                return@setOnThrottledClickListener
            } 
            
            if (name.length <= 2 || name.matches(Regex(".*\\d.*"))) {
                viewModel.showToast.value = getString(R.string.INVALID_NAME)
                return@setOnThrottledClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                viewModel.showToast.value = getString(R.string.INVALID_EMAIL_FORM)
                return@setOnThrottledClickListener
            }
            
            viewModel.signUp(email, password, name)
        }

        binding.signInBtn.setOnThrottledClickListener { navigateToSignIn() }
        
        binding.btnGoogle.setOnThrottledClickListener {
            viewModel.googleSign(requireContext())
        }
    }

    private fun handleObservers(){
        viewModel.signIn.observe(viewLifecycleOwner){ result ->
            when(result){
                is ResultCallBack.Success -> { handleSuccess() }
                is ResultCallBack.Error -> { handleError(result.message) }
                else -> {}
            }
        }
    }

    private fun handleSuccess(){
        if(requireActivity() is RegistrationActivity){
            (requireActivity() as RegistrationActivity).navigateToDashBoard()
        }
    }
    
    private fun handleError(message: String?) {
        viewModel.showErrorMessage.value = message
        viewModel.resetSignIn()
    }
}