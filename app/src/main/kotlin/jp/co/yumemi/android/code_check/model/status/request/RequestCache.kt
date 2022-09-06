package jp.co.yumemi.android.code_check.model.status.request

data class RequestCache<A>(
    val allData: List<A>,
    val lastRequest: FetchQuery?
)
