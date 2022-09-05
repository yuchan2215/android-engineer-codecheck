package jp.co.yumemi.android.code_check.view.adapter

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.progressindicator.BaseProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator

object LinearProgressIndicatorAdapter {
    @BindingAdapter("animateVisibility")
    @JvmStatic
    fun LinearProgressIndicator.animate(visibility: Int) {
        this.hideAnimationBehavior = BaseProgressIndicator.HIDE_OUTWARD
        this.showAnimationBehavior = BaseProgressIndicator.SHOW_INWARD
        when (visibility) {
            View.VISIBLE -> show()
            View.INVISIBLE -> hide()
            View.GONE -> hide()
        }
    }
}
