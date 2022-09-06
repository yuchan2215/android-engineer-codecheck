package jp.co.yumemi.android.code_check.model.status.request

/**
 * APIのフェッチ状況。前回と続けた内容を取得しているか確認するために使用する。
 */
data class FetchQuery(
    val query: String,
    val loadPage: Int
) {
    /**
     * 引数で渡されたクエリが同じものかどうか
     */
    fun isSameFetch(newQuery: String): Boolean {
        return query == newQuery
    }
}
