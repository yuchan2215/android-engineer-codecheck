/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.databinding.GitRepositoryDetailFragmentBinding
import jp.co.yumemi.android.code_check.view.activity.TopActivity.Companion.lastSearchDate
import jp.co.yumemi.android.code_check.viewmodel.GitRepositoryDetailFragmentViewModel

/**
 * GitHubリポジトリの詳細を表示するフラグメント
 * 表示する内容は[GitRepositoryDetailFragmentArgs]から渡される。
 */
class GitRepositoryDetailFragment : Fragment(R.layout.git_repository_detail_fragment) {

    private val viewModel by viewModels<GitRepositoryDetailFragmentViewModel> {
        val args: GitRepositoryDetailFragmentArgs by navArgs()
        GitRepositoryDetailFragmentViewModel.Companion.Factory(args.repository)
    }

    private val repositoryUrl: String by lazy {
        viewModel.repository.htmlUrl
    }

    /**
     * メニューのリスナー。
     * ブラウザで開くボタンとシェアボタンが用意されている。
     */
    private val listener = Toolbar.OnMenuItemClickListener { menuItem ->
        when (menuItem.itemId) {
            R.id.open_browser -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repositoryUrl))
                startActivity(intent)
            }
            R.id.share -> {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, repositoryUrl)
                }
                startActivity(intent)
            }
            else -> return@OnMenuItemClickListener false
        }
        return@OnMenuItemClickListener true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("検索した日時", lastSearchDate.toString())

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        GitRepositoryDetailFragmentBinding.bind(view).apply {
            this.viewModel = this@GitRepositoryDetailFragment.viewModel
            lifecycleOwner = viewLifecycleOwner

            toolBar.setupWithNavController(navController, appBarConfiguration)
            toolBar.setOnMenuItemClickListener(listener)

            ownerCard.setOnClickListener {
                val name = viewModel?.repository?.owner?.name ?: return@setOnClickListener
                findNavController().navigate(
                    GitRepositoryDetailFragmentDirections.openUserDetail(name)
                )
            }
        }
    }
}
