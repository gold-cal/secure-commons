package com.secure.commons.dialogs

import android.app.Activity
import com.secure.commons.R
import com.secure.commons.R.id.conflict_dialog_radio_keep_both
import com.secure.commons.R.id.conflict_dialog_radio_merge
import com.secure.commons.R.id.conflict_dialog_radio_skip
import com.secure.commons.databinding.DialogFileConflictBinding
import com.secure.commons.extensions.baseConfig
import com.secure.commons.extensions.beVisibleIf
import com.secure.commons.extensions.getAlertDialogBuilder
import com.secure.commons.extensions.setupDialogStuff
import com.secure.commons.helpers.CONFLICT_KEEP_BOTH
import com.secure.commons.helpers.CONFLICT_MERGE
import com.secure.commons.helpers.CONFLICT_OVERWRITE
import com.secure.commons.helpers.CONFLICT_SKIP
import com.secure.commons.models.FileDirItem

class FileConflictDialog(
    val activity: Activity, val fileDirItem: FileDirItem, val showApplyToAllCheckbox: Boolean,
    val callback: (resolution: Int, applyForAll: Boolean) -> Unit
) {
    val binding = DialogFileConflictBinding.inflate(activity.layoutInflater)

    init {
        binding.apply {
            val stringBase = if (fileDirItem.isDirectory) R.string.folder_already_exists else R.string.file_already_exists
            conflictDialogTitle.text = String.format(activity.getString(stringBase), fileDirItem.name)
            conflictDialogApplyToAll.isChecked = activity.baseConfig.lastConflictApplyToAll
            conflictDialogApplyToAll.beVisibleIf(showApplyToAllCheckbox)
            conflictDialogDivider.beVisibleIf(showApplyToAllCheckbox)
            conflictDialogRadioMerge.beVisibleIf(fileDirItem.isDirectory)

            val resolutionButton = when (activity.baseConfig.lastConflictResolution) {
                CONFLICT_OVERWRITE -> conflictDialogRadioOverwrite
                CONFLICT_MERGE -> conflictDialogRadioMerge
                else -> conflictDialogRadioSkip
            }
            resolutionButton.isChecked = true
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok) { dialog, which -> dialogConfirmed() }
            .setNegativeButton(R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this)
            }
    }

    private fun dialogConfirmed() {
        val resolution = when (binding.conflictDialogRadioGroup.checkedRadioButtonId) {
            conflict_dialog_radio_skip -> CONFLICT_SKIP
            conflict_dialog_radio_merge -> CONFLICT_MERGE
            conflict_dialog_radio_keep_both -> CONFLICT_KEEP_BOTH
            else -> CONFLICT_OVERWRITE
        }

        val applyToAll = binding.conflictDialogApplyToAll.isChecked
        activity.baseConfig.apply {
            lastConflictApplyToAll = applyToAll
            lastConflictResolution = resolution
        }

        callback(resolution, applyToAll)
    }
}
