package com.olbigames.finddifferencesgames.ui.game

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.olbigames.finddifferencesgames.Constants.GAME_LEVEL_KEY
import com.olbigames.finddifferencesgames.R
import com.olbigames.finddifferencesgames.extension.checkIsSupportsEs2
import kotlinx.android.synthetic.main.fragment_game.*


class GameFragment : Fragment(R.layout.fragment_game) {

    private lateinit var viewModel: GameViewModel
    private var gameLevel: Int = 0

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        getGameLevel()
        startGameRender()
        handleClick()
        setTouchListener()
    }

    private fun getGameLevel() {
        arguments.let { bundle ->
            bundle?.getString(GAME_LEVEL_KEY)?.let { level ->
                gameLevel = level.toInt()
            }
        }
    }

    private fun startGameRender() {
        if (activity!!.checkIsSupportsEs2()) {
            val metrics = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(metrics)
            val displayW = metrics.widthPixels
            val displayH = metrics.heightPixels
            val bannerHeight = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                50f,
                resources.displayMetrics
            ).toInt()

            viewModel.startGameRenderer(displayW, displayH, bannerHeight, gameLevel)
            viewModel.gameRender.observe(this, Observer {
                gameSurface.visibility = View.VISIBLE
                gameSurface.setRenderer(it)
            })
        }
    }

    private fun handleClick() {
        all_game.setOnClickListener { findNavController().navigateUp() }
        next_game.setOnClickListener { viewModel.startNextGame() }
    }

    private fun setTouchListener() {
        gameSurface.setOnTouchListener { v, event ->
            val pointerId = event.getPointerId(event.actionIndex)
            val action = event.actionMasked
            viewModel.handleTouch(event, action, pointerId)
            true
        }
    }
}
