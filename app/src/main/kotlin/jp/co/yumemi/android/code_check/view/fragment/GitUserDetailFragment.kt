package jp.co.yumemi.android.code_check.view.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.databinding.GitUserDetailFragmentBinding
import jp.co.yumemi.android.code_check.viewmodel.UserDetailViewModel

class GitUserDetailFragment : Fragment(R.layout.git_user_detail_fragment) {
    private val args: GitUserDetailFragmentArgs by navArgs()

    val viewModel: UserDetailViewModel by viewModels {
        UserDetailViewModel.Companion.Factory(args.userName)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = GitUserDetailFragmentBinding.bind(view)
        binding.apply {
            this@apply.viewModel = this@GitUserDetailFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        viewModel.fetchStatus()
    }
}
