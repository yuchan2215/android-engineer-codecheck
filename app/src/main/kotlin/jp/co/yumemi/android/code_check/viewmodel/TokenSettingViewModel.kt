package jp.co.yumemi.android.code_check.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.co.yumemi.android.code_check.CodeCheckApplication
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.repository.TokenRepository
import kotlinx.coroutines.launch

class TokenSettingViewModel : ViewModel() {
    fun onOpenBottomSheet() {
        fetchTokenStatus()
    }
    val tokenInputText: MutableLiveData<String> = MutableLiveData()

    private val _tokenStatusText: MutableLiveData<String> = MutableLiveData()
    val tokenStatueText: LiveData<String> get() = _tokenStatusText

    private fun fetchTokenStatus() {
        viewModelScope.launch {
            val tokenIsExist = !TokenRepository.getToken().isNullOrEmpty()
            setTokenStatusText(tokenIsExist)
        }
    }

    private fun setTokenStatusText(configured: Boolean) {
        val statusResId =
            if (configured) {
                R.string.token_status_configured
            } else {
                R.string.token_status_not_configured
            }

        val statusText = CodeCheckApplication.instance.getString(statusResId)
        val formattedStatusText =
            CodeCheckApplication.instance.getString(R.string.token_status_text, statusText)
        _tokenStatusText.value = formattedStatusText
    }

    fun saveToken() {
        viewModelScope.launch {
            TokenRepository.setToken(tokenInputText.value)
            fetchTokenStatus()
        }
    }
}
