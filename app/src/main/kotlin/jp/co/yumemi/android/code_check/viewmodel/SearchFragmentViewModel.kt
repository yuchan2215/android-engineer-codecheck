/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import java.util.Date
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.model.github.repositories.GitRepository
import jp.co.yumemi.android.code_check.model.github.repositories.SearchGitRepoResponse
import jp.co.yumemi.android.code_check.model.status.FetchQuery
import jp.co.yumemi.android.code_check.model.status.RequestStatus
import jp.co.yumemi.android.code_check.repository.GitHubApiRepository
import jp.co.yumemi.android.code_check.util.QuantityStringUtil
import jp.co.yumemi.android.code_check.view.activity.TopActivity
import jp.co.yumemi.android.code_check.view.fragment.SearchFragment
import kotlinx.coroutines.launch

/**
 * [SearchFragment]で利用されるViewModel。
 */
class SearchFragmentViewModel : ViewModel() {

    private val requestStatus: MutableLiveData<RequestStatus<SearchGitRepoResponse>> =
        MutableLiveData(RequestStatus.Nothing())

    private val _repositoryList: MutableLiveData<List<GitRepository>> = MutableLiveData(null)
    val repositoryList: LiveData<List<GitRepository>> = _repositoryList

    /**追加のページを読み込んでいるかどうか*/
    val additionLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    val errorText: LiveData<String> = requestStatus.map {
        if (it !is RequestStatus.OnError) ""
        else it.error.errorDescription
    }

    val repositoryCount: LiveData<String> = requestStatus.map {
        if (it !is RequestStatus.OnSuccess) ""
        else QuantityStringUtil.getString(R.plurals.repository_counts, it.body.totalCount)
    }

    private val _lastFetchQuery: MutableLiveData<FetchQuery?> = MutableLiveData(null)
    val lastFetchQuery: LiveData<FetchQuery?> = _lastFetchQuery

    /**
     * GitHubのAPIを叩き、リポジトリ一覧を取得して[_requestStatus]の値を更新する。
     * 読み込むまでは[RequestStatus.OnLoading]にする。
     * @param newFetchQuery 検索クエリ
     */
    fun fetchResults(newFetchQuery: FetchQuery) {
        requestStatus.value = RequestStatus.OnLoading()
        viewModelScope.launch {
            TopActivity.lastSearchDate = Date()

            // 最後のクエリ
            val lastQuery = lastFetchQuery.value
            // 次のページを読み込むか？
            val isNextPage = lastQuery != null && lastQuery.isNextFetch(newFetchQuery)
            // ステータスを更新
            requestStatus.value = GitHubApiRepository.getRepositories(newFetchQuery)

            // 失敗したなら空にする
            if (requestStatus.value !is RequestStatus.OnSuccess) {
                _lastFetchQuery.value = null
                _repositoryList.value = listOf()
                additionLoading.value = false
                return@launch
            }
            val successResult = requestStatus.value as RequestStatus.OnSuccess

            if (isNextPage) { // 次のページを読み込むなら
                // 成功したならリストに値を足す
                val oldList = _repositoryList.value ?: listOf()
                val newList = oldList.toMutableList().apply {
                    addAll(successResult.body.repositories)
                }
                _repositoryList.value = newList
            } else { // 新規のページを読み込むなら
                _repositoryList.value = successResult.body.repositories
            }
            // 状態を変更
            _lastFetchQuery.value = newFetchQuery
            additionLoading.value = false
        }
    }
}
