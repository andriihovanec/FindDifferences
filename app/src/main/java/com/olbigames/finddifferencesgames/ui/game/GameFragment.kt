package com.olbigames.finddifferencesgames.ui.game

import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
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
        handleTouch()
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

    private fun handleTouch() {
        image_bottom_imageview.setOnTouchListener { v, event ->
            val id = event.getPointerId(event.actionIndex)
            val action = event.actionMasked
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
                Toast.makeText(requireContext(), "$id X-${event.x} Y-${event.y}", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }
}
