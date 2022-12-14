/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
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
import jp.co.yumemi.android.code_check.model.status.request.query.OrderQuery
import jp.co.yumemi.android.code_check.model.status.request.query.SearchQuery
import jp.co.yumemi.android.code_check.model.status.request.query.SortQuery
import jp.co.yumemi.android.code_check.repository.GitHubApiRepository
import jp.co.yumemi.android.code_check.util.EnchantedMediatorLiveData
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

    // 追加読み込み中　又は　リクエストが成功している時に、リポジトリカウントを表示する。
    val isShowRepositoryCount by lazy {
        object : EnchantedMediatorLiveData<Int>(isAdditionLoading, requestStatus) {
            override fun getData(): Int {
                val additionLoading = isAdditionLoading.value ?: false
                val statusSuccess = requestStatus.value is RequestStatus.OnSuccess
                val isShow = additionLoading || statusSuccess
                return VisibilityUtil.booleanToVisibility(isShow)
            }
        }
    }

    val isShowLoading = requestStatus.map {
        val isShow = it is RequestStatus.OnLoading
        return@map VisibilityUtil.booleanToVisibility(isShow)
    }

    val isShowError = requestStatus.map {
        val isShow = it is RequestStatus.OnError
        return@map VisibilityUtil.booleanToVisibility(isShow)
    }
    val errorText: LiveData<String> = requestStatus.map {
        if (it !is RequestStatus.OnError) ""
        else {
            val repositoryName = requestCache.value?.lastRequest?.toStringQuery()
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

    val languageText: MutableLiveData<String> = MutableLiveData("")

    val sortType: MutableLiveData<Int> = MutableLiveData(R.id.sort_best_match_and_default)
    val sortOrder: MutableLiveData<Int> = MutableLiveData(R.id.order_desc)

    private fun getLanguageQuery(): List<SearchQuery> {
        val languageText = languageText.value ?: ""
        if (languageText.isEmpty()) return listOf()

        return listOf(
            SearchQuery.LanguageQuery(languageText)
        )
    }

    private fun getOwnerSearchQuery(): List<SearchQuery> {
        val ownerText = searchOwnerText.value ?: ""
        if (ownerText.isEmpty()) return listOf()

        val queries: MutableList<SearchQuery> = mutableListOf()

        if (searchUser.value == true)
            queries.add(
                SearchQuery.UserQuery(ownerText)
            )

        if (searchOrganization.value == true)
            queries.add(
                SearchQuery.OrganizationQuery(ownerText)
            )

        return queries
    }

    private fun getSortQuery(): SortQuery {
        return when (sortType.value) {
            R.id.sort_forks -> SortQuery.Forks
            R.id.sort_stars -> SortQuery.Stars
            R.id.sort_best_match_and_default -> SortQuery.Default
            R.id.sort_updated -> SortQuery.Updated
            // Deprecated R.id.sort_help_wanted_issues -> SortQuery.HelpWantedIssues
            else -> SortQuery.Default
        }
    }

    private fun getOrderQuery(): OrderQuery {
        return when (sortOrder.value) {
            R.id.order_desc -> OrderQuery.DESC
            R.id.order_asc -> OrderQuery.ASC
            else -> OrderQuery.DESC
        }
    }

    /**
     * 検索設定のクエリを取得する。
     */
    private fun getSearchSettingQueryObjects(): List<SearchQuery> {
        return listOf(
            // クエリを増やす時はここ！
            getOwnerSearchQuery(),
            getLanguageQuery()
        ).flatten()
    }

    /**
     * 全てのサーチクエリをまとめたリストを作成する。（サーチバーと各種設定）
     */
    private fun getAllSearchQuery(): List<SearchQuery> {
        val searchBarQuery = SearchQuery.SearchBarQuery(inputQueryText.value ?: "")
        return mutableListOf<SearchQuery>(
            searchBarQuery
        ).apply {
            addAll(getSearchSettingQueryObjects())
        }
    }

    /**
     * GitHubのAPIを叩き、リポジトリ一覧を取得して[requestStatus]の値を更新する。
     * 読み込むまでは[RequestStatus.OnLoading]にする。
     */
    fun fetchResults(isLoadNextPage: Boolean = false) {
        requestStatus.value = RequestStatus.OnLoading()
        viewModelScope.launch {
            TopActivity.lastSearchDate = Date()

            // キャッシュを含めたデータを取得
            val cacheAndRequestStatus = GitHubApiRepository.getRepositoriesWithCache(
                getAllSearchQuery(),
                getSortQuery(),
                getOrderQuery(),
                requestCache.value,
                isLoadNextPage
            )
            // ステータスを更新
            requestStatus.value = cacheAndRequestStatus.status
            _requestCache.value = cacheAndRequestStatus.cache

            // 状態を変更
            isAdditionLoading.value = false
        }
    }
}
