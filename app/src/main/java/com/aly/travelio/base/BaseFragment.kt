package com.aly.travelio.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.aly.travelio.ui.dashboard.main.BookDialogFragment
import com.aly.travelio.util.ConnectionLiveData
import com.aly.travelio.util.LoadingDialog
import com.aly.travelio.util.NavigationCommands
import com.aly.travelio.util.Util


abstract class BaseFragment<T : ViewBinding>(private val inflateMethod : (LayoutInflater, ViewGroup?, Boolean) -> T) : Fragment() {
    //open val viewModel : SharedViewModel by activityViewModels() //->> all viewModels same instance but no args sent
    //open val viewModel : SharedViewModel by viewModels () //->> all viewModels diff instance but args sent
   // open val viewModel : SharedViewModel by viewModels ({ requireActivity() }) //->> all viewModels same instance but args sent
    private var _binding : T? = null
    val binding : T get() = _binding!!
    abstract val viewModel: BaseViewModel
    open val isOnline = MutableLiveData(false)

    open fun T.initialize(){}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = inflateMethod.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.initialize()
    }


    override fun onStart() {
        super.onStart()
        ConnectionLiveData(requireContext()).observe(viewLifecycleOwner){ isOnline.value = it }

        viewModel.showErrorMessage.observe(viewLifecycleOwner){
            if (it != null) {
                Util.alertMsg(requireView(), msg = it)
            }
        }

        viewModel.showToast.observe(viewLifecycleOwner){
            if (it != null) {
                Util.toastMsg(requireContext(), msg = it)
            }
        }

        viewModel.navigationCommand.observe(viewLifecycleOwner){
            if (it != null){
                when(it){
                    is NavigationCommands.To -> {
                        findNavController().navigate(it.directions)
                        viewModel.resetNavCommand()
                    }
                    is NavigationCommands.Back -> {
                        findNavController().popBackStack()
                        viewModel.resetNavCommand()
                    }
                }
            }
        }
        viewModel.loading.observe(viewLifecycleOwner){
            if (it){
                LoadingDialog.showDialogue(requireContext())
            }else{
                LoadingDialog.hideDialogue()
            }
        }

    }

     fun launchBookDialog(
         title: String,
         type: String,
         refId: String,
         pricePerPerson: Double,
         imageUrl: String = "",
         location: String = "",
         startDate: Long = 0L
     ) {
        BookDialogFragment.newInstance(type, refId, title, location, pricePerPerson, imageUrl, startDate)
            .show(childFragmentManager, "bookDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LoadingDialog.hideDialogue()
        _binding = null
    }
}