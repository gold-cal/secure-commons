package com.secure.commons.dialogs

import android.app.Activity
import android.content.DialogInterface
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.secure.commons.R
import com.secure.commons.databinding.DialogCustomIntervalPickerBinding
import com.secure.commons.extensions.*
import com.secure.commons.helpers.DAY_SECONDS
import com.secure.commons.helpers.HOUR_SECONDS
import com.secure.commons.helpers.MINUTE_SECONDS

class CustomIntervalPickerDialog(val activity: Activity, val selectedSeconds: Int = 0, val showSeconds: Boolean = false, val callback: (minutes: Int) -> Unit) {
    private var dialog: AlertDialog? = null
    private var binding = DialogCustomIntervalPickerBinding.inflate(activity.layoutInflater)

    init {
        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok) { dialogInterface, i -> confirmReminder() }
            .setNegativeButton(R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this) { alertDialog ->
                    dialog = alertDialog
                    alertDialog.showKeyboard(binding.dialogCustomIntervalValue)
                }
            }

        binding.apply {
            dialogRadioSeconds.beVisibleIf(showSeconds)
            when {
                selectedSeconds == 0 -> dialogRadioView.check(R.id.dialog_radio_minutes)
                selectedSeconds % DAY_SECONDS == 0 -> {
                    dialogRadioView.check(R.id.dialog_radio_days)
                    dialogCustomIntervalValue.setText((selectedSeconds / DAY_SECONDS).toString())
                }
                selectedSeconds % HOUR_SECONDS == 0 -> {
                    dialogRadioView.check(R.id.dialog_radio_hours)
                    dialogCustomIntervalValue.setText((selectedSeconds / HOUR_SECONDS).toString())
                }
                selectedSeconds % MINUTE_SECONDS == 0 -> {
                    dialogRadioView.check(R.id.dialog_radio_minutes)
                    dialogCustomIntervalValue.setText((selectedSeconds / MINUTE_SECONDS).toString())
                }
                else -> {
                    dialogRadioView.check(R.id.dialog_radio_seconds)
                    dialogCustomIntervalValue.setText(selectedSeconds.toString())
                }
            }

            dialogCustomIntervalValue.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                    if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        dialog?.getButton(DialogInterface.BUTTON_POSITIVE)?.performClick()
                        return true
                    }

                    return false
                }
            })
        }
    }

    private fun confirmReminder() {
        val value = binding.dialogCustomIntervalValue.value
        val multiplier = getMultiplier(binding.dialogRadioView.checkedRadioButtonId)
        val minutes = Integer.valueOf(if (value.isEmpty()) "0" else value)
        callback(minutes * multiplier)
        activity.hideKeyboard()
        dialog?.dismiss()
    }

    private fun getMultiplier(id: Int) = when (id) {
        R.id.dialog_radio_days -> DAY_SECONDS
        R.id.dialog_radio_hours -> HOUR_SECONDS
        R.id.dialog_radio_minutes -> MINUTE_SECONDS
        else -> 1
    }
}
