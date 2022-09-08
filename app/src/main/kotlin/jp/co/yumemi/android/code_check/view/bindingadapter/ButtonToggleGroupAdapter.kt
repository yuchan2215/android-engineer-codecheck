package jp.co.yumemi.android.code_check.view.bindingadapter

import androidx.annotation.IdRes
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.ChipGroup

/**
 * [ChipGroup]をラジオボタンのように扱えるようにするアダプター。
 */
object ButtonToggleGroupAdapter {

    @BindingAdapter("onlySelectedButton")
    @JvmStatic
    fun MaterialButtonToggleGroup.setSelected(@IdRes selectedChip: Int) {
        check(selectedChip)
        isSingleSelection = true
    }

    @InverseBindingAdapter(attribute = "onlySelectedButton")
    @JvmStatic
    fun MaterialButtonToggleGroup.getSelected(): Int {
        return this.checkedButtonIds[0]
    }

    @BindingAdapter("onlySelectedButtonAttrChanged")
    @JvmStatic
    fun MaterialButtonToggleGroup.bindListeners(valueAttrChanged: InverseBindingListener?) {
        this.addOnButtonCheckedListener { group, checkId, _ ->
            if (group.checkedButtonIds.isEmpty()) {
                check(checkId)
            }
            valueAttrChanged?.onChange()
        }
    }
}
