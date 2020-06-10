package com.olbigames.finddifferencesgames.ui.game

import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.olbigames.finddifferencesgames.R

class RateAppDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.FindDifferenceAlertDialog)
        builder.setMessage(resources.getString(R.string.want_rate_app))
            .setPositiveButton(
                resources.getString(R.string.yes)
            ) { _, _ ->
                rateMyApp()
            }
            .setNeutralButton(resources.getString(R.string.later)
            ) { dialog, _ ->
                dialog.cancel()
            }
            .setNegativeButton(
                resources.getString(R.string.never)
            ) { dialog, _ ->
                dialog.cancel()
            }
        return builder.create()
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
}