package jp.co.yumemi.android.code_check.model.status.request.query

/**
 * APIのフェッチ状況。前回と続けた内容を取得しているか確認するために使用する。
 */
data class FetchQuery(
    val queries: List<SearchQuery>,
    val sortQuery: SortQuery,
    val orderQuery: OrderQuery,
    val loadPage: Int
) {
    fun toStringQuery(): String {
        return queries.toStringQuery()
    }

    /**
     * 引数で渡されたクエリが同じものかどうか
     */
    fun isSameQuery(
        queries: List<SearchQuery>,
        sortQuery: SortQuery,
        orderQuery: OrderQuery
    ): Boolean {
        return this.queries.toStringQuery() == queries.toStringQuery() &&
            this.sortQuery == sortQuery &&
            this.orderQuery == orderQuery
    }

    private fun List<SearchQuery>.toStringQuery(): String {
        return this.map {
            it.getText
        }.filter {
            it.isNotEmpty()
        }.joinToString(" ")
    }
}
