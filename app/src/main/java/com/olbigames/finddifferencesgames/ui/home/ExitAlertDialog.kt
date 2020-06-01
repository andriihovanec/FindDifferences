package com.olbigames.finddifferencesgames.ui.home

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.olbigames.finddifferencesgames.R

class ExitAlertDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.FindDifferenceAlertDialog)
        builder.setMessage(resources.getString(R.string.are_you_sure_you_want_to_quit))
            .setPositiveButton(
                resources.getString(R.string.yes)
            ) { _, _ ->
                activity?.finish()
            }
            .setNegativeButton(
                resources.getString(R.string.no)
            ) { dialog, _ ->
                dialog.cancel()
            }
        return builder.create()
    }
}