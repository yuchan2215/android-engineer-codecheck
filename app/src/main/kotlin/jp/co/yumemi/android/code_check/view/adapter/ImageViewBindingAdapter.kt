package jp.co.yumemi.android.code_check.view.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

object ImageViewBindingAdapter {
    @BindingAdapter("imageUrl")
    @JvmStatic
    /**
     * 画像をxmlから読み込む際に利用する。
     */
    fun ImageView.loadImage(imageUrl: String) {
        this.load(imageUrl)
    }
}
