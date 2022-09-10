package jp.co.yumemi.android.code_check.view.bindingadapter

import android.view.View
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter

object TextViewAdapter {

    /**
     * [TextView]に設定されているDrawableの大きさと色を、[TextView]の高さに合わせるためのアダプター
     */
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

    /**
     * もしTextがNullの時に、[TextView]を[View.GONE]にする。
     */
    @BindingAdapter("isGoneWhenNull")
    @JvmStatic
    fun TextView.setVisibilityOption(isGoneWhenNull: Boolean) {
        if (!isGoneWhenNull) return
        // テキストが変更された時の処理
        fun onTextChanged(text: String) {
            if (text.isEmpty()) {
                this.visibility = View.GONE
            } else {
                this.visibility = View.VISIBLE
            }
        }
        onTextChanged(this.text.toString())
        this.addTextChangedListener {
            onTextChanged(it.toString())
        }
    }
}
