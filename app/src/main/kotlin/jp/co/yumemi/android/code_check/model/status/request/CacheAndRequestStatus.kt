package jp.co.yumemi.android.code_check.model.status.request

import jp.co.yumemi.android.code_check.model.status.RequestStatus

data class CacheAndRequestStatus<L, T>(
    val cache: RequestCache<L>,
    val status: RequestStatus<T>
) {
    companion object {
        /**
         * [RequestCache]も一緒に定義します。
         */
        fun <L, T> create(
            lastRequest: FetchQuery?,
            allData: List<L>,
            status: RequestStatus<T>
        ): CacheAndRequestStatus<L, T> {
            val cache = RequestCache(
                allData = allData,
                lastRequest = lastRequest
            )
            return CacheAndRequestStatus(cache, status)
        }
    }
}
