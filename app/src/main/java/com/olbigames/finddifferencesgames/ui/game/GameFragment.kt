package com.olbigames.finddifferencesgames.ui.game

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.olbigames.finddifferencesgames.App
import com.olbigames.finddifferencesgames.R
import com.olbigames.finddifferencesgames.extension.checkIsSupportsEs2
import com.olbigames.finddifferencesgames.extension.invisible
import com.olbigames.finddifferencesgames.extension.loadFromDrawable
import com.olbigames.finddifferencesgames.extension.visible
import com.olbigames.finddifferencesgames.presentation.viewmodel.GameViewModel
import com.olbigames.finddifferencesgames.renderer.DisplayDimensions
import com.olbigames.finddifferencesgames.utilities.BannerGenerator
import com.olbigames.finddifferencesgames.utilities.ConnectionUtil
import com.olbigames.finddifferencesgames.utilities.Constants.APPS_ON_GOOGLE_PLAY_STORE
import com.olbigames.finddifferencesgames.utilities.Constants.APP_MARKET_DETAILS
import com.olbigames.finddifferencesgames.utilities.Constants.FREE_HINT_DIALOG_TAG
import com.olbigames.finddifferencesgames.utilities.Constants.GAME_COMPLETED_DIALOG_TAG
import com.olbigames.finddifferencesgames.utilities.Constants.GAME_COMPLETED_KEY
import com.olbigames.finddifferencesgames.utilities.Constants.NO_VIDEO_DIALOG_TAG
import com.olbigames.finddifferencesgames.utilities.Constants.OLBI_GAMES_SEARCH_MARKET_URL
import com.olbigames.finddifferencesgames.utilities.Constants.OLBI_GAMES_SEARCH_PLAY_STORE_URL
import com.olbigames.finddifferencesgames.utilities.Constants.RATE_APP_DIALOG_TAG
import com.olbigames.finddifferencesgames.utilities.Constants.REWARDED_DIALOG_TAG
import com.olbigames.finddifferencesgames.utilities.Constants.REWARDED_VIDEO_AD_LISTENER_TAG
import com.olbigames.finddifferencesgames.utilities.Globals
import kotlinx.android.synthetic.main.fragment_game.*
import javax.inject.Inject

