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
import jp.co.yumemi.android.code_check.model.status.RequestStatus
import jp.co.yumemi.android.code_check.model.status.request.RequestCache
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

    private val _requestCache: MutableLiveData<RequestCache<GitRepository>?> = MutableLiveData(null)
    val requestCache: LiveData<RequestCache<GitRepository>?> = _requestCache

    private val requestStatus: MutableLiveData<RequestStatus<SearchGitRepoResponse>> =
        MutableLiveData(RequestStatus.Nothing())

    val isAdditionLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    /**
     * [requestStatus]を監視し、リポジトリのカウント文字列を生成する。
     * [requestStatus]が[RequestStatus.OnLoading]以外の時は文字を書き換えない。
     */
    val repositoryCountText by lazy {
        MediatorLiveData<String>().apply {
            this.value = ""
            addSource(requestStatus) {
                if (it !is RequestStatus.OnSuccess)
                    return@addSource

                this.value = QuantityStringUtil.getString(
                    R.plurals.repository_counts,
                    it.body.totalCount
                )
            }
        }
    }

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

    private fun getOwnerSearchQuery(): String {
        val ownerText = searchOwnerText.value ?: ""
        if (ownerText.isEmpty()) return ""

        val queries: MutableList<String> = mutableListOf()

        if (searchUser.value == true)
            queries.add("user:$ownerText")

        if (searchOrganization.value == true)
            queries.add("org:$ownerText")

        return queries.joinToString(" ")
    }

    /**
     * 検索設定のクエリを取得する。
     */
    private fun getSearchSettingQueryText(): String {
        val queryItems = listOf(
            // もしクエリアイテムが増える時はここに追加する。
            getOwnerSearchQuery()
        ).filter {
            it.isNotEmpty()
        }
        return queryItems.joinToString(" ")
    }

    /**
     * 検索を実行します。
     * @param loadNext 次のページを読み込むかどうか
     */
    fun doSearch(loadNext: Boolean = false) {

        val queryText = listOf(
            inputQueryText.value ?: "",
            getSearchSettingQueryText()
        ).filter {
            it.isNotEmpty()
        }.joinToString(" ")

        fetchResults(queryText, loadNext)
    }

    /**
     * GitHubのAPIを叩き、リポジトリ一覧を取得して[_requestStatus]の値を更新する。
     * 読み込むまでは[RequestStatus.OnLoading]にする。
     * @param newFetchQuery 検索クエリ
     */
    private fun fetchResults(query: String, isLoadNextPage: Boolean = false) {
        requestStatus.value = RequestStatus.OnLoading()
        viewModelScope.launch {
            TopActivity.lastSearchDate = Date()

            // キャッシュを含めたデータを取得
            val cacheAndRequestStatus = GitHubApiRepository.getRepositoriesWithCache(
                query,
                requestCache.value,
                isLoadNextPage
            )
            val cache = cacheAndRequestStatus.cache
            val status = cacheAndRequestStatus.status

            // ステータスを更新
            requestStatus.value = status

            // 状態を変更
            _requestCache.value = cache
            isAdditionLoading.value = false
        }
    }
}
