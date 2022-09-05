package jp.co.yumemi.android.code_check.network

import jp.co.yumemi.android.code_check.model.github.repositories.SearchGitRepoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApiService {
    @GET("search/repositories")
    suspend fun getRepositories(
        @Query("q") query: String
    ): Response<SearchGitRepoResponse>
}
