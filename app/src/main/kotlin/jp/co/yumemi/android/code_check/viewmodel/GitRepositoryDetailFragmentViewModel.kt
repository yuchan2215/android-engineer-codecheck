package jp.co.yumemi.android.code_check.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.model.github.repositories.GitRepository
import jp.co.yumemi.android.code_check.util.QuantityStringUtil

class GitRepositoryDetailFragmentViewModel(val repository: GitRepository) : ViewModel() {

    val ownerImageUrl = repository.owner?.avatarUrl ?: ""

    val name = repository.name
    val starsText = QuantityStringUtil.getString(
        R.plurals.github_stars,
        repository.stargazersCount
    )

    val watchersText = QuantityStringUtil.getString(
        R.plurals.github_watchers,
        repository.watchersCount
    )

    val issuesText = QuantityStringUtil.getString(
        R.plurals.github_open_issues,
        repository.openIssuesCount
    )

    val forksText = QuantityStringUtil.getString(
        R.plurals.github_forks,
        repository.forksCount
    )

    companion object {
        class Factory(private val repository: GitRepository) :
            ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(GitRepositoryDetailFragmentViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return GitRepositoryDetailFragmentViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
