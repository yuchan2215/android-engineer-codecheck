package jp.co.yumemi.android.code_check.model.status.request

sealed interface SearchQuery {
    val getText: String

/**[filterType]:[filterQueryText]の形のクエリ*/
    sealed interface FilterQuery : SearchQuery {
        val filterType: String
        val filterQueryText: String
        override val getText: String get() = "$filterType:$filterQueryText"
    }

    class SearchBarQuery(queryText: String) : SearchQuery {
        override val getText: String = queryText
    }

    class OrganizationQuery(organizationName: String) : FilterQuery {
        override val filterQueryText = organizationName
        override val filterType = "org"
    }

    class UserQuery(organizationName: String) : FilterQuery {
        override val filterQueryText = organizationName
        override val filterType = "user"
    }

    class LanguageQuery(languageName: String) : FilterQuery {
        override val filterQueryText: String = languageName
        override val filterType: String = "language"
    }
}
