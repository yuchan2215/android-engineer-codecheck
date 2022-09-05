/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.databinding.SearchFragmentBinding
import jp.co.yumemi.android.code_check.model.github.repositories.GitRepository
import jp.co.yumemi.android.code_check.model.status.RequestStatus
import jp.co.yumemi.android.code_check.view.adapter.GitRepositoryListAdapter
import jp.co.yumemi.android.code_check.viewmodel.SearchFragmentViewModel

/**
 * GitHubリポジトリを検索するフラグメント。
 * 検索結果をリストに表示する。
 * リストのアイテムがタップされたら[GitRepositoryDetailFragment]へ推移する。
 */
class SearchFragment : Fragment(R.layout.search_fragment) {
    private val viewModel by viewModels<SearchFragmentViewModel>()

    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = SearchFragmentBinding.bind(view)

        val adapter = object : GitRepositoryListAdapter() {
            override fun itemClick(item: GitRepository) {
                gotoRepositoryFragment(item)
            }
        }

        // 結果が更新されたらリストを更新する。
        viewModel.requestStatus.observe(viewLifecycleOwner) {
            if (it !is RequestStatus.OnSuccess) return@observe

            adapter.submitList(it.body.repositories)
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.searchInputText
            .setOnEditorActionListener { editText, action, _ ->
                if (action != EditorInfo.IME_ACTION_SEARCH)
                    return@setOnEditorActionListener false

                val text = editText.text.toString()
                viewModel.fetchResults(text)

                hideKeyboard()
                clearFocus()

                return@setOnEditorActionListener true
            }

        binding.recyclerView.also {

            // 項目を線で区切る
            val layoutManager = LinearLayoutManager(requireContext())
            val dividerItemDecoration =
                DividerItemDecoration(requireContext(), layoutManager.orientation)
            it.layoutManager = layoutManager
            it.addItemDecoration(dividerItemDecoration)

            it.adapter = adapter
        }
    }

    private fun clearFocus() {
        binding.constraintLayout.requestFocus()
    }

    private fun hideKeyboard() {
        requireActivity().currentFocus?.let { view ->
            val manager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)
            manager as InputMethodManager
            manager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun gotoRepositoryFragment(item: GitRepository) {
        val action = SearchFragmentDirections
            .openRepositoryDetail(repository = item)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
