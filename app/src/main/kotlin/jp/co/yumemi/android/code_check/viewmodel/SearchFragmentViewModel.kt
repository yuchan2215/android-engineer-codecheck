/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import java.util.Date
import jp.co.yumemi.android.code_check.CodeCheckApplication
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.model.github.repositories.GitRepository
import jp.co.yumemi.android.code_check.model.github.repositories.SearchGitRepoResponse
import jp.co.yumemi.android.code_check.model.status.FetchQuery
import jp.co.yumemi.android.code_check.model.status.RequestStatus
import jp.co.yumemi.android.code_check.repository.GitHubApiRepository
import jp.co.yumemi.android.code_check.util.QuantityStringUtil
import jp.co.yumemi.android.code_check.util.VisibilityUtil
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

    val isAdditionLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    val repositoryCountText = MutableLiveData("")

    private val _lastFetchQuery: MutableLiveData<FetchQuery?> = MutableLiveData(null)
    val lastFetchQuery: LiveData<FetchQuery?> = _lastFetchQuery

    val inputQueryText: MutableLiveData<String> = MutableLiveData()

    val isShowRepositoryCount by lazy {
        MediatorLiveData<Int>().apply {
            val observer = Observer<Any?> {
                val additionLoading = isAdditionLoading.value ?: false
                val statusSuccess = requestStatus.value is RequestStatus.OnSuccess
                val isShow = additionLoading || statusSuccess
                this.value = VisibilityUtil.booleanToVisibility(isShow)
            }
            observer.onChanged(null)
            addSource(isAdditionLoading, observer)
            addSource(requestStatus, observer)
        }
    }

    val isShowLoading by lazy {
        MediatorLiveData<Int>().apply {
            val observer = Observer<Any?> {
                val additionLoading = isAdditionLoading.value ?: false
                val statusLoading = requestStatus.value is RequestStatus.OnLoading
                val isShow = additionLoading || statusLoading
                this.value = VisibilityUtil.booleanToVisibility(isShow)
            }
            observer.onChanged(null)
            addSource(isAdditionLoading, observer)
            addSource(requestStatus, observer)
        }
    }

    val isShowError = requestStatus.map {
        val isShow = it is RequestStatus.OnError
        return@map VisibilityUtil.booleanToVisibility(isShow)
    }
    val errorText: LiveData<String> = requestStatus.map {
        if (it !is RequestStatus.OnError) ""
        else {
            val repositoryName = it.fetchQuery.query
            val errorTitle = CodeCheckApplication.instance.getString(R.string.error_title)
            val errorDescription = it.error.errorDescription

            CodeCheckApplication.instance.getString(
                R.string.error_display,
                errorTitle,
                repositoryName,
                errorDescription
            )
        }
    }

    val searchUser: MutableLiveData<Boolean> = MutableLiveData(true)
    val searchOrganization: MutableLiveData<Boolean> = MutableLiveData(true)
    val searchOwnerText: MutableLiveData<String> = MutableLiveData("")
    /**
     * 検索を実行します。
     * @param loadNext 次のページを読み込むかどうか
     */
    fun doSearch(loadNext: Boolean = false) {
        val page = if (loadNext && lastFetchQuery.value != null) {
            isAdditionLoading.value = true
            lastFetchQuery.value!!.loadPage + 1
        } else {
            1
        }

        val query = FetchQuery(
            query = inputQueryText.value ?: "",
            loadPage = page
        )

        fetchResults(query)
    }

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
            val isLoadNextPage = lastQuery != null && lastQuery.isNextFetch(newFetchQuery)
            // ステータスを更新
            requestStatus.value = GitHubApiRepository.getRepositories(newFetchQuery)

            // 失敗したなら早期リターン
            if (requestStatus.value !is RequestStatus.OnSuccess) {
                isAdditionLoading.value = false
                return@launch
            }

            val successResult = requestStatus.value as RequestStatus.OnSuccess

            /**
             * もし次のページを読み込むなら、既存のリストに追加する。
             * そうでないなら、読み込んだ値はそのまま代入する。
             */
            if (isLoadNextPage) {
                val oldList = _repositoryList.value ?: listOf()
                val newList = oldList.toMutableList().apply {
                    addAll(successResult.body.repositories)
                }
                _repositoryList.value = newList
            } else {
                _repositoryList.value = successResult.body.repositories
            }
            // 状態を変更
            _lastFetchQuery.value = newFetchQuery
            isAdditionLoading.value = false
            repositoryCountText.value =
                QuantityStringUtil.getString(
                    R.plurals.repository_counts,
                    successResult.body.totalCount
                )
        }
    }
}
