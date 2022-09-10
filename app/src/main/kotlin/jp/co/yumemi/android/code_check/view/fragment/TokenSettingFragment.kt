package jp.co.yumemi.android.code_check.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.databinding.TokenSettingFragmentBinding
import jp.co.yumemi.android.code_check.viewmodel.TokenSettingViewModel

class TokenSettingFragment : Fragment(R.layout.token_setting_fragment) {

    val viewModel: TokenSettingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        TokenSettingFragmentBinding.bind(view).apply {
            this.viewModel = this@TokenSettingFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
            openTokenSettingPage.setOnClickListener {
                val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/settings/tokens"))
                startActivity(intent)
            }
            toolBar.setupWithNavController(navController, appBarConfiguration)
        }

        viewModel.fetchTokenStatus()
    }
}
