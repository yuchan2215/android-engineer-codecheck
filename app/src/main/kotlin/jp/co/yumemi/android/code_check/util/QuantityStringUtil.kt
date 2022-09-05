package jp.co.yumemi.android.code_check.util

import androidx.annotation.PluralsRes
import jp.co.yumemi.android.code_check.CodeCheckApplication

object QuantityStringUtil {
    fun getString(
        @PluralsRes resId: Int,
        quantity: Int
    ): String {
        return CodeCheckApplication.instance.resources.getQuantityString(resId, quantity, quantity)
    }
}
