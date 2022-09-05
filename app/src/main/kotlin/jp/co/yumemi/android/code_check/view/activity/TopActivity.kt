/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.view.activity

import androidx.appcompat.app.AppCompatActivity
import java.util.*
import jp.co.yumemi.android.code_check.R

class TopActivity : AppCompatActivity(R.layout.activity_top) {

    companion object {
        /**GitHubリポジトリを最後に検索した日時*/
        var lastSearchDate: Date? = null
    }
}
