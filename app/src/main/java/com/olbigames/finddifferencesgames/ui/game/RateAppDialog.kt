package com.olbigames.finddifferencesgames.ui.game

import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.olbigames.finddifferencesgames.R
import com.olbigames.finddifferencesgames.utilities.Constants

class RateAppDialog : DialogFragment() {

    private lateinit var listener: RateDialogClickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initClickListener()
    }

    private fun initClickListener() {
        listener = try {
            parentFragment as RateDialogClickListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString() + Constants.DIALOG_LISTENER_EXCEPTION
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.FindDifferenceAlertDialog)
        builder.setMessage(resources.getString(R.string.want_rate_app))
            .setPositiveButton(
                resources.getString(R.string.yes)
            ) { _, _ ->
                listener.rateDialogPositiveButtonClick()
            }
            .setNeutralButton(resources.getString(R.string.later)
            ) { _, _ ->
                listener.rateDialogNeutralButtonClick()
            }
            .setNegativeButton(
                resources.getString(R.string.never)
            ) { _, _ ->
                listener.rateDialogNegativeButtonClick()
            }
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    private fun rateMyApp() {
        val uri =
            Uri.parse("market://details?id=" + requireActivity().packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + requireActivity().packageName)
                )
            )
        }
    }

    interface RateDialogClickListener {
        fun rateDialogPositiveButtonClick()
        fun rateDialogNeutralButtonClick()
        fun rateDialogNegativeButtonClick()
    }
}