package jp.co.yumemi.android.code_check.view.fragment.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
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

        val bottomSheetBehavior = (dialog as BottomSheetDialog).behavior
        // 全て展開する
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        // 中途半端は表示をスキップする
        bottomSheetBehavior.skipCollapsed = true
        // レイアウトに含まれるスクロールビューが一番上にない時はドラッグできないようにする
        binding.bottomSheetScrollView.setOnScrollChangeListener { _, _, y, _, _ ->
            bottomSheetBehavior.isDraggable = y == 0
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
