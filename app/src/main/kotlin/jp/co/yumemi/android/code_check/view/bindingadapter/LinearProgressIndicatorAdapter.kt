package jp.co.yumemi.android.code_check.view.bindingadapter

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.progressindicator.BaseProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator

object LinearProgressIndicatorAdapter {
    @BindingAdapter("animateVisibility")
    @JvmStatic
    /**
     * https://tech.dely.jp/entry/2021/03/03/162802
     * なめらかに[LinearProgressIndicator]の表示/非表示を切り替えたい時に利用する。
     */
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
