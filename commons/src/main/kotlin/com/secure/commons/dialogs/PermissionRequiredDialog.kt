package com.secure.commons.dialogs

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.secure.commons.R
import com.secure.commons.databinding.DialogMessageBinding
import com.secure.commons.extensions.getAlertDialogBuilder
import com.secure.commons.extensions.openNotificationSettings
import com.secure.commons.extensions.openRequestExactAlarmSettings
import com.secure.commons.extensions.setupDialogStuff

class PermissionRequiredDialog(val activity: Activity, textId: Int, positiveActionCallback: Int, id: String = "") {
    private var dialog: AlertDialog? = null

    init {
        val binding = DialogMessageBinding.inflate(activity.layoutInflater)
            //activity.layoutInflater.inflate(R.layout.dialog_message, null)
        binding.message.text = activity.getString(textId)

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.grant_permission) { dialog, which ->
                if (positiveActionCallback == 1) {
                    activity.openRequestExactAlarmSettings(id) // id = Application_id
                } else {
                    activity.openNotificationSettings()
                }
            }
            .setNegativeButton(R.string.cancel, null).apply {
                val title = activity.getString(R.string.permission_required)
                activity.setupDialogStuff(binding.root, this, titleText = title) { alertDialog ->
                    dialog = alertDialog
                }
            }
    }
}
