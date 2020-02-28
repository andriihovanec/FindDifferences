package com.olbigames.finddifferencesgames.ui.game

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.olbigames.finddifferencesgames.Constants.GAME_LEVEL_KEY
import com.olbigames.finddifferencesgames.R
import kotlinx.android.synthetic.main.fragment_game.*


class GameFragment : Fragment(R.layout.fragment_game) {

    private lateinit var viewModel: GameViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        getGameLevel()
        setupGame()
        handleClick()
    }

    private fun getGameLevel() {
        arguments.let { bundle ->
            bundle?.getString(GAME_LEVEL_KEY)?.let { level ->
                viewModel.startGame(level)
            }
        }
    }

    private fun setupGame() {
        viewModel.foundedGame.observe(this, Observer { game ->
            image_top_imageview.setImageURI(Uri.parse(game.pathToDifferentFile))
            image_bottom_imageview.setImageURI(Uri.parse(game.pathToMainFile))
        })
    }

    private fun handleClick() {
        all_game.setOnClickListener { findNavController().navigateUp() }
        next_game.setOnClickListener { viewModel.startNextGame() }
    }
}
