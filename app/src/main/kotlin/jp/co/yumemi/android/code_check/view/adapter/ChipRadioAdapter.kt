package jp.co.yumemi.android.code_check.view.adapter

import androidx.annotation.IdRes
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.chip.ChipGroup
import jp.co.yumemi.android.code_check.R

/**
 * [ChipGroup]をラジオボタンのように扱えるようにするアダプター。
 */
object ChipRadioAdapter {
    private const val DEFAULT_ID_TAG = R.id.default_selection
    private const val IN_PROGRESS_TAG = R.id.in_progress

    @BindingAdapter("onlySelectedChip", "defaultId", requireAll = true)
    @JvmStatic
    fun ChipGroup.setSelectedChip(@IdRes selectedChip: Int, @IdRes defaultId: Int) {
        setTag(DEFAULT_ID_TAG, defaultId)
        isSingleSelection = true
        // すでに含まれるならチェックしない
        if (!this.checkedChipIds.contains(selectedChip)) {
            fixedCheck(selectedChip)
        }
    }

    @InverseBindingAdapter(attribute = "onlySelectedChip")
    @JvmStatic
    fun ChipGroup.getSelectedChip(): Int? {
        return checkedChipIds.getOrNull(0)
    }

    @BindingAdapter("onlySelectedChipAttrChanged")
    @JvmStatic
    fun ChipGroup.bindListeners(valueAttrChanged: InverseBindingListener?) {
        setOnCheckedStateChangeListener { group, _ ->
            // １回目はチェックを外す処理なのでなにもしない
            if ((getTag(IN_PROGRESS_TAG) ?: false) as Boolean) {
                setTag(IN_PROGRESS_TAG, false)
                return@setOnCheckedStateChangeListener
            }
            // チェックが外されたなら、デフォルト値にチェックを入れる
            if (group.checkedChipIds.isEmpty()) {
                val defaultId = (getTag(DEFAULT_ID_TAG) ?: -1) as Int
                fixedCheck(defaultId)
            }
            valueAttrChanged?.onChange()
        }
    }

    /**
     * [IN_PROGRESS_TAG]のタグをつけてチェックをつける。
     */
    private fun ChipGroup.fixedCheck(@IdRes resId: Int) {
        setTag(IN_PROGRESS_TAG, true)
        check(resId)
    }
}
