package jp.co.yumemi.android.code_check.repository

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import jp.co.yumemi.android.code_check.CodeCheckApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * GitHubのトークンを管理するリポジトリ
 * 参考：https://qiita.com/masaki_shoji/items/6c512c7ebb30a13cda1d
 */
object TokenRepository {
    private const val PREF_NAME = "encrypted_pref"
    private const val TOKEN_PREF_KEY = "TOKEN_KEY"

    private fun getEncryptedSharedPreferences(): SharedPreferences {
        val applicationContext = CodeCheckApplication.instance.applicationContext

        val mainKey = MasterKey.Builder(applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            applicationContext,
            PREF_NAME,
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    suspend fun getToken(): String? {
        return withContext(Dispatchers.IO) {
            val prefs = getEncryptedSharedPreferences()
            return@withContext prefs.getString(TOKEN_PREF_KEY, null)
        }
    }

    suspend fun setToken(token: String?) {
        withContext(Dispatchers.IO) {
            val prefs = getEncryptedSharedPreferences()
            with(prefs.edit()) {
                putString(TOKEN_PREF_KEY, token)
                apply()
            }
        }
    }
}
