/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

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
import jp.co.yumemi.android.code_check.TopActivity.Companion.lastSearchDate
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.Date

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
        }
    }
}

/**
 * https://api.github.com/search/repositories を叩いた時のレスポンス。
 * [Json]のignoreUnknownKeysをtrueにしているため、利用する値のみ定義している。
 * ドキュメント&スキーマ：https://docs.github.com/ja/rest/search#search-repositories
 */
@Serializable
data class SearchGitRepoResponse(
    @SerialName("items") val repositories: List<GitRepository>
)

/**
 * https://api.github.com/search/repositories を叩いた時の repositories -> owner に含まれる要素。
 * [Json]のignoreUnknownKeysをtrueにしているため、利用する値のみ定義している。
 * ドキュメント&スキーマ：https://docs.github.com/ja/rest/search#search-repositories
 */
@Serializable
data class GitOwner(
    @SerialName("avatar_url") val avatarUrl: String
)

/**
 * https://api.github.com/search/repositories を叩いた時の repositories に含まれる要素。
 * [Json]のignoreUnknownKeysをtrueにしているため、利用する値のみ定義している。
 * ドキュメント&スキーマ：https://docs.github.com/ja/rest/search#search-repositories
 */
@Serializable
data class GitRepository(
    @SerialName("name") val name: String,
    @SerialName("owner") val owner: GitOwner?,
    @SerialName("language") val language: String?,
    @SerialName("stargazers_count") val stargazersCount: Int,
    @SerialName("watchers_count") val watchersCount: Int,
    @SerialName("forks_count") val forksCount: Int,
    @SerialName("open_issues_count") val openIssuesCount: Int
) : java.io.Serializable
