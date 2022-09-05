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
import jp.co.yumemi.android.code_check.model.github.repositories.SearchGitRepoResponse
import jp.co.yumemi.android.code_check.model.status.RequestStatus
import jp.co.yumemi.android.code_check.repository.GitHubApiRepository
import jp.co.yumemi.android.code_check.view.activity.TopActivity
import jp.co.yumemi.android.code_check.view.fragment.SearchFragment
import kotlinx.coroutines.launch

/**
 * [SearchFragment]で利用されるViewModel。
 */
class SearchFragmentViewModel : ViewModel() {

    private val _requestStatus: MutableLiveData<RequestStatus<SearchGitRepoResponse>> =
        MutableLiveData(RequestStatus.Nothing())

    val requestStatus: LiveData<RequestStatus<SearchGitRepoResponse>> = _requestStatus

    val errorText: LiveData<String> = requestStatus.map {
        if (it !is RequestStatus.OnError) ""
        else it.error.errorDescription
    }

    /**
     * GitHubのAPIを叩き、リポジトリ一覧を取得して[_requestStatus]の値を更新する。
     * 読み込むまでは[RequestStatus.OnLoading]にする。
     * @param inputText 検索したいテキスト。
     */
    fun fetchResults(inputText: String) {
        _requestStatus.value = RequestStatus.OnLoading()
        viewModelScope.launch {
            TopActivity.lastSearchDate = Date()
            _requestStatus.value = GitHubApiRepository.getRepositories(inputText)
        }
    }
}
