package jp.co.yumemi.android.code_check.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.databinding.GitUserDetailFragmentBinding
import jp.co.yumemi.android.code_check.viewmodel.UserDetailViewModel

class GitUserDetailFragment : Fragment(R.layout.git_user_detail_fragment) {
    private val args: GitUserDetailFragmentArgs by navArgs()

    val viewModel: UserDetailViewModel by viewModels {
        UserDetailViewModel.Companion.Factory(args.userName)
    }

    /**
     * メニューのリスナー。
     * ブラウザで開くボタンとシェアボタンが用意されている。
     */
    private val listener = Toolbar.OnMenuItemClickListener { menuItem ->
        val url = viewModel.loadedUser.value?.url
        when (menuItem.itemId) {
            R.id.open_browser -> {
                if (url == null) return@OnMenuItemClickListener false
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
            R.id.share -> {
                if (url == null) return@OnMenuItemClickListener false
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, url)
                }
                startActivity(intent)
            }
            else -> return@OnMenuItemClickListener false
        }
        return@OnMenuItemClickListener true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = GitUserDetailFragmentBinding.bind(view)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.apply {
            this@apply.viewModel = this@GitUserDetailFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
            toolBar.setupWithNavController(navController, appBarConfiguration)
            toolBar.setOnMenuItemClickListener(listener)
        }
        viewModel.fetchStatus()
    }
}
