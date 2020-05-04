package com.olbigames.finddifferencesgames.ui.game

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.olbigames.finddifferencesgames.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_game_complete.*


class GameCompleteDialog : DialogFragment() {

    private lateinit var listener: NoticeDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = try {
            nav_host_fragment as NoticeDialogListener
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        all_game.setOnClickListener { listener.onDialogAllGameClick() }
        next_game.setOnClickListener { listener.onDialogNextGameClick() }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    interface NoticeDialogListener {
        fun onDialogAllGameClick()
        fun onDialogNextGameClick()
    }
}