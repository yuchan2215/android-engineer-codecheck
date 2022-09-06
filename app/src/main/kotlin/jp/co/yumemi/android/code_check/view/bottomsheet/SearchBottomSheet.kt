package jp.co.yumemi.android.code_check.view.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.yumemi.android.code_check.databinding.SearchBottomSheetBinding
import jp.co.yumemi.android.code_check.viewmodel.SearchFragmentViewModel

class SearchBottomSheet : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "SEARCH_BOTTOM_SHEET"
    }

    private val viewModel:
        SearchFragmentViewModel by viewModels({ requireParentFragment() })

    private var _binding: SearchBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SearchBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.reSearchButton.setOnClickListener {
            viewModel.fetchResults(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
