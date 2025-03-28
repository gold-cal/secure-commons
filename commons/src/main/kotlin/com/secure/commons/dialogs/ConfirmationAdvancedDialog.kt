package com.secure.commons.dialogs

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.secure.commons.R
import com.secure.commons.databinding.DialogMessageBinding
import com.secure.commons.extensions.getAlertDialogBuilder
import com.secure.commons.extensions.setupDialogStuff

// similar fo ConfirmationDialog, but has a callback for negative button too
class ConfirmationAdvancedDialog(
    activity: Activity, message: String = "", messageId: Int = R.string.proceed_with_deletion, positive: Int = R.string.yes,
    negative: Int = R.string.no, val cancelOnTouchOutside: Boolean = true, val callback: (result: Boolean) -> Unit
) {
    private var dialog: AlertDialog? = null

    init {
        val binding = DialogMessageBinding.inflate(activity.layoutInflater)
        binding.message.text = if (message.isEmpty()) activity.resources.getString(messageId) else message

        val builder = activity.getAlertDialogBuilder()
            .setPositiveButton(positive) { dialog, which -> positivePressed() }

        if (negative != 0) {
            builder.setNegativeButton(negative) { dialog, which -> negativePressed() }
        }

        if (!cancelOnTouchOutside) {
            builder.setOnCancelListener { negativePressed() }
        }

        builder.apply {
            activity.setupDialogStuff(binding.root, this, cancelOnTouchOutside = cancelOnTouchOutside) { alertDialog ->
                dialog = alertDialog
            }
        }
    }

    private fun positivePressed() {
        dialog?.dismiss()
        callback(true)
    }

    private fun negativePressed() {
        dialog?.dismiss()
        callback(false)
    }
}
