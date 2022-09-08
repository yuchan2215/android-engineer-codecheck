package jp.co.yumemi.android.code_check.model.status.request

enum class SortQuery(val queryText: String? = null) {
    Stars("stars"),
    Forks("forks"),

    @Deprecated("Incorrect response from GitHub.")
    HelpWantedIssues("help-wanted-issues"),
    Updated("updated"),
    Default;
}
