package jp.co.yumemi.android.code_check.model.github.repositories

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * https://api.github.com/search/repositories を叩いた時のレスポンス。
 * [Json]のignoreUnknownKeysをtrueにしているため、利用する値のみ定義している。
 * ドキュメント&スキーマ：https://docs.github.com/ja/rest/search#search-repositories
 */
@Serializable
data class SearchGitRepoResponse(
    @SerialName("items") val repositories: List<GitRepository>
)
