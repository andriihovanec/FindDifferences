package com.olbigames.finddifferencesgames.ui.home

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.olbigames.finddifferencesgames.App
import com.olbigames.finddifferencesgames.R
import com.olbigames.finddifferencesgames.domain.game.GameEntity
import com.olbigames.finddifferencesgames.extension.animateFade
import com.olbigames.finddifferencesgames.extension.invisible
import com.olbigames.finddifferencesgames.extension.visible
import com.olbigames.finddifferencesgames.presentation.viewmodel.GameListViewModel
import com.olbigames.finddifferencesgames.utilities.Constants.FOUNDED_COUNT
import kotlinx.android.synthetic.main.fragment_game_list.*
import javax.inject.Inject

class GameListFragment : Fragment(R.layout.fragment_game_list),
    GameListAdapter.OnItemClickListener {

    private lateinit var viewModel: GameListViewModel
    private var selectedLevel: Int = 0

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var adapter: GameListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[GameListViewModel::class.java]
        viewModel.initGamesList()
        networkNotify()
        adapterNotify()
        gameReseatedNotify()
        setupGamesList()
        handleClick()
    }

    private fun networkNotify() {
        viewModel.notifyNetworkConnection().observe(viewLifecycleOwner, Observer { isAvailable ->
            when (isAvailable) {
                true -> showProgress()
                else -> showMessage(R.string.check_connection)
            }
        })
    }

    private fun adapterNotify() {
        viewModel.notifyAdapter().observe(viewLifecycleOwner, Observer { isEmpty ->
            when (isEmpty) {
                false -> {
                    hideProgress()
                    adapter.notifyDataSetChanged()
                }
            }
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
        findNavController().navigate(R.id.gameFragment, null, animateFade())
    }

    private fun setupGamesList() {
        adapter = GameListAdapter(viewModel.getList(), this)
        games_recyclerview.layoutManager = GridLayoutManager(context, 2)
        games_recyclerview.setHasFixedSize(true)
        games_recyclerview.isNestedScrollingEnabled = false
        games_recyclerview.adapter = adapter
    }

    private fun showProgress() {
        olbiProgressBar.visible()
        progress.visible()
    }

    private fun hideProgress() {
        olbiProgressBar.invisible()
        progress.invisible()
    }

    private fun showMessage(messageResource: Int) {
        Toast.makeText(
            context,
            "${context?.resources?.getString(messageResource)}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun handleClick() {
        download_level_btn.setOnClickListener {
            findNavController().navigate(
                R.id.downloadNewLevelFragment,
                null,
                animateFade()
            )
        }
    }

    override fun onItemClicked(game: GameEntity) {
        selectedLevel = game.level
        if (game.foundedCount == FOUNDED_COUNT) {
            viewModel.resetFoundedCount(game)
        } else {
            navigateToGame()
        }
    }
}