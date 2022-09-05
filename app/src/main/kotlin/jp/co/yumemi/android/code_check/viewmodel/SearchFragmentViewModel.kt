/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import java.util.Date
import jp.co.yumemi.android.code_check.model.github.repositories.GitRepository
import jp.co.yumemi.android.code_check.model.github.repositories.SearchGitRepoResponse
import jp.co.yumemi.android.code_check.view.activity.TopActivity.Companion.lastSearchDate
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

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
            try { // TODO アーキテクチャ導入時、利用者にエラーを伝えるようにする
                val client = HttpClient(Android)

                val response: HttpResponse =
                    client.get("https://api.github.com/search/repositories") {
                        header("Accept", "application/vnd.github.v3+json")
                        parameter("q", inputText)
                    }!!

                val responseString = response.receive<String>()

                val json = Json { ignoreUnknownKeys = true }
                val searchResponse = json.decodeFromString<SearchGitRepoResponse>(responseString)

                lastSearchDate = Date()

                _searchResults.value = searchResponse.repositories
            } catch (_: Throwable) {
                _searchResults.value = null
            }
        }
    }
}
