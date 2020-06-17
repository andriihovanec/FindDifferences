package com.olbigames.finddifferencesgames.ui.game

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.olbigames.finddifferencesgames.App
import com.olbigames.finddifferencesgames.R
import com.olbigames.finddifferencesgames.extension.showMessage
import com.olbigames.finddifferencesgames.extension.visible
import com.olbigames.finddifferencesgames.presentation.viewmodel.DownloadLevelViewModel
import com.olbigames.finddifferencesgames.utilities.Constants.REFERENCE_POINT_20
import com.olbigames.finddifferencesgames.utilities.Constants.REFERENCE_POINT_40
import kotlinx.android.synthetic.main.fragment_more_games.*
import javax.inject.Inject

class DownloadLevelFragment : Fragment(R.layout.fragment_more_games) {

    private lateinit var viewModel: DownloadLevelViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[DownloadLevelViewModel::class.java]
        notifyLevelDownloaded()
        handleClick()
    }

    private fun notifyLevelDownloaded() {
        viewModel.notifyLevelDownloaded.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandle()?.let { levelDownloaded ->
                if (levelDownloaded) {
                    findNavController().navigateUp()
                } else {
                    showMessage(R.string.all_level_loaded)
                    findNavController().navigateUp()
                }
            }
        })
    }

    private fun handleClick() {
        next20_btn.setOnClickListener {
            setupViewWhenDownloadBeginning()
            viewModel.downloadGamesSet(REFERENCE_POINT_20)
        }
        next40_btn.setOnClickListener {
            setupViewWhenDownloadBeginning()
            viewModel.downloadGamesSet(REFERENCE_POINT_40)
        }
        close_iv.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setupViewWhenDownloadBeginning() {
        close_iv.isClickable = false
        olbiProgressBar.visible()
    }
}