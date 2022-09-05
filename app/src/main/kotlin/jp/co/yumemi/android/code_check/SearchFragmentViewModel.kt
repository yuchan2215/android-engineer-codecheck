/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import androidx.lifecycle.ViewModel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import jp.co.yumemi.android.code_check.TopActivity.Companion.lastSearchDate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.*

/**
 * TwoFragment で使う
 */
class SearchFragmentViewModel : ViewModel() {

    // 検索結果
    fun searchResults(inputText: String): List<GitRepository> = runBlocking {
        val client = HttpClient(Android)

        return@runBlocking GlobalScope.async {
            val response: HttpResponse = client?.get("https://api.github.com/search/repositories") {
                header("Accept", "application/vnd.github.v3+json")
                parameter("q", inputText)
            }!!

            val responseString = response.receive<String>()

            val json = Json{ ignoreUnknownKeys = true }
            val searchResponse = json.decodeFromString<SearchGitRepoResponse>(responseString)

            lastSearchDate = Date()

            return@async searchResponse.repositories
        }.await()
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
