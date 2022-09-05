/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.Date
import jp.co.yumemi.android.code_check.model.github.repositories.GitRepository
import jp.co.yumemi.android.code_check.network.GitHubApi
import jp.co.yumemi.android.code_check.view.activity.TopActivity.Companion.lastSearchDate
import kotlinx.coroutines.launch

/**
 * [SearchFragment]で利用されるViewModel。
 */
class SearchFragmentViewModel : ViewModel() {

    private val _searchResults = MutableLiveData<List<GitRepository>?>(null)
    val searchResults: LiveData<List<GitRepository>?> = _searchResults

    /**
     * GitHubのAPIを叩き、リポジトリ一覧を取得して[_searchResults]の値を更新する。
     * @param inputText 検索したいテキスト。
     */
    fun fetchResults(inputText: String) {
        viewModelScope.launch {
            try {
                val response = GitHubApi.gitHubApiService.getRepositories(inputText)
                if (!response.isSuccessful) throw IllegalStateException()

                lastSearchDate = Date()

                _searchResults.value = response.body()!!.repositories
            } catch (_: Throwable) {
                _searchResults.value = null
            }
        }
    }
}
