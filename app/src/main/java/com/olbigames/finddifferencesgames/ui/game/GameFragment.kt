package com.olbigames.finddifferencesgames.ui.game

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.olbigames.finddifferencesgames.R
import kotlinx.android.synthetic.main.fragment_game.*


class GameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        val imagePath= arguments!!.getString("level")
        image_top_imageview.setImageURI(Uri.parse(imagePath))
        image_bottom_imageview.setImageURI(Uri.parse(imagePath))
        all_game.setOnClickListener { findNavController().navigateUp() }
    }
}
