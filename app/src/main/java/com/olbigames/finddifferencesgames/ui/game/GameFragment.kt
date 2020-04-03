package com.olbigames.finddifferencesgames.ui.game

import android.graphics.Color
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.olbigames.finddifferencesgames.App
import com.olbigames.finddifferencesgames.R
import com.olbigames.finddifferencesgames.extension.checkIsSupportsEs2
import com.olbigames.finddifferencesgames.game.DisplayDimensions
import com.olbigames.finddifferencesgames.utilities.Constants.GAME_LEVEL_KEY
import kotlinx.android.synthetic.main.fragment_game.*
import javax.inject.Inject


class GameFragment : Fragment(R.layout.fragment_game) {

    private lateinit var viewModel: GameViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var gameLevel: Int = 0
    private var surfaceStatus = 0
    private var surface: GLSurfaceView? = null
    private var adParams: ConstraintLayout.LayoutParams? = null
    private var displayDimensions: DisplayDimensions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]
        getGameLevel()
        handleClick()
        setTouchListener()
        surfaceClearedNotify()
    }

    private fun getGameLevel() {
        arguments.let { bundle ->
            bundle?.getString(GAME_LEVEL_KEY)?.let { level ->
                gameLevel = level.toInt()
                viewModel.setGameLevel(gameLevel)
                createGameRenderer()
            }
        }
    }

    private fun handleFoundedCountChange() {
        viewModel.foundedCount.observe(this, Observer { foundedCount ->
            game_counter.text = context?.resources?.getString(R.string._0_10, foundedCount)
        })
    }

    private fun createGameRenderer() {
        if (activity!!.checkIsSupportsEs2()) {
            val metrics = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(metrics)
            displayDimensions = DisplayDimensions(
                metrics.widthPixels,
                metrics.heightPixels,
                0
            )
            setSurface(displayDimensions)
            viewModel.setDisplayMetrics(displayDimensions!!)
            viewModel.startGame()
            startRenderer()
        }
    }

    private fun setSurface(displayDimensions: DisplayDimensions?) {
        surface = GLSurfaceView(context)
        // Request an OpenGL ES 2.0 compatible context.
        surface!!.setEGLContextClientVersion(2)
        surface!!.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        if (displayDimensions != null) {
            game_surface_container.setBackgroundColor(Color.argb(255, 0, 0, 0))
            var banner_height2: Int = displayDimensions.bannerHeight
            //if (displayH > displayW & showAd){
            if (displayDimensions.displayH > displayDimensions.displayW) {
                banner_height2 = displayDimensions.bannerHeight * 2
            }
            adParams = ConstraintLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                game_surface_container.height - banner_height2
            )
            game_surface_container.addView(surface, adParams)
            surface?.visibility = View.GONE
        }
    }

    private fun startRenderer() {
        viewModel.gameRendererCreated.observe(this, Observer { gameRenderer ->
            if (surfaceStatus != 1) {
                setSurface(displayDimensions)
                surface?.visibility = View.VISIBLE
                surface?.setRenderer(gameRenderer)
                surfaceStatus = 1
            }
            handleFoundedCountChange()
        })
    }

    private fun surfaceClearedNotify() {
        viewModel.surfaceCleared.observe(this, Observer { cleared ->
            if (cleared) clearSurface()
        })
    }

    private fun clearSurface() {
        if (surface != null) {
            game_surface_container.removeAllViewsInLayout()
            surface?.clearAnimation()
            surface?.destroyDrawingCache()
            surfaceStatus = 0
        }
    }

    private fun handleClick() {
        all_game.setOnClickListener { findNavController().navigateUp() }
        next_game.setOnClickListener { viewModel.startNextGame() }
    }

    private fun setTouchListener() {
        game_surface_container?.setOnTouchListener { v, event ->
            val pointerId = event.getPointerId(event.actionIndex)
            val action = event.actionMasked
            viewModel.handleTouch(event, action, pointerId)
            true
        }
    }
}
