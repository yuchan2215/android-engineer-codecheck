package jp.co.yumemi.android.code_check.model.status.request

import jp.co.yumemi.android.code_check.model.status.request.query.FetchQuery

data class RequestCache<A>(
    val allData: List<A>,
    val lastRequest: FetchQuery?
)
