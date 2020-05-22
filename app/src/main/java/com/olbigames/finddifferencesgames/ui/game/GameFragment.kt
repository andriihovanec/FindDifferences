package com.olbigames.finddifferencesgames.ui.game

import android.graphics.Color
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.olbigames.finddifferencesgames.App
import com.olbigames.finddifferencesgames.R
import com.olbigames.finddifferencesgames.extension.checkIsSupportsEs2
import com.olbigames.finddifferencesgames.presentation.viewmodel.GameViewModel
import com.olbigames.finddifferencesgames.renderer.DisplayDimensions
import com.olbigames.finddifferencesgames.utilities.animateAndPopFromStack
import com.olbigames.finddifferencesgames.utilities.animateFade
import kotlinx.android.synthetic.main.fragment_game.*
import javax.inject.Inject

class GameFragment : Fragment(R.layout.fragment_game), GameCompleteDialog.NoticeDialogListener,
    RewardedVideoAdListener {

    private lateinit var viewModel: GameViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var surfaceStatus: SurfaceStatus = SurfaceStatus.Cleared
    private var surface: GLSurfaceView? = null
    private var displayDimensions: DisplayDimensions? = null
    private lateinit var mRewardedVideoAd: RewardedVideoAd
    private lateinit var dialog: GameCompleteDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
        initRewardVideo()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]
        createGame()
        handleClick()
        setTouchListener()
        needMoreLevelNotify()
        surfaceClearedNotify()
        gameCompletedNotify()
        initADMOBBanner()
    }

    private fun initADMOBBanner() {
        val adRequest =
            AdRequest.Builder().build()
        adView1.loadAd(adRequest)
    }

    private fun initRewardVideo() {
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context)
        mRewardedVideoAd.rewardedVideoAdListener = this
        mRewardedVideoAd.loadAd(
            resources.getString(R.string.rewarded_ad_unit_id),
            AdRequest.Builder().build()
        )
    }

    private fun createGame() {
        if (activity!!.checkIsSupportsEs2()) {
            val metrics = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(metrics)
            displayDimensions = DisplayDimensions(
                metrics.widthPixels,
                metrics.heightPixels - 240
            )
            clearSurface()
            setSurface(displayDimensions)
            viewModel.setDisplayMetrics(displayDimensions!!)
            viewModel.startGame()
            startRenderer()
        }
    }

    private fun setSurface(displayDimensions: DisplayDimensions?) {
        surface = GLSurfaceView(context)
        // Request an OpenGL ES 2.0 compatible context.
        surface?.setEGLContextClientVersion(2)
        surface?.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        if (displayDimensions != null) {
            game_surface_container.setBackgroundColor(Color.argb(255, 0, 0, 0))
        }
    }

    private fun startRenderer() {
        viewModel.gameRendererCreated.observe(viewLifecycleOwner, Observer { gameRenderer ->
            gameRenderer.getContentIfNotHandle()?.let {
                if (surfaceStatus != SurfaceStatus.Started) {
                    setSurface(displayDimensions)
                    surface?.setRenderer(it)
                    game_surface_container.addView(surface)
                    surfaceStatus = SurfaceStatus.Started
                } else {
                    createGame()
                }
                foundedCountNotify()
                hiddenHintCountNotify()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (surfaceStatus is SurfaceStatus.Paused) {
            surface?.onResume()
            surfaceStatus = SurfaceStatus.Started
            createGame()
        }
        mRewardedVideoAd.resume(context)
    }

    override fun onPause() {
        super.onPause()
        if (surfaceStatus == SurfaceStatus.Started) {
            surface?.onPause()
            surfaceStatus = SurfaceStatus.Paused
        }
        mRewardedVideoAd.pause(context)
    }

    override fun onDestroy() {
        super.onDestroy()
        mRewardedVideoAd.destroy(context)
    }

    private fun clearSurface() {
        if (surface != null) {
            surface?.clearAnimation()
            surface?.destroyDrawingCache()
            surfaceStatus = SurfaceStatus.Cleared
        }

        if (game_surface_container != null) {
            game_surface_container.removeAllViewsInLayout()
        }
    }

    private fun foundedCountNotify() {
        viewModel.foundedCount.observe(viewLifecycleOwner, Observer { foundedCount ->
            game_counter.text = context?.resources?.getString(R.string._0_10, foundedCount)
        })
    }

    private fun hiddenHintCountNotify() {
        viewModel.hiddenHintCount.observe(viewLifecycleOwner, Observer { hintCount ->
            hint_counter.text = hintCount.toString()
        })
    }

    private fun surfaceClearedNotify() {
        viewModel.surfaceCleared.observe(viewLifecycleOwner, Observer { cleared ->
            if (cleared) clearSurface()
        })
    }

    private fun needMoreLevelNotify() {
        viewModel.needMoreLevelNotify.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandle()?.let { needMoreLevel ->
                if (needMoreLevel) {
                    findNavController().navigate(
                        R.id.downloadNewLevelFragment, null,
                        animateFade()
                    )
                }
            }
        })
    }

    private fun gameCompletedNotify() {
        viewModel.gameCompletedNotify.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandle()?.let { isCompleted ->
                if (isCompleted) {
                    showNoticeDialog()
                }
            }
        })
    }

    private fun showNoticeDialog() {
        dialog = GameCompleteDialog()
        dialog.show(childFragmentManager, "GameCompleteDialog")
    }

    private fun handleClick() {
        all_game.setOnClickListener { findNavController().navigateUp() }
        next_game.setOnClickListener { viewModel.startNextGame() }
        game_hint.setOnClickListener { viewModel.useHint() }
    }

    private fun setTouchListener() {
        game_surface_container?.setOnTouchListener { v, event ->
            val pointerId = event.getPointerId(event.actionIndex)
            val action = event.actionMasked
            viewModel.handleTouch(event, action, pointerId)
            true
        }
    }

    override fun onDialogAllGameClick() {
        view?.post { findNavController().navigateUp() }
    }

    override fun onDialogNextGameClick() {
        viewModel.startNextGame()
        findNavController().navigate(
            R.id.gameFragment, null,
            animateAndPopFromStack(R.id.gameFragment)
        )
    }

    override fun onDialogFreeHintsGameClick() {
        loadRewardedVideoAd()
    }

    private fun loadRewardedVideoAd() {
        if (mRewardedVideoAd.isLoaded) {
            mRewardedVideoAd.show()
        }
    }

    override fun onRewarded(reward: RewardItem) {
        viewModel.addRewardHints(2)
        hiddenHintCountNotify()
    }

    override fun onRewardedVideoAdLeftApplication() {
//        Toast.makeText(context, "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdClosed() {
//        Toast.makeText(context, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {
//        Toast.makeText(context, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdLoaded() {
//        Toast.makeText(context, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdOpened() {
//        Toast.makeText(context, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoStarted() {
//        Toast.makeText(context, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoCompleted() {
//        Toast.makeText(context, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show()
    }
}
