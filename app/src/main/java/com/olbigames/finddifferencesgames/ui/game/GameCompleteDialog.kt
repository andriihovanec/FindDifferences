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
import kotlinx.android.synthetic.main.dialog_game_complete.*
import kotlinx.android.synthetic.main.dialog_game_complete.view.*

class GameCompleteDialog : DialogFragment() {

    private lateinit var listener: NoticeDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = try {
            parentFragment as NoticeDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString()
                        + " must implement NoticeDialogListener"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_game_complete, container, false)
        val anim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        anim.duration = 1000
        anim.start()
        return view
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        handleClick()
        initADMOBBanner()
    }

    private fun initADMOBBanner() {
        if (ConnectionUtil.isNetworkAvailable(requireContext())) {
            val adRequest =
                AdRequest.Builder().build()
            adView2.loadAd(adRequest)
            ivBanner.visibility = View.GONE
        } else {
            ib_free_hints.invisible()
            adView2.visibility = View.GONE
            Glide.with(requireContext())
                .load(BannerGenerator.getBanner(resources))
                .into(ivBanner)
            ivBanner.setOnClickListener {
                openMarket()
            }
        }
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