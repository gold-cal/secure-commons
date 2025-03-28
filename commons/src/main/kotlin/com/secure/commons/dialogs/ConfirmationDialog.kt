package com.secure.commons.dialogs

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.secure.commons.R
import com.secure.commons.databinding.DialogMessageBinding
import com.secure.commons.extensions.getAlertDialogBuilder
import com.secure.commons.extensions.setupDialogStuff

/**
 * A simple dialog without any view, just a messageId, a positive button and optionally a negative button
 *
 * @param activity has to be activity context to avoid some Theme.AppCompat issues
 * @param message the dialogs message, can be any String. If empty, messageId is used
 * @param messageId the dialogs messageId ID. Used only if message is empty
 * @param positive positive buttons text ID
 * @param negative negative buttons text ID (optional)
 * @param callback an anonymous function
 */
class ConfirmationDialog(
    activity: Activity, message: String = "", messageId: Int = R.string.proceed_with_deletion, positive: Int = R.string.yes,
    negative: Int = R.string.no, val cancelOnTouchOutside: Boolean = true, dialogTitle: String = "", val callback: () -> Unit
) {
    private var dialog: AlertDialog? = null

    init {
        val binding = DialogMessageBinding.inflate(activity.layoutInflater)
        binding.message.text = if (message.isEmpty()) activity.resources.getString(messageId) else message

        val builder = activity.getAlertDialogBuilder()
            .setPositiveButton(positive) { dialog, which -> dialogConfirmed() }

        if (negative != 0) {
            builder.setNegativeButton(negative, null)
        }

        builder.apply {
            activity.setupDialogStuff(binding.root, this, titleText = dialogTitle, cancelOnTouchOutside = cancelOnTouchOutside) { alertDialog ->
                dialog = alertDialog
            }
        }
    }

    private fun dialogConfirmed() {
        dialog?.dismiss()
        callback()
    }
}
