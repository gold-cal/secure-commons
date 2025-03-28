package com.secure.commons.dialogs

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import com.secure.commons.R
import com.secure.commons.activities.BaseSimpleActivity
import com.secure.commons.databinding.DialogRadioGroupBinding
import com.secure.commons.databinding.RadioButtonBinding
import com.secure.commons.extensions.*

/**
 * A dialog for choosing between internal, root, SD card (optional) storage
 *
 * @param activity has to be activity to avoid some Theme.AppCompat issues
 * @param currPath current path to decide which storage should be preselected
 * @param pickSingleOption if only one option like "Internal" is available, select it automatically
 * @param callback an anonymous function
 *
 */
class StoragePickerDialog(
    val activity: BaseSimpleActivity, val currPath: String, val showRoot: Boolean, pickSingleOption: Boolean,
    val callback: (pickedPath: String) -> Unit
) {
    private val ID_INTERNAL = 1
    private val ID_SD = 2
    private val ID_OTG = 3
    private val ID_ROOT = 4

    private lateinit var radioGroup: RadioGroup
    private var dialog: AlertDialog? = null
    private var defaultSelectedId = 0
    private val availableStorages = ArrayList<String>()

    init {
        availableStorages.add(activity.internalStoragePath)
        when {
            activity.hasExternalSDCard() -> availableStorages.add(activity.sdCardPath)
            activity.hasOTGConnected() -> availableStorages.add("otg")
            showRoot -> availableStorages.add("root")
        }

        if (pickSingleOption && availableStorages.size == 1) {
            callback(availableStorages.first())
        } else {
            initDialog()
        }
    }

    private fun initDialog() {
        val inflater = LayoutInflater.from(activity)
        val resources = activity.resources
        val layoutParams = RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val binding = DialogRadioGroupBinding.inflate(activity.layoutInflater)
        radioGroup = binding.dialogRadioGroup
        val basePath = currPath.getBasePath(activity)

        val internalButton = RadioButtonBinding.inflate(activity.layoutInflater) //inflater.inflate(R.layout.radio_button, null) as RadioButton
        internalButton.apply {
            root.id = ID_INTERNAL
            root.text = resources.getString(R.string.internal)
            root.isChecked = basePath == root.context.internalStoragePath
            root.setOnClickListener { internalPicked() }
            if (root.isChecked) {
                defaultSelectedId = root.id
            }
        }
        radioGroup.addView(internalButton.root, layoutParams)

        if (activity.hasExternalSDCard()) {
            val sdButton = RadioButtonBinding.inflate(activity.layoutInflater) // inflater.inflate(R.layout.radio_button, null) as RadioButton
            sdButton.apply {
                root.id = ID_SD
                root.text = resources.getString(R.string.sd_card)
                root.isChecked = basePath == root.context.sdCardPath
                root.setOnClickListener { sdPicked() }
                if (root.isChecked) {
                    defaultSelectedId = root.id
                }
            }
            radioGroup.addView(sdButton.root, layoutParams)
        }

        if (activity.hasOTGConnected()) {
            val otgButton = RadioButtonBinding.inflate(activity.layoutInflater) //inflater.inflate(R.layout.radio_button, null) as RadioButton
            otgButton.apply {
                root.id = ID_OTG
                root.text = resources.getString(R.string.usb)
                root.isChecked = basePath == root.context.otgPath
                root.setOnClickListener { otgPicked() }
                if (root.isChecked) {
                    defaultSelectedId = root.id
                }
            }
            radioGroup.addView(otgButton.root, layoutParams)
        }

        // allow for example excluding the root folder at the gallery
        if (showRoot) {
            val rootButton = RadioButtonBinding.inflate(activity.layoutInflater) //inflater.inflate(R.layout.radio_button, null) as RadioButton
            rootButton.apply {
                root.id = ID_ROOT
                root.text = resources.getString(R.string.root)
                root.isChecked = basePath == "/"
                root.setOnClickListener { rootPicked() }
                if (root.isChecked) {
                    defaultSelectedId = root.id
                }
            }
            radioGroup.addView(rootButton.root, layoutParams)
        }

        activity.getAlertDialogBuilder().apply {
            activity.setupDialogStuff(binding.root, this, R.string.select_storage) { alertDialog ->
                dialog = alertDialog
            }
        }
    }

    private fun internalPicked() {
        dialog?.dismiss()
        callback(activity.internalStoragePath)
    }

    private fun sdPicked() {
        dialog?.dismiss()
        callback(activity.sdCardPath)
    }

    private fun otgPicked() {
        activity.handleOTGPermission {
            if (it) {
                callback(activity.otgPath)
                dialog?.dismiss()
            } else {
                radioGroup.check(defaultSelectedId)
            }
        }
    }

    private fun rootPicked() {
        dialog?.dismiss()
        callback("/")
    }
}
