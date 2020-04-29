package com.olbigames.finddifferencesgames.ui.home

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.olbigames.finddifferencesgames.App
import com.olbigames.finddifferencesgames.R
import com.olbigames.finddifferencesgames.cache.SharedPrefsManager
import com.olbigames.finddifferencesgames.domain.game.GameEntity
import com.olbigames.finddifferencesgames.extension.animateFade
import com.olbigames.finddifferencesgames.presentation.viewmodel.GameListViewModel
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
        observeNetworkNotification()
        observeAdapterNotification()
        observeGameReseated()
        setupGamesList()
        handleClick()
    }

    private fun observeNetworkNotification() {
        viewModel.notifyNetworkConnection().observe(viewLifecycleOwner, Observer { isAvailable ->
            when (isAvailable) {
                true -> showProgress()
                else -> showMessage(R.string.check_connection)
            }
        })
    }

    private fun observeAdapterNotification() {
        viewModel.notifyAdapter().observe(viewLifecycleOwner, Observer { isEmpty ->
            when (isEmpty) {
                false -> {
                    olbiProgressBar.visibility = View.GONE
                    progress.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun setupGamesList() {
        adapter = GameListAdapter(viewModel.getList(), this)
        games_recyclerview.layoutManager = GridLayoutManager(context, 2)
        games_recyclerview.setHasFixedSize(true)
        games_recyclerview.isNestedScrollingEnabled = false
        games_recyclerview.adapter = adapter
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

    private fun showProgress() {
        olbiProgressBar.visibility = View.VISIBLE
        progress.visibility = View.VISIBLE
    }

    private fun showMessage(messageResource: Int) {
        Toast.makeText(
            context,
            "${context?.resources?.getString(messageResource)}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun observeGameReseated() {
        viewModel.notifyGameReseated().observe(viewLifecycleOwner, Observer { gameReseated ->
            gameReseated.getContentIfNotHandle()?.let {
                if (it) {
                    navigateToGame()
                }
            }
        })
    }

    private fun navigateToGame() {
        val sharedPref = context?.getSharedPreferences(context?.packageName, Context.MODE_PRIVATE)
        sharedPref?.let { SharedPrefsManager(it).saveGameLevel(selectedLevel) }
        findNavController().navigate(R.id.gameFragment, null, animateFade())
    }

    override fun onItemClicked(game: GameEntity) {
        selectedLevel = game.level
        if (game.foundedCount == 10) {
            viewModel.resetFoundedCount(game)
        } else {
            navigateToGame()
        }
    }
}