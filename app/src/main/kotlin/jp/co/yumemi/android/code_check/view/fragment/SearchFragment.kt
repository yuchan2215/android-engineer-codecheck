/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Date
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.databinding.SearchFragmentBinding
import jp.co.yumemi.android.code_check.model.github.repositories.GitRepository
import jp.co.yumemi.android.code_check.view.adapter.GitRepositoryListAdapter
import jp.co.yumemi.android.code_check.view.bottomsheet.SearchBottomSheet
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

    /**
     * 検索した際に呼び出されるリスナー。
     * [viewModel]に値を渡し、キーボードを閉じてフォーカスを外す。（横画面のフルスクリーンキーボード対策）
     */
    private val editorActionListener = TextView.OnEditorActionListener { editText, actionId, _ ->
        if (actionId != EditorInfo.IME_ACTION_SEARCH)
            return@OnEditorActionListener false

        viewModel.fetchResults(false)

        hideKeyboard()
        clearFocus()

        return@OnEditorActionListener true
    }

    /**
     * [GitRepositoryListAdapter]を実装する。
     * クリックすると[gotoRepositoryFragment]を呼び出す。
     */
    private val repositoryListAdapter = object : GitRepositoryListAdapter() {
        override fun itemClick(item: GitRepository) {
            gotoRepositoryFragment(item)
        }
    }

    /**
     * 一番下までスクロールした際に、追加で読み込む。
     * 上方向のスクロール・１秒(1000ms)未満の際読み込みは行わない。
     * 参考： https://qiita.com/u-dai/items/0b1661e8329adf41830a
     */
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        var lastFetch = Date()
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            // アダプターが保持しているアイテムの合計
            val itemCount = repositoryListAdapter.itemCount
            // 画面に表示されているアイテム数
            val childCount = recyclerView.childCount
            val manager = recyclerView.layoutManager as LinearLayoutManager
            // 画面に表示されている一番上のアイテムの位置
            val firstPosition = manager.findFirstVisibleItemPosition()

            // 何度もリクエストしないようにロード中は何もしない。
            if (viewModel.isAdditionLoading.value == true) {
                return
            }
            // 上方向のスクロールは処理しない。
            if (dy < 0) {
                return
            }

            // 以下の条件に当てはまれば一番下までスクロールされたと判断できる。
            if (itemCount == childCount + firstPosition) {

                // 最後に読み込んでから1秒以内は再読み込みしない。(重複対策)
                val diff = Date().time - lastFetch.time
                if (diff < 1000) {
                    return
                }
                lastFetch = Date()
                viewModel.fetchResults(true)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = SearchFragmentBinding.bind(view)

        // 結果が更新されたらリストを更新する。
        viewModel.requestCache.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            repositoryListAdapter.submitList(it.allData)
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // 検索時のアクション
        binding.searchInputText.setOnEditorActionListener(editorActionListener)

        binding.recyclerView.also {

            // 項目を線で区切る
            val layoutManager = LinearLayoutManager(requireContext())
            val dividerItemDecoration =
                DividerItemDecoration(requireContext(), layoutManager.orientation)
            it.layoutManager = layoutManager
            it.addItemDecoration(dividerItemDecoration)

            it.adapter = repositoryListAdapter
            it.addOnScrollListener(scrollListener)
        }

        binding.floatingButton.setOnClickListener {
            val bottomSheet = SearchBottomSheet()
            bottomSheet.show(this.childFragmentManager, SearchBottomSheet.TAG)
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
