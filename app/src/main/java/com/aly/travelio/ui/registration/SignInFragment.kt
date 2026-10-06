package com.aly.travelio.ui.registration


import androidx.fragment.app.activityViewModels
import com.aly.travelio.R
import com.aly.travelio.base.BaseFragment
import com.aly.travelio.databinding.FragmentSignInBinding
import com.aly.travelio.ui.user.edit.ForgetPasswordFragment
import com.aly.travelio.util.ResultCallBack
import com.aly.travelio.util.navigateToSignUp
import com.aly.travelio.util.setOnThrottledClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : BaseFragment<FragmentSignInBinding>(FragmentSignInBinding::inflate) {
    override val viewModel: RegistrationViewModel by activityViewModels()

    override fun FragmentSignInBinding.initialize() {
        handleClicks()
        handleObservers()
    }

    private fun handleClicks(){
        binding.signIn.setOnThrottledClickListener {
            val email = binding.etEmail.editText?.text.toString()
            val pass = binding.etPassword.editText?.text.toString()
            if( email.isEmpty() || pass.isEmpty() ){
                viewModel.showToast.value = getString(R.string.NO_DATA)
            } else {
                viewModel.signIn(email, pass)
            }
        }

        binding.forgetPassword.setOnThrottledClickListener { openForgetPasswordDialog() }
        binding.signUpBtn.setOnThrottledClickListener{ navigateToSignUp() }

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

    private fun openForgetPasswordDialog(){
        ForgetPasswordFragment.newInstance().show(childFragmentManager, "ForgetPasswordFragment")
    }
}