package jp.co.yumemi.android.code_check.model.status

/**
 * APIのフェッチ状況。前回と続けた内容を取得しているか確認するために使用する。
 */
data class FetchQuery(
    val query: String,
    val loadPage: Int
) {
    /**
     * 引数で渡された値が、現在の値の次の取得であるか
     */
    fun isNextFetch(newStatus: FetchQuery): Boolean {
        val sameQuery = this.query == newStatus.query
        val nextPage = (this.loadPage + 1) == newStatus.loadPage
        return sameQuery && nextPage
    }
}
