package com.olbigames.finddifferencesgames.ui.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.olbigames.finddifferencesgames.App
import com.olbigames.finddifferencesgames.R
import com.olbigames.finddifferencesgames.domain.game.GameEntity
import com.olbigames.finddifferencesgames.extension.invisible
import com.olbigames.finddifferencesgames.extension.visible
import com.olbigames.finddifferencesgames.presentation.viewmodel.GameListViewModel
import com.olbigames.finddifferencesgames.utilities.Constants.APP_ON_MARKET
import com.olbigames.finddifferencesgames.utilities.Constants.EXIT_DIALOG_TAG
import com.olbigames.finddifferencesgames.utilities.BannerGenerator
import com.olbigames.finddifferencesgames.utilities.ConnectionUtil
import com.olbigames.finddifferencesgames.utilities.Constants.FOUNDED_COUNT
import com.olbigames.finddifferencesgames.utilities.Constants.MARKET_DETAILS_ID
import com.olbigames.finddifferencesgames.utilities.Constants.OLBI_GAMES
import com.olbigames.finddifferencesgames.utilities.Constants.OLBI_ON_TWITTER
import kotlinx.android.synthetic.main.fragment_game_list.*
import javax.inject.Inject

class GameListFragment : Fragment(R.layout.fragment_game_list),
    GameListAdapter.OnItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: GameListViewModel

    private lateinit var adapter: GameListAdapter
    private lateinit var dialog: ExitAlertDialog
    private var selectedLevel: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[GameListViewModel::class.java]
        viewModel.initGamesList()
        subscribeUi()
        muteStateNotify()
        gameReseatedNotify()
        setupGamesList()
        handleClick()
        handleBackPressed()
    }

    private fun muteStateNotify() {
        viewModel.soundOn.observe(viewLifecycleOwner, Observer { isSoundOn ->
            iv_mute.isChecked = isSoundOn
        })
    }

    private fun subscribeUi() {
        viewModel.gameSet().observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            hideProgress()
        })
    }

    private fun gameReseatedNotify() {
        viewModel.notifyGameReseated().observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandle()?.let { gameReseated ->
                if (gameReseated) {
                    navigateToGame()
                }
            }
        })
    }

    private fun navigateToGame() {
        viewModel.saveGameLevel(selectedLevel)
        findNavController().navigate(GameListFragmentDirections.actionHomeFragmentToGameFragment())
    }

    private fun setupGamesList() {
        adapter = GameListAdapter(this)
        games_recyclerview.layoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        games_recyclerview.isNestedScrollingEnabled = true
        games_recyclerview.adapter = adapter
    }

    private fun redirectToTwitter() {
        val tweetUrl = OLBI_ON_TWITTER + resources.getString(R.string.app_name) + OLBI_GAMES
        val uri = Uri.parse(tweetUrl)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun rateMyApp() {
        val uri = Uri.parse(MARKET_DETAILS_ID + requireActivity().packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(APP_ON_MARKET + requireActivity().packageName)
                )
            )
        }
    }

    private fun hideProgress() {
        nested_scroll_view.visible()
        progress.invisible()
    }

    private fun handleClick() {
        download_level_btn.setOnClickListener {
            findNavController().navigate(GameListFragmentDirections.actionHomeFragmentToDownloadNewLevelFragment())
        }
        iv_mute.setOnClickListener {
            viewModel.switchSoundEffect()
        }
        iv_market.setOnClickListener {
            rateMyApp()
        }
        iv_twitter.setOnClickListener {
            redirectToTwitter()
        }
    }

    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    dialog = ExitAlertDialog()
                    dialog.show(childFragmentManager, EXIT_DIALOG_TAG)
                }
            }
        )
    }

    override fun onItemClicked(game: GameEntity) {
        selectedLevel = game.level
        if (game.foundedCount == FOUNDED_COUNT) {
            viewModel.resetFoundedCount(game)
        } else {
            navigateToGame()
        }
    }

    private fun openMarket() {
        val uri = Uri.parse("market://search?q=pub:Olbi Games")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/search?q=pub:Olbi Games")
                )
            )
        }
    }
}