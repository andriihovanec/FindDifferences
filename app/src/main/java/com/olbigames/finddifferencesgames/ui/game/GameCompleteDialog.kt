package com.olbigames.finddifferencesgames.ui.game

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.olbigames.finddifferencesgames.R
import com.olbigames.finddifferencesgames.extension.invisible
import com.olbigames.finddifferencesgames.utilities.BannerGenerator
import com.olbigames.finddifferencesgames.utilities.ConnectionUtil
import com.olbigames.finddifferencesgames.utilities.Constants
import com.olbigames.finddifferencesgames.utilities.Constants.DIALOG_LISTENER_EXCEPTION
import com.olbigames.finddifferencesgames.utilities.Constants.OBJECT_ANIMATOR_PROPERTY_NAME
import kotlinx.android.synthetic.main.dialog_game_complete.*

class GameCompleteDialog : DialogFragment() {

    private lateinit var listener: NoticeDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initClickListener()
    }

    private fun initClickListener() {
        listener = try {
            parentFragment as NoticeDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString() + DIALOG_LISTENER_EXCEPTION
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_game_complete, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initADMOBBanner()
        setupAnimation()
        handleClick()
    }

    private fun initADMOBBanner() {
        if (ConnectionUtil.isNetworkAvailable(requireContext()))
            showAdMobBanner()
        else showOlbiBanner()
    }

    private fun showAdMobBanner() {
        val adRequest =
            AdRequest.Builder().build()
        adView2.loadAd(adRequest)
        ivBanner.visibility = View.GONE
    }

    private fun showOlbiBanner() {
        ib_free_hints.invisible()
        adView2.visibility = View.GONE
        Glide.with(requireContext())
            .load(BannerGenerator.getBanner(resources))
            .into(ivBanner)
        ivBanner.setOnClickListener {
            goToMarket()
        }
    }

    private fun goToMarket() =
        try {
            openMarket()
        } catch (e: ActivityNotFoundException) {
            openPlayStore()
        }

    private fun openMarket() =
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.OLBI_MARKET_URL)))

    private fun openPlayStore() =
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constants.OLBI_PLAY_STORE_URL)))

    private fun setupAnimation() {
        val anim = ObjectAnimator.ofFloat(view, OBJECT_ANIMATOR_PROPERTY_NAME, 0f, 1f)
        anim.duration = 1000
        anim.start()
    }

    private fun handleClick() {
        all_game.setOnClickListener { listener.onDialogAllGameClick() }
        next_game.setOnClickListener { listener.onDialogNextGameClick() }
        ib_free_hints.setOnClickListener {
            listener.onDialogFreeHintsGameClick()
            ib_free_hints.invisible()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    interface NoticeDialogListener {
        fun onDialogAllGameClick()
        fun onDialogNextGameClick()
        fun onDialogFreeHintsGameClick()
    }
}