package jp.co.yumemi.android.code_check.view.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.yumemi.android.code_check.databinding.SearchBottomSheetBinding

class SearchBottomSheet : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "SEARCH_BOTTOM_SHEET"
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