class GameFragment : Fragment(R.layout.fragment_game),
    GameCompletedDialog.GameCompletedDialogListener,
    FreeHintDialog.FreeHintDialogListener,
    RateAppDialog.RateDialogClickListener,
    RewardedVideoAdListener {

    companion object {
        const val RATE_APP_REQUEST_CODE = 465
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: GameViewModel

    private var surfaceStatus: SurfaceStatus = SurfaceStatus.Cleared
    private var surface: GLSurfaceView? = null
    private var displayDimensions: DisplayDimensions? = null
    private lateinit var rewardedVideoAd: RewardedVideoAd
    private lateinit var interstitialAd: InterstitialAd
    private lateinit var gameCompletedDialog: GameCompletedDialog
    private lateinit var noVideoDialog: NoVideoDialog
    private lateinit var rewardedDialog: RewardedDialog
    private lateinit var freeHintDialog: FreeHintDialog
    private lateinit var rateAppDialog: RateAppDialog
    private lateinit var sounds: SoundPool
    private var gestureTip: ImageView? = null
    private var ifNoHint = false
    private var gameCompleted = false

    private var displayWith = 0
    private var displayHeight = 0
    private var bannerHeight = 0
    private var sbeep = 0
    private var sound = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            gameCompleted = it.getBoolean(GAME_COMPLETED_KEY)
        }
        App.appComponent.inject(this)
        initRewardVideo()
        initADS()
    }

    private fun initADS() {
        Globals.adRequest = AdRequest.Builder().build()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]
        createGame()
        initADMOBBanner()
        initInterstitialAd()
        initSoundEffect()
        handleClick()
        setTouchListener()
        showGestureTipNotify()
        needMoreLevelNotify()
        surfaceClearedNotify()
        soundEffectNotify()
        gameCompletedNotify()
        interstitialAdNotify()
        needUseSoundEffectNotify()
        rateAppNotify()
    }

    private fun showGestureTipNotify() {
        viewModel.gestureTipShown.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandle()?.let { notShown ->
                if (notShown) setupGestureTip()
                else if (gestureTip != null) game_surface_container.removeView(gestureTip)
            }
        })
    }

    private fun setupGestureTip() {
        gestureTip = ImageView(requireContext())
        val gestureTipSize = if (displayWith > displayHeight) displayHeight - bannerHeight * 3
        else displayWith - bannerHeight * 2
        val gtParams = FrameLayout.LayoutParams(
            gestureTipSize,
            gestureTipSize
        )
        gtParams.gravity = Gravity.CENTER
        gestureTip?.layoutParams = gtParams
        gestureTip?.setImageResource(R.drawable.gestures_tip)
        game_surface_container.addView(gestureTip)
        gestureTip?.setOnClickListener { game_surface_container.removeView(gestureTip) }
    }

    private fun soundEffectNotify() {
        viewModel.soundEffect.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandle()?.let { soundEffect ->
                sound = soundEffect
            }
        })
    }

    private fun initSoundEffect() {
        createNewSoundPool()
        requireActivity().volumeControlStream = AudioManager.STREAM_MUSIC
        sbeep = sounds.load(requireContext(), R.raw.beep, 1)
    }

    private fun createNewSoundPool() {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        sounds = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(attributes)
            .build()
    }

    private fun initADMOBBanner() {
        if (gameCompleted) cl_ad_view.invisible()
        else cl_ad_view.visible()

        if (ConnectionUtil.isNetworkAvailable(requireContext())) {
            adView1.loadAd(Globals.adRequest)
        } else {
            ivListBanner1.loadFromDrawable(BannerGenerator.getBanner(resources))
            ivListBanner1.setOnClickListener { goToMarket() }
        }
    }

    private fun goToMarket() =
        try {
            searchOlbiGamesOnMarket()
        } catch (e: ActivityNotFoundException) {
            searchOlbiGamesOnPlayStore()
        }

    private fun searchOlbiGamesOnMarket() =
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(OLBI_GAMES_SEARCH_MARKET_URL)))

    private fun searchOlbiGamesOnPlayStore() =
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(OLBI_GAMES_SEARCH_PLAY_STORE_URL)))

    private fun initRewardVideo() {
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context)
        rewardedVideoAd.rewardedVideoAdListener = this
        rewardedVideoAd.loadAd(
            resources.getString(R.string.rewarded_ad_unit_id),
            Globals.adRequest
        )
    }

    private fun initInterstitialAd() {
        interstitialAd = InterstitialAd(requireContext())
        interstitialAd.adUnitId = resources.getString(R.string.interst_ad_unit_id)
        interstitialAd.loadAd(Globals.adRequest)
    }

    private fun showInterstitialAd() {
        if (interstitialAd.isLoaded) interstitialAd.show()
    }

    private fun createGame() {
        if (requireActivity().checkIsSupportsEs2()) {
            val metrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(metrics)

            displayWith = metrics.widthPixels
            displayHeight = metrics.heightPixels
            bannerHeight = calculateBannerHeight()

            displayDimensions = DisplayDimensions(
                displayWith,
                displayHeight - bannerHeight,
                bannerHeight
            )
            clearSurface()
            setSurface(displayDimensions)
            viewModel.setDisplayMetrics(displayDimensions!!)
            viewModel.startGame()
            startRenderer()
        }
    }

    private fun calculateBannerHeight(): Int {
        var bannerHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            50f,
            resources.displayMetrics
        ).toInt()

        val bannerWidth468 = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            468f,
            resources.displayMetrics
        ).toInt()

        val bannerHeight60 = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            60f,
            resources.displayMetrics
        ).toInt()

        if ((bannerHeight60 * 4 + bannerWidth468) <= displayWith) {
            bannerHeight = bannerHeight60
        }

        return bannerHeight
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
                    progress.invisible()
                } else {
                    createGame()
                }
                viewModel.ifNeedShownGestureTip()
                foundedCountNotify()
                hiddenHintCountNotify()
                noMoHiddenHintNotify()
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
        rewardedVideoAd.resume(context)
    }

    override fun onPause() {
        super.onPause()
        if (surfaceStatus == SurfaceStatus.Started) {
            surface?.onPause()
            surfaceStatus = SurfaceStatus.Paused
        }
        rewardedVideoAd.pause(context)
    }

    override fun onDestroy() {
        super.onDestroy()
        rewardedVideoAd.destroy(context)
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

    private fun needUseSoundEffectNotify() {
        viewModel.needUseSoundEffect.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandle()?.let { needed ->
                if (needed) {
                    sounds.play(sbeep, sound, sound, 0, 0, 1.0f)
                }
            }
        })
    }

    private fun hiddenHintCountNotify() {
        viewModel.hintCount.observe(viewLifecycleOwner, Observer { hintCount ->
            if (hintCount == 0) {
                setupNoHintView()
            } else {
                setupHintView(hintCount)
            }
        })
    }

    private fun noMoHiddenHintNotify() {
        viewModel.noMoreHint.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandle()?.let { noMoreHint ->
                if (noMoreHint) {
                    setupNoHintView()
                }
            }
        })
    }

    private fun setupNoHintView() {
        ifNoHint = true
        hint_counter.text = ""
        game_hint.setImageResource(R.drawable.hint_add)
    }

    private fun setupHintView(hintCount: Int) {
        hint_counter.text = hintCount.toString()
        game_hint.setImageResource(R.drawable.hint)
        ifNoHint = false
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
                    findNavController().navigate(GameFragmentDirections.actionGameFragmentToDownloadNewLevelFragment())
                }
            }
        })
    }

    private fun gameCompletedNotify() {
        viewModel.gameCompletedNotify.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandle()?.let { isCompleted ->
                if (isCompleted) {
                    gameCompleted = true
                    cl_ad_view.invisible()
                    showGameCompletedDialog()
                }
            }
        })
    }

    private fun showGameCompletedDialog() {
        gameCompletedDialog = GameCompletedDialog()
        gameCompletedDialog.show(childFragmentManager, GAME_COMPLETED_DIALOG_TAG)
    }

    private fun showNoVideoDialog() {
        noVideoDialog = NoVideoDialog()
        noVideoDialog.show(childFragmentManager, NO_VIDEO_DIALOG_TAG)
    }

    private fun showRewardedDialog() {
        rewardedDialog = RewardedDialog()
        rewardedDialog.show(childFragmentManager, REWARDED_DIALOG_TAG)
    }

    private fun showFreeHintDialog() {
        freeHintDialog = FreeHintDialog()
        freeHintDialog.show(childFragmentManager, FREE_HINT_DIALOG_TAG)
    }

    private fun showRateAppDialog() {
        rateAppDialog = RateAppDialog()
        rateAppDialog.show(childFragmentManager, RATE_APP_DIALOG_TAG)
    }

    private fun handleClick() {
        all_game.setOnClickListener {
            findNavController().navigateUp()
        }
        next_game.setOnClickListener {
            createGame()
            viewModel.startNextGame()
        }
        game_hint.setOnClickListener {
            if (ifNoHint) showFreeHintDialog()
            else viewModel.useHint()
        }
        game_counter_container.setOnClickListener { showRateAppDialog() }
    }

    private fun setTouchListener() {
        game_surface_container?.setOnTouchListener { _, event ->
            val pointerId = event.getPointerId(event.actionIndex)
            val action = event.actionMasked
            viewModel.handleTouch(event, action, pointerId)
            true
        }
    }

    private fun loadRewardedVideoAd() {
        if (rewardedVideoAd.isLoaded) rewardedVideoAd.show()
    }

    private fun interstitialAdNotify() {
        viewModel.interstitialAdShown.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandle()?.let { needShowInterstitialAd ->
                if (needShowInterstitialAd) showInterstitialAd()
            }
        })
    }

    private fun rateAppNotify() {
        viewModel.rateAppShown.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandle()?.let { needRateApp ->
                if (needRateApp) showRateAppDialog()
            }
        })
    }

    private fun rateMyApp() {
        try {
            openAppDetailsOnMarket()
        } catch (e: ActivityNotFoundException) {
            openAppDetailsOnPlayStore()
        }
    }

    private fun openAppDetailsOnMarket() {
        startActivityForResult(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(APP_MARKET_DETAILS + requireActivity().packageName)
            ),
            RATE_APP_REQUEST_CODE
        )
    }

    private fun openAppDetailsOnPlayStore() {
        startActivityForResult(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(APPS_ON_GOOGLE_PLAY_STORE + requireActivity().packageName)
            ),
            RATE_APP_REQUEST_CODE
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(GAME_COMPLETED_KEY, gameCompleted)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RATE_APP_REQUEST_CODE) viewModel.showGameCompletedDialog()
    }

    override fun onGameCompletedDialogAllGameClick() {
        progress.visible()
        gameCompletedDialog.dismiss()
        view?.post { findNavController().navigateUp() }
    }

    override fun onGameCompletedDialogNextGameClick() {
        gameCompletedDialog.dismiss()
        createGame()
        viewModel.startNextGame()
    }

    override fun onGameCompletedDialogFreeHintsGameClick() {
        loadRewardedVideoAd()
    }

    override fun onDialogOkClick() {
        loadRewardedVideoAd()
        freeHintDialog.dismiss()
    }

    override fun rateDialogPositiveButtonClick() {
        rateMyApp()
    }

    override fun rateDialogNeutralButtonClick() {
        viewModel.showGameCompletedDialog()
    }

    override fun rateDialogNegativeButtonClick() {
        viewModel.showGameCompletedDialog()
    }

    override fun onRewarded(reward: RewardItem) {
        Log.d(REWARDED_VIDEO_AD_LISTENER_TAG, "Rewarded")
        viewModel.addRewardHints()
    }

    override fun onRewardedVideoAdLeftApplication() {
        Log.d(REWARDED_VIDEO_AD_LISTENER_TAG, "Left application")
    }

    override fun onRewardedVideoAdClosed() {
        Log.d(REWARDED_VIDEO_AD_LISTENER_TAG, "Closed")
    }

    override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {
        Log.d(REWARDED_VIDEO_AD_LISTENER_TAG, "Failed to load")
        showNoVideoDialog()
    }

    override fun onRewardedVideoAdLoaded() {
        Log.d(REWARDED_VIDEO_AD_LISTENER_TAG, "Loaded")
    }

    override fun onRewardedVideoAdOpened() {
        Log.d(REWARDED_VIDEO_AD_LISTENER_TAG, "Opened")
    }

    override fun onRewardedVideoStarted() {
        Log.d(REWARDED_VIDEO_AD_LISTENER_TAG, "Started")
    }

    override fun onRewardedVideoCompleted() {
        Log.d(REWARDED_VIDEO_AD_LISTENER_TAG, "Completed")
        showRewardedDialog()
    }
}
