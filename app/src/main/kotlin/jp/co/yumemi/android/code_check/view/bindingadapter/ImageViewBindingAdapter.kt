package jp.co.yumemi.android.code_check.view.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import jp.co.yumemi.android.code_check.R

object ImageViewBindingAdapter {
    @BindingAdapter("imageUrl")
    @JvmStatic
    /**
     * 画像をxmlから読み込む際に利用する。
     */
    fun ImageView.loadImage(imageUrl: String) {
        this.load(imageUrl) {
            placeholder(R.drawable.loading)
            error(R.drawable.git_issue)
        }
    }
}
