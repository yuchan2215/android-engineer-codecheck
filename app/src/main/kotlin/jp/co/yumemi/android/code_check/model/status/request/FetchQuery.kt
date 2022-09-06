package jp.co.yumemi.android.code_check.model.status.request

/**
 * APIのフェッチ状況。前回と続けた内容を取得しているか確認するために使用する。
 */
data class FetchQuery(
    val queries: List<SearchQuery>,
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
    fun isSameQuery(queries: List<SearchQuery>): Boolean {
        return this.queries == queries
    }
}
