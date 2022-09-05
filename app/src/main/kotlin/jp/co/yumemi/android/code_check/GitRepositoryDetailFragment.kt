/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import jp.co.yumemi.android.code_check.TopActivity.Companion.lastSearchDate
import jp.co.yumemi.android.code_check.databinding.GitRepositoryDetailFragmentBinding

/**
 * GitHubリポジトリの詳細を表示するフラグメント
 * 表示する内容は[GitRepositoryDetailFragmentArgs]から渡される。
 */
class GitRepositoryDetailFragment : Fragment(R.layout.git_repository_detail_fragment) {

    private val args: GitRepositoryDetailFragmentArgs by navArgs()

    private var _binding: GitRepositoryDetailFragmentBinding? = null
    private val binding get() = this._binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("検索した日時", lastSearchDate.toString())

        _binding = GitRepositoryDetailFragmentBinding.bind(view)

        val item = args.repository

        binding.apply {
            val starsText =
                resources.getQuantityString(
                    R.plurals.github_stars,
                    item.stargazersCount,
                    item.stargazersCount
                )
            val watchersText =
                resources.getQuantityString(
                    R.plurals.github_watchers,
                    item.watchersCount,
                    item.watchersCount
                )
            val forksText =
                resources.getQuantityString(
                    R.plurals.github_forks,
                    item.forksCount,
                    item.forksCount
                )
            val openIssuesText =
                resources.getQuantityString(
                    R.plurals.github_open_issues,
                    item.openIssuesCount,
                    item.openIssuesCount
                )

            ownerIconView.load(item.owner?.avatarUrl)
            nameView.text = item.name
            languageView.text = item.language
            starsView.text = starsText
            watchersView.text = watchersText
            forksView.text = forksText
            openIssuesView.text = openIssuesText
        }
    }
}
