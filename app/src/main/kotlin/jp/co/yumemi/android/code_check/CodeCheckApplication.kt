package jp.co.yumemi.android.code_check

import android.app.Application

class CodeCheckApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: CodeCheckApplication
    }
}
