package jp.co.yumemi.android.code_check.view.adapter

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter

object TextViewAdapter {

    @BindingAdapter("isFitDrawable", "isMatchDrawableColor")
    @JvmStatic
    fun TextView.setDrawableOptions(isFitDrawable: Boolean?, isMatchDrawableColor: Boolean?) {
        val drawables = this.compoundDrawablesRelative.map {
            it?.apply {
                if (isFitDrawable == true)
                    setBounds(0, 0, textSize.toInt(), textSize.toInt())

                if (isMatchDrawableColor == true)
                    setTint(currentTextColor)
            }
        }
        this.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3])
    }

    @BindingAdapter("isGoneWhenNull")
    @JvmStatic
    fun TextView.setVisibilityOption(isGoneWhenNull: Boolean) {
        if (!isGoneWhenNull) return
        if (this.text.isEmpty()) {
            this.visibility = View.GONE
        }
    }
}
