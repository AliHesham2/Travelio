package com.aly.travelio.ui.user.edit


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.aly.travelio.R
import com.aly.travelio.base.BaseActivity
import com.aly.travelio.databinding.FragmentForgetPasswordBinding
import com.aly.travelio.ui.registration.RegistrationViewModel
import com.aly.travelio.util.ResultCallBack
import com.aly.travelio.util.setOnThrottledClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgetPasswordFragment : DialogFragment() {
    private lateinit var binding: FragmentForgetPasswordBinding
    val viewModel: RegistrationViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = FragmentForgetPasswordBinding.inflate(inflater)
        val alert = AlertDialog.Builder(requireContext())
        alert.setCancelable(false)
        alert.setView(binding.root)
        return alert.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View { return binding.root }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            decorView.background = ResourcesCompat.getDrawable(resources, android.R.color.transparent, null)
            setWindowAnimations(R.style.DialogSlideAnimation)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleValidation()
        handleCLicks()
        handleObservers()
    }


    private fun handleValidation(){
        binding.email.editText?.doOnTextChanged { text, _, _, _ ->
            if(text.isNullOrEmpty()){
                binding.email.editText?.error= this.resources.getString(R.string.NO_EMAIL)
                binding.email.endIconDrawable = null
            }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()){
                binding.email.editText?.error = this.resources.getString(R.string.INVALID_EMAIL_FORM)
                binding.email.endIconDrawable = null
            }else{
                binding.email.setEndIconDrawable(R.drawable.forget_email)
            }
        }
    }

    private fun handleCLicks(){

        binding.backToSignIn.setOnThrottledClickListener{ dismiss() }

        binding.confirmUpdate.setOnThrottledClickListener{
           if (binding.email.editText?.error.isNullOrEmpty() && binding.email.editText?.text?.isNotEmpty() == true){
               viewModel.forgotPassword(binding.email.editText?.text.toString())
           } else{
               viewModel.showToast.value = getString(R.string.NO_DATA)
            }
        }
    }

    private fun handleObservers(){
        viewModel.forgetPassword.observe(viewLifecycleOwner){
            when(it) {
                is ResultCallBack.Success -> { handleSuccess() }
                is ResultCallBack.Error -> {handleError(it.message)}
                else->{}
            }
        }
    }

    private fun handleSuccess(){
        viewModel.resetForgetPassword()
        viewModel.showErrorMessage.value = getString(R.string.email_sent)
        dismiss()
    }

    private fun handleError(message: String?) {
        viewModel.showErrorMessage.value = message
        viewModel.resetForgetPassword()
    }


    companion object {
        fun newInstance(): ForgetPasswordFragment { return ForgetPasswordFragment() }
    }
}