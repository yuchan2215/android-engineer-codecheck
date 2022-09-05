package jp.co.yumemi.android.code_check.util

import android.view.View

object VisibilityUtil {
    /**
     * BooleanをVisibilityな値にする。
     * @param defaultInvisible 非表示の時の値
     */
    fun booleanToVisibility(boolean: Boolean, defaultInvisible: Int = View.GONE): Int {
        return if (boolean) View.VISIBLE else defaultInvisible
    }
}
