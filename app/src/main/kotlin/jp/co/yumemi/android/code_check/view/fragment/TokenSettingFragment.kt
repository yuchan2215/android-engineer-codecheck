package jp.co.yumemi.android.code_check.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.databinding.TokenSettingFragmentBinding
import jp.co.yumemi.android.code_check.viewmodel.TokenSettingViewModel

class TokenSettingFragment : Fragment(R.layout.token_setting_fragment) {

    val viewModel: TokenSettingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = TokenSettingFragmentBinding.bind(view)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.openTokenSettingPage.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/settings/tokens"))
            startActivity(intent)
        }

        viewModel.onOpenBottomSheet()
    }
}
