package jp.co.yumemi.android.code_check.repository

import jp.co.yumemi.android.code_check.model.github.repositories.SearchGitRepoResponse
import jp.co.yumemi.android.code_check.model.status.FetchQuery
import jp.co.yumemi.android.code_check.model.status.RequestStatus
import jp.co.yumemi.android.code_check.network.GitHubApi
import jp.co.yumemi.android.code_check.network.GitHubApiService

object GitHubApiRepository {
    /**
     * GitHubのリポジトリを[GitHubApiService]を介して叩きます。
     * 返り値の作成は[RequestStatus.Companion]にある関数に委譲しています。
     */
    suspend fun getRepositories(query: FetchQuery): RequestStatus<SearchGitRepoResponse> {
        return try {
            val response = GitHubApi.gitHubApiService.getRepositories(query.query, query.loadPage)
            RequestStatus.createStatusFromRetrofit(response)
        } catch (t: Throwable) {
            RequestStatus.createErrorStatusFromThrowable(t)
        }
    }
}
