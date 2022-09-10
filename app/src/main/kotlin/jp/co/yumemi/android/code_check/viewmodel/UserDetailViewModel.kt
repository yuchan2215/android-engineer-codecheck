package jp.co.yumemi.android.code_check.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import jp.co.yumemi.android.code_check.CodeCheckApplication
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.model.github.users.GitUser
import jp.co.yumemi.android.code_check.model.status.RequestStatus
import jp.co.yumemi.android.code_check.repository.GitHubApiRepository
import jp.co.yumemi.android.code_check.util.QuantityStringUtil
import jp.co.yumemi.android.code_check.util.VisibilityUtil
import kotlinx.coroutines.launch

class UserDetailViewModel(private val inputName: String) : ViewModel() {

    private val _requestStatus: MutableLiveData<RequestStatus<GitUser>?> = MutableLiveData(null)

    val loadedUser = _requestStatus.map {
        when (val status = it) {
            is RequestStatus.OnSuccess -> status.body
            else -> null
        }
    }

    val isShowLoading = _requestStatus.map {
        val isVisible = it is RequestStatus.OnLoading
        VisibilityUtil.booleanToVisibility(isVisible)
    }

    val isShowError = _requestStatus.map {
        val isVisible = it is RequestStatus.OnError
        VisibilityUtil.booleanToVisibility(isVisible)
    }

    val errorText = _requestStatus.map {
        when (val status = it) {
            is RequestStatus.OnError -> {
                status.error.errorDescription
            }
            else -> null
        }
    }

    val isShowFollowObject = loadedUser.map {
        val isVisible = it?.type == GitUser.Companion.UserType.USER
        VisibilityUtil.booleanToVisibility(isVisible)
    }

    val followerText = loadedUser.map {
        if (it == null) return@map null
        QuantityStringUtil.getString(R.plurals.followers, it.followers)
    }

    val followText = loadedUser.map {
        if (it == null) return@map null
        QuantityStringUtil.getString(R.plurals.follows, it.following)
    }

    fun getString(@StringRes resId: Int?): String? {
        if (resId == null || resId <= 0) return null
        return CodeCheckApplication.instance.getString(resId)
    }

    fun fetchStatus() {
        _requestStatus.value = RequestStatus.OnLoading()
        viewModelScope.launch {
            _requestStatus.value = GitHubApiRepository.getUser(inputName)
        }
    }

    companion object {
        class Factory(private val inputName: String) :
            ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(UserDetailViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return UserDetailViewModel(inputName) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
