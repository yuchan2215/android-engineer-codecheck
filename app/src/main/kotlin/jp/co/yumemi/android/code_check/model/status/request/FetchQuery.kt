package jp.co.yumemi.android.code_check.model.status.request

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
        return queries.map {
            it.getText
        }.filter {
            it.isNotEmpty()
        }.joinToString(" ")
    }

    /**
     * 引数で渡されたクエリが同じものかどうか
     */
    fun isSameQuery(
        queries: List<SearchQuery>,
        sortQuery: SortQuery,
        orderQuery: OrderQuery
    ): Boolean {
        return this.queries == queries &&
            this.sortQuery == sortQuery &&
            this.orderQuery == orderQuery
    }
}
