package jp.co.yumemi.android.code_check.network

import jp.co.yumemi.android.code_check.constant.HTTPResponseCode.VALIDATION_FAILED
import jp.co.yumemi.android.code_check.model.github.repositories.SearchGitRepoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApiService {
    @GET("search/repositories")
    suspend fun getRepositories(
        @Query("q") query: String,
        @Query("sort") sortQuery: String?,
        @Query("order") orderQuery: String?,
        /**ページ数を指定できる。10001件目以降を取得しようとすると[VALIDATION_FAILED]が帰ってくるため注意*/
        @Query("page") pageNumber: Int = 1,
        @Query("per_page") perPage: Int = 100
    ): Response<SearchGitRepoResponse>
}
