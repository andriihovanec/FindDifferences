package com.olbigames.finddifferencesgames.ui.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.AdRequest
import com.olbigames.finddifferencesgames.App
import com.olbigames.finddifferencesgames.R
import com.olbigames.finddifferencesgames.domain.game.GameEntity
import com.olbigames.finddifferencesgames.extension.gone
import com.olbigames.finddifferencesgames.extension.visible
import com.olbigames.finddifferencesgames.presentation.viewmodel.GameListViewModel
import com.olbigames.finddifferencesgames.utilities.Constants
import com.olbigames.finddifferencesgames.utilities.Constants.EXIT_DIALOG_TAG
import com.olbigames.finddifferencesgames.utilities.Constants.OLBI_GAMES
import com.olbigames.finddifferencesgames.utilities.Constants.OLBI_ON_TWITTER
import com.olbigames.finddifferencesgames.utilities.Globals
import kotlinx.android.synthetic.main.fragment_game_list.*
import javax.inject.Inject

class GameListFragment : Fragment(R.layout.fragment_game_list),
    GameListAdapter.OnItemClickListener {

    companion object {
        const val PORTRAIT_COLUMN_NUMBER = 2
        const val LANDSCAPE_COLUMN_NUMBER = 3
    }

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
        initADS()
        handleOrientationChange()
        muteStateNotify()
        gameReseatedNotify()
        handleClick()
        handleBackPressed()
    }

    private fun subscribeUi() {
        viewModel.gameSet.observe(viewLifecycleOwner, Observer {
            val list = it as ArrayList
            list.add(GameEntity(99999,"","","",0,0,true))
            adapter.submitList(list)
            hideProgress()
        })
    }

    private fun initADS() {
        Globals.adRequest = AdRequest.Builder().build()
    }

    private fun handleOrientationChange() {
        val orientation = this.resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            setupGamesList(PORTRAIT_COLUMN_NUMBER)
        else setupGamesList(LANDSCAPE_COLUMN_NUMBER)
    }

    private fun muteStateNotify() {
        viewModel.soundOn.observe(viewLifecycleOwner, Observer { isSoundOn ->
            iv_mute.isChecked = isSoundOn
        })
    }

    private fun gameReseatedNotify() {
        viewModel.gameReseated.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandle()?.let { gameReseated ->
                if (gameReseated) navigateToGame()
            }
        })
    }

    private fun navigateToGame() {
        viewModel.saveGameLevel(selectedLevel)
        findNavController().navigate(GameListFragmentDirections.actionHomeFragmentToGameFragment())
    }

    private fun setupGamesList(spanCount: Int) {
        adapter = GameListAdapter(this)
        games_recyclerview.layoutManager =
            GridLayoutManager(context, spanCount, GridLayoutManager.VERTICAL, false)
        games_recyclerview.isNestedScrollingEnabled = true
        games_recyclerview.adapter = adapter
    }

    private fun redirectToTwitter() {
        val tweetUrl = OLBI_ON_TWITTER + resources.getString(R.string.app_name) + OLBI_GAMES
        val uri = Uri.parse(tweetUrl)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun hideProgress() {
        nested_scroll_view.visible()
        progress.gone()
    }

    private fun handleClick() {
        iv_mute.setOnClickListener {
            viewModel.switchSoundEffect()
        }
        iv_market.setOnClickListener {
            goToMarket()
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
                    showExitDialog()
                }
            }
        )
    }

    private fun showExitDialog() {
        dialog = ExitAlertDialog()
        dialog.show(childFragmentManager, EXIT_DIALOG_TAG)
    }

    override fun onItemClicked(game: GameEntity) {
        selectedLevel = game.level
        navigateToGame()
    }

    override fun onReloadClicked(game: GameEntity) {
        selectedLevel = game.level
        viewModel.resetFoundedCount(game)
    }

    private fun goToMarket() =
        try {
            searchOlbiGamesOnMarket()
        } catch (e: ActivityNotFoundException) {
            searchOlbiGamesOnPlayStore()
        }

    private fun searchOlbiGamesOnMarket() =
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.OLBI_GAMES_SEARCH_MARKET_URL)))

    private fun searchOlbiGamesOnPlayStore() =
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(Constants.OLBI_GAMES_SEARCH_PLAY_STORE_URL)
            )
        )

    override fun onPlusClicked() {
        findNavController().navigate(GameListFragmentDirections.actionHomeFragmentToDownloadNewLevelFragment())
    }
}