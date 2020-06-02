package com.olbigames.finddifferencesgames.ui.game

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
import kotlinx.android.synthetic.main.dialog_free_hint.*

class FreeHintDialog : DialogFragment() {

    private lateinit var listener: FreeHintDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = try {
            parentFragment as FreeHintDialogListener
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
        return inflater.inflate(R.layout.dialog_free_hint, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_cancel.setOnClickListener { dialog?.dismiss() }
        btn_ok.setOnClickListener { listener.onDialogOkClick() }
    }

    interface FreeHintDialogListener {
        fun onDialogOkClick()
    }
}