package jp.co.yumemi.android.code_check.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

/**
 * [MediatorLiveData]にエンチャントを加えたクラス。
 * これを利用することによって[addSource]が冗長的にならない。
 * @param source ソースとしたい[LiveData]。これが更新されると[getData]を呼び出し、値を更新する。
 */
abstract class EnchantedMediatorLiveData<T>(
    vararg source: LiveData<*>,
) :
    MediatorLiveData<T>() {
    abstract fun getData(): T

    init {
        val observer = Observer<Any> {
            this.value = getData()
        }

        observer.onChanged(null)
        source.forEach {
            addSource(it, observer)
        }
    }
}
