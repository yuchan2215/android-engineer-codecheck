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

class GitRepositoryDetailFragment : Fragment(R.layout.git_repository_detail_fragment) {

    private val args: GitRepositoryDetailFragmentArgs by navArgs()

    private var _binding: GitRepositoryDetailFragmentBinding? = null
    private val binding get() = this._binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("検索した日時", lastSearchDate.toString())

        _binding = GitRepositoryDetailFragmentBinding.bind(view)

        val item = args.item

        binding.apply{
            ownerIconView.load(item.owner?.avatarUrl);
            nameView.text = item.name;
            languageView.text = item.language;
            starsView.text = "${item.stargazersCount} stars";
            watchersView.text = "${item.watchersCount} watchers";
            forksView.text = "${item.forksCount} forks";
            openIssuesView.text = "${item.openIssuesCount} open issues";
        }
    }
}
