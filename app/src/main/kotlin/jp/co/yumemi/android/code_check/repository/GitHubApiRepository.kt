package jp.co.yumemi.android.code_check.repository

import jp.co.yumemi.android.code_check.model.github.repositories.SearchGitRepoResponse
import jp.co.yumemi.android.code_check.model.status.RequestStatus
import jp.co.yumemi.android.code_check.network.GitHubApi
import jp.co.yumemi.android.code_check.network.GitHubApiService

object GitHubApiRepository {
    /**GitHubのリポジトリAPI一覧を返す[GitHubApiService]を叩く。*/
    suspend fun getRepositories(query: String): RequestStatus<SearchGitRepoResponse> {
        return try {
            val response = GitHubApi.gitHubApiService.getRepositories(query)
            RequestStatus.createStatusFromRetrofit(response)
        } catch (t: Throwable) {
            RequestStatus.createErrorStatusFromThrowable(t)
        }
    }
}
