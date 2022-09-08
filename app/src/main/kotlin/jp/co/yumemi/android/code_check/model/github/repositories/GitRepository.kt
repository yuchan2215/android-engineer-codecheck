package jp.co.yumemi.android.code_check.model.github.repositories

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * https://api.github.com/search/repositories を叩いた時の repositories に含まれる要素。
 * [Json]のignoreUnknownKeysをtrueにしているため、利用する値のみ定義している。
 * ドキュメント&スキーマ：https://docs.github.com/ja/rest/search#search-repositories
 */
@Serializable // Kotlinx SerializationのSerializable
data class GitRepository(
    @SerialName("name") val name: String,
    @SerialName("owner") val owner: GitOwner?,
    @SerialName("language") val language: String?,
    @SerialName("stargazers_count") val stargazersCount: Int,
    @SerialName("watchers_count") val watchersCount: Int,
    @SerialName("forks_count") val forksCount: Int,
    @SerialName("open_issues_count") val openIssuesCount: Int,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("description") val description: String? = null
) : java.io.Serializable // ナビゲーションの引数のSerializable
