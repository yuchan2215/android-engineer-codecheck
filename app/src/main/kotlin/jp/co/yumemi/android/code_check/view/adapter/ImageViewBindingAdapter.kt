package jp.co.yumemi.android.code_check.view.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

object ImageViewBindingAdapter {
    @BindingAdapter("imageUrl")
    @JvmStatic
    fun ImageView.loadImage(imageUrl: String) {
        this.load(imageUrl)
    }
}
