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
import com.bumptech.glide.Glide
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
import com.olbigames.finddifferencesgames.utilities.BannerGenerator
import com.olbigames.finddifferencesgames.utilities.ConnectionUtil
import com.olbigames.finddifferencesgames.utilities.Constants.FREE_HINT_DIALOG_TAG
import com.olbigames.finddifferencesgames.utilities.Constants.GAME_COMPLETED_DIALOG_TAG
import com.olbigames.finddifferencesgames.utilities.Constants.NO_VIDEO_DIALOG_TAG
import com.olbigames.finddifferencesgames.utilities.Constants.REWARDED_DIALOG_TAG
import com.olbigames.finddifferencesgames.utilities.Constants.REWARDED_VIDEO_AD_LISTENER_TAG
import com.olbigames.finddifferencesgames.utilities.animateAndPopFromStack
import kotlinx.android.synthetic.main.fragment_game.*
import kotlinx.android.synthetic.main.fragment_game_list.*
import javax.inject.Inject


class GameFragment : Fragment(R.layout.fragment_game),
    GameCompleteDialog.NoticeDialogListener,
    FreeHintDialog.FreeHintDialogListener,
    RewardedVideoAdListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: GameViewModel

    private var surfaceStatus: SurfaceStatus = SurfaceStatus.Cleared
    private var surface: GLSurfaceView? = null
    private var displayDimensions: DisplayDimensions? = null
    private lateinit var rewardedVideoAd: RewardedVideoAd
    private lateinit var dialog: GameCompleteDialog
    private lateinit var noVideoDialog: NoVideoDialog
    private lateinit var rewardedDialog: RewardedDialog
    private lateinit var freeHintDialog: FreeHintDialog
    private lateinit var sounds: SoundPool
    private var gestureTip: ImageView? = null
    private var ifNoHint = false

    private var displayWith = 0
    private var displayHeight = 0
    private var bannerHeight = 0
    private var sbeep = 0
    private var sound = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
        initRewardVideo()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]
        createGame()
        initADMOBBanner()
        initSoundEffect()
        handleClick()
        setTouchListener()
        showGestureTipNotify()
        needMoreLevelNotify()
        surfaceClearedNotify()
        soundEffectNotify()
        gameCompletedNotify()
        needUseSoundEffectNotify()
    }

    private fun showGestureTipNotify() {
        viewModel.gestureTipShown.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandle()?.let { notShown ->
                if (notShown) {
                    setupGestureTip()
                } else {
                    if (gestureTip != null) {
                        game_surface_container.removeView(gestureTip)
                    }
                }
            }
        })
    }

    private fun setupGestureTip() {
        gestureTip = ImageView(requireContext())
        val gtsize: Int = if (displayWith > displayHeight) {
            displayHeight - bannerHeight * 3
        } else {
            displayWith - bannerHeight * 2
        }
        val gtParams = FrameLayout.LayoutParams(
            gtsize,
            gtsize
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
        if (ConnectionUtil.isNetworkAvailable(context!!)) {
            val adRequest =
                AdRequest.Builder().build()
            adView1.loadAd(adRequest)
        } else {
            Glide.with(context!!)
                .load(BannerGenerator.getBanner(resources))
                .into(ivListBanner1)
            ivListBanner1.setOnClickListener {
                openMarket()
            }
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

    private fun initRewardVideo() {
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context)
        rewardedVideoAd.rewardedVideoAdListener = this
        rewardedVideoAd.loadAd(
            resources.getString(R.string.rewarded_ad_unit_id),
            AdRequest.Builder().build()
        )
    }

    private fun createGame() {
        if (activity!!.checkIsSupportsEs2()) {
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
        val displayW = displayWith
        val displayH = displayHeight

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

        if (bannerHeight60 * 4 + bannerWidth468 <= displayW) {
            bannerHeight = bannerHeight60
        }

        if (displayH > displayW) {
            bannerHeight *= 2
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
        viewModel.hiddenHintCount.observe(viewLifecycleOwner, Observer { hintCount ->
            if (hintCount == 0) {
                setupNoHintView()
            } else {
                setupHintView(hintCount)
            }
        })
    }

    private fun noMoHiddenHintNotify() {
        viewModel.noMoreHiddenHint.observe(viewLifecycleOwner, Observer {
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
                    showNoticeDialog()
                }
            }
        })
    }

    private fun showNoticeDialog() {
        dialog = GameCompleteDialog()
        dialog.show(childFragmentManager, GAME_COMPLETED_DIALOG_TAG)
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

    private fun handleClick() {
        all_game.setOnClickListener {
            findNavController().navigateUp()
        }
        next_game.setOnClickListener {
            viewModel.startNextGame()
        }
        game_hint.setOnClickListener {
            if (ifNoHint) {
                showFreeHintDialog()
            } else {
                viewModel.useHint()
            }
        }
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
        if (rewardedVideoAd.isLoaded) {
            rewardedVideoAd.show()
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

    override fun onDialogOkClick() {
        loadRewardedVideoAd()
        freeHintDialog.dismiss()
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
