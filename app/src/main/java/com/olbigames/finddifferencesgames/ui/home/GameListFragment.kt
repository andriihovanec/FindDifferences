package com.olbigames.finddifferencesgames.ui.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.olbigames.finddifferencesgames.utilities.BannerGenerator
import com.olbigames.finddifferencesgames.utilities.ConnectionUtil
import com.olbigames.finddifferencesgames.utilities.Constants.FOUNDED_COUNT
import com.olbigames.finddifferencesgames.utilities.animateFade
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
        //adapterNotify()
        subscribeUi()
        gameReseatedNotify()
        setupGamesList()
        handleClick()
        initADMOB()
    }

    private fun initADMOB() {
        if (ConnectionUtil.isNetworkAvailable(context!!)) {
            val adRequest =
                AdRequest.Builder().build()
            adView.loadAd(adRequest)
        } else {
            Glide.with(context!!)
                .load(BannerGenerator.getBanner(resources))
                .into(ivListBanner);
            ivListBanner.setOnClickListener {
                openMarket()
            }
        }
    }

    private fun subscribeUi() {
        viewModel.gameSet().observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            hideProgress()
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
        findNavController().navigate(
            R.id.gameFragment, null,
            animateFade()
        )
    }

    private fun setupGamesList() {
        adapter = GameListAdapter(this)
        games_recyclerview.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL,false)
        games_recyclerview.isNestedScrollingEnabled = true
        games_recyclerview.adapter = adapter
    }

    private fun hideProgress() {
        nested_scroll_view.visible()
        progress.invisible()
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