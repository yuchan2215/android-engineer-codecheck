package jp.co.yumemi.android.code_check.view.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter

object TextViewAdapter {

    @BindingAdapter("isFitDrawable")
    @JvmStatic
    fun TextView.setFitDrawable(isFitDrawable: Boolean) {
        if (!isFitDrawable) return
        val drawables = this.compoundDrawablesRelative.map {
            it?.apply {
                setBounds(0, 0, textSize.toInt(), textSize.toInt())
            }
        }
        this.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3])
    }
}
