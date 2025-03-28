package com.secure.commons.dialogs

import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.secure.commons.R
import com.secure.commons.activities.BaseSimpleActivity
import com.secure.commons.databinding.DialogSelectAlarmSoundBinding
import com.secure.commons.extensions.*
import com.secure.commons.helpers.SILENT
import com.secure.commons.models.AlarmSound
import com.secure.commons.models.RadioItem
import com.secure.commons.views.MyCompatRadioButton

class SelectAlarmSoundDialog(
    val activity: BaseSimpleActivity, val currentUri: String, val audioStream: Int, val pickAudioIntentId: Int,
    val type: Int, val loopAudio: Boolean, val onAlarmPicked: (alarmSound: AlarmSound?) -> Unit,
    val onAlarmSoundDeleted: (alarmSound: AlarmSound) -> Unit
) {
    private val ADD_NEW_SOUND_ID = -2

    private val binding = DialogSelectAlarmSoundBinding.inflate(activity.layoutInflater)
    private var systemAlarmSounds = ArrayList<AlarmSound>()
    private var yourAlarmSounds = ArrayList<AlarmSound>()
    private var mediaPlayer: MediaPlayer? = null
    private val config = activity.baseConfig
    private var dialog: AlertDialog? = null

    init {
        activity.getAlarmSounds(type) {
            systemAlarmSounds = it
            gotSystemAlarms()
        }

        binding.dialogSelectAlarmYourLabel.setTextColor(activity.getProperPrimaryColor())
        binding.dialogSelectAlarmSystemLabel.setTextColor(activity.getProperPrimaryColor())

        addYourAlarms()

        activity.getAlertDialogBuilder()
            .setOnDismissListener { mediaPlayer?.stop() }
            .setPositiveButton(R.string.ok) { dialog, which -> dialogConfirmed() }
            .setNegativeButton(R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this) { alertDialog ->
                    dialog = alertDialog
                    alertDialog.window?.volumeControlStream = audioStream
                }
            }
    }

    private fun addYourAlarms() {
        binding.dialogSelectAlarmYourRadio.removeAllViews()
        val token = object : TypeToken<ArrayList<AlarmSound>>() {}.type
        yourAlarmSounds = Gson().fromJson<ArrayList<AlarmSound>>(config.yourAlarmSounds, token) ?: ArrayList()
        yourAlarmSounds.add(AlarmSound(ADD_NEW_SOUND_ID, activity.getString(R.string.add_new_sound), ""))
        yourAlarmSounds.forEach {
            addAlarmSound(it, binding.dialogSelectAlarmYourRadio)
        }
    }

    private fun gotSystemAlarms() {
        systemAlarmSounds.forEach {
            addAlarmSound(it, binding.dialogSelectAlarmSystemRadio)
        }
    }

    private fun addAlarmSound(alarmSound: AlarmSound, holder: ViewGroup) {
        val radioButton = (activity.layoutInflater.inflate(R.layout.item_select_alarm_sound, null) as MyCompatRadioButton).apply {
            text = alarmSound.title
            isChecked = alarmSound.uri == currentUri
            id = alarmSound.id
            setColors(activity.getProperTextColor(), activity.getProperPrimaryColor(), activity.getProperBackgroundColor())
            setOnClickListener {
                alarmClicked(alarmSound)

                if (holder == binding.dialogSelectAlarmSystemRadio) {
                    binding.dialogSelectAlarmYourRadio.clearCheck()
                } else {
                    binding.dialogSelectAlarmSystemRadio.clearCheck()
                }
            }

            if (alarmSound.id != -2 && holder == binding.dialogSelectAlarmYourRadio) {
                setOnLongClickListener {
                    val items = arrayListOf(RadioItem(1, context.getString(R.string.remove)))

                    RadioGroupDialog(activity, items) {
                        removeAlarmSound(alarmSound)
                    }
                    true
                }
            }
        }

        holder.addView(radioButton, RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    private fun alarmClicked(alarmSound: AlarmSound) {
        when {
            alarmSound.uri == SILENT -> mediaPlayer?.stop()
            alarmSound.id == ADD_NEW_SOUND_ID -> {
                val action = Intent.ACTION_OPEN_DOCUMENT
                val intent = Intent(action).apply {
                    type = "audio/*"
                    flags = flags or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                }

                try {
                    activity.startActivityForResult(intent, pickAudioIntentId)
                } catch (e: ActivityNotFoundException) {
                    activity.toast(R.string.no_app_found)
                }
                dialog?.dismiss()
            }
            else -> try {
                mediaPlayer?.reset()
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer().apply {
                        setAudioStreamType(audioStream)
                        isLooping = loopAudio
                    }
                }

                mediaPlayer?.apply {
                    setDataSource(activity, Uri.parse(alarmSound.uri))
                    prepare()
                    start()
                }
            } catch (e: Exception) {
                activity.showErrorToast(e)
            }
        }
    }

    private fun removeAlarmSound(alarmSound: AlarmSound) {
        val token = object : TypeToken<ArrayList<AlarmSound>>() {}.type
        yourAlarmSounds = Gson().fromJson<ArrayList<AlarmSound>>(config.yourAlarmSounds, token) ?: ArrayList()
        yourAlarmSounds.remove(alarmSound)
        config.yourAlarmSounds = Gson().toJson(yourAlarmSounds)
        addYourAlarms()

        if (alarmSound.id == binding.dialogSelectAlarmYourRadio.checkedRadioButtonId) {
            binding.dialogSelectAlarmYourRadio.clearCheck()
            binding.dialogSelectAlarmSystemRadio.check(systemAlarmSounds.firstOrNull()?.id ?: 0)
        }

        onAlarmSoundDeleted(alarmSound)
    }

    private fun dialogConfirmed() {
        if (binding.dialogSelectAlarmYourRadio.checkedRadioButtonId != -1) {
            val checkedId = binding.dialogSelectAlarmYourRadio.checkedRadioButtonId
            onAlarmPicked(yourAlarmSounds.firstOrNull { it.id == checkedId })
        } else {
            val checkedId = binding.dialogSelectAlarmSystemRadio.checkedRadioButtonId
            onAlarmPicked(systemAlarmSounds.firstOrNull { it.id == checkedId })
        }
    }
}

