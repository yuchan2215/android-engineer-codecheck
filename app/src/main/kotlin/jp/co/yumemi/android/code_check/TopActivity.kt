/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import androidx.appcompat.app.AppCompatActivity
import java.util.*

class TopActivity : AppCompatActivity(R.layout.activity_top) {

    companion object {
        /**GitHubリポジトリを最後に検索した日時*/
        var lastSearchDate: Date? = null
    }
}
