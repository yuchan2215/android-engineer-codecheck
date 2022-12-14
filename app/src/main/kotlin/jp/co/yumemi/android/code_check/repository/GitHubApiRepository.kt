package jp.co.yumemi.android.code_check.repository

import jp.co.yumemi.android.code_check.model.github.repositories.GitRepository
import jp.co.yumemi.android.code_check.model.github.repositories.SearchGitRepoResponse
import jp.co.yumemi.android.code_check.model.github.users.GitUser
import jp.co.yumemi.android.code_check.model.status.RequestStatus
import jp.co.yumemi.android.code_check.model.status.request.CacheAndRequestStatus
import jp.co.yumemi.android.code_check.model.status.request.RequestCache
import jp.co.yumemi.android.code_check.model.status.request.query.FetchQuery
import jp.co.yumemi.android.code_check.model.status.request.query.OrderQuery
import jp.co.yumemi.android.code_check.model.status.request.query.SearchQuery
import jp.co.yumemi.android.code_check.model.status.request.query.SortQuery
import jp.co.yumemi.android.code_check.network.GitHubApi
import jp.co.yumemi.android.code_check.network.GitHubApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GitHubApiRepository {
    private suspend fun getGitHubApiService(): GitHubApiService {
        return withContext(Dispatchers.IO) {
            val token = TokenRepository.getToken()
            return@withContext GitHubApi.getGitHubApiService(token)
        }
    }

    /**
     * GitHubのユーザーを[GitHubApiService]を介して叩きます。
     * 返り値の作成は[RequestStatus.Companion]にある関数に委譲しています。
     */
    suspend fun getUser(userName: String): RequestStatus<GitUser> {
        return try {
            val response = getGitHubApiService().getUser(
                userName
            )
            RequestStatus.createStatusFromRetrofit(response)
        } catch (t: Throwable) {
            RequestStatus.createErrorStatusFromThrowable(t)
        }
    }

    /**
     * GitHubのリポジトリを[GitHubApiService]を介して叩きます。
     * 返り値の作成は[RequestStatus.Companion]にある関数に委譲しています。
     */
    private suspend fun getRepositories(query: FetchQuery): RequestStatus<SearchGitRepoResponse> {
        return try {
            val response = getGitHubApiService().getRepositories(
                query.toStringQuery(),
                query.sortQuery.queryText,
                query.orderQuery.queryText,
                query.loadPage
            )
            RequestStatus.createStatusFromRetrofit(response)
        } catch (t: Throwable) {
            RequestStatus.createErrorStatusFromThrowable(t)
        }
    }

    /**
     * GitHubのリポジトリ一覧を[getRepositories]を叩いて取得し、そのデータを含む[RequestStatus]を作成します。
     * リクエスト内容や読み取ったデータを含む[RequestCache]として作成します。
     * @return [RequestStatus]と[RequestCache]を持った[CacheAndRequestStatus]
     * @param queryList 検索クエリ
     * @param cache 前回検索時のキャッシュです。次のページを読み込んで良いのか、ページとページを繋げたデータを作成するために利用します。
     * @param isRequestNextPage 次のページを読み込むことを希望しているかを示します。
     */
    suspend fun getRepositoriesWithCache(
        queryList: List<SearchQuery>,
        sortQuery: SortQuery,
        orderQuery: OrderQuery,
        cache: RequestCache<GitRepository>?,
        isRequestNextPage: Boolean = false
    ): CacheAndRequestStatus<GitRepository, SearchGitRepoResponse> {
        val lastRequestQuery = cache?.lastRequest

        val isLoadNextPage: Boolean // 次のページを読み込むか
        val loadPage: Int // 読み込むべきページ

        /**
         * 同じクエリであり、[isRequestNextPage]なら、
         * 次のページを読み込む[FetchQuery]を作成します。
         */
        val isSameQuery: Boolean =
            lastRequestQuery?.isSameQuery(queryList, sortQuery, orderQuery) ?: false
        if (lastRequestQuery != null && isSameQuery && isRequestNextPage) {
            isLoadNextPage = true
            loadPage = lastRequestQuery.loadPage + 1
        } else {
            isLoadNextPage = false
            loadPage = 1
        }
        val fetchQuery = FetchQuery(queryList, sortQuery, orderQuery, loadPage)

        // APIリクエスト実行
        val requestStatus = getRepositories(fetchQuery)

        /**リクエストに失敗したら、キャッシュは書き換えずに[RequestStatus]のみ書き換える。*/
        if (requestStatus !is RequestStatus.OnSuccess)
            return CacheAndRequestStatus.create(
                lastRequest = cache?.lastRequest,
                allData = cache?.allData ?: listOf(),
                status = requestStatus
            )

        /**
         * [isRequestNextPage]を考慮したデータ一覧のリストを作成します。
         */
        val newDates = if (isLoadNextPage) {
            val oldList = cache?.allData ?: listOf()
            oldList.toMutableList().apply {
                addAll(requestStatus.body.repositories)
            }
        } else {
            requestStatus.body.repositories
        }

        return CacheAndRequestStatus.create(
            lastRequest = fetchQuery,
            allData = newDates,
            status = requestStatus
        )
    }
}
