package com.secure.commons.activities

import android.os.Bundle
import android.view.ViewGroup
import android.widget.RadioGroup
import com.secure.commons.R
import com.secure.commons.databinding.ActivityCustomizationBinding
import com.secure.commons.databinding.RadioButtonBinding
import com.secure.commons.extensions.*
import com.secure.commons.helpers.*

class CustomizeActionBarActivity : BaseSimpleActivity() {
    private val binding by viewBinding(ActivityCustomizationBinding::inflate)

    private var curStyle = 0
    private var bgColor = 0
    private var primaryColor = 0

    override fun getAppIconIDs() = intent.getIntegerArrayListExtra(APP_ICON_IDS) ?: ArrayList()

    override fun getAppLauncherName() = intent.getStringExtra(APP_LAUNCHER_NAME) ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        curStyle = baseConfig.actionBarStyle
        bgColor = getProperBackgroundColor()
        primaryColor = getProperPrimaryColor()
    }

    override fun onResume() {
        super.onResume()
        binding.customizationToolbar.title = "Action Bar Style"
        binding.customizationToolbar.menu.findItem(R.id.save).isVisible = true
        setupToolbar(binding.customizationToolbar, NavigationIcon.Cross, curStyle, primaryColor =  primaryColor, bgColor =  bgColor)
        setupOptionsMenu()

        arrayOf(binding.customizationThemeHolder, binding.customizationTextColorHolder, binding.customizationBackgroundColorHolder,
            binding.customizationPrimaryColorHolder, binding.customizationAccentColorHolder, binding.customizationAppIconColorHolder).forEach {
                it.beGone()
        }

        binding.actionBarRadioGroup.removeAllViews()

        val defaultStyleButton = createRadioButton()
        val flatStyleButton = createRadioButton()
        //val legacyDefaultButton = createRadioButton()

        defaultStyleButton.dialogRadioButton.apply {
            text = getString(R.string.default_action_bar)
            isChecked = curStyle == ACTION_BAR_DEFAULT
            id = ACTION_BAR_DEFAULT
            setOnClickListener {
                curStyle = ACTION_BAR_DEFAULT
                updateView()
            }
        }
        binding.actionBarRadioGroup.addView(defaultStyleButton.root,
            RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        flatStyleButton.dialogRadioButton.apply {
            text = getString(R.string.flat_action_bar)
            isChecked = curStyle == ACTION_BAR_FLAT
            id = ACTION_BAR_FLAT
            setOnClickListener {
                curStyle = ACTION_BAR_FLAT
                updateView()
            }
        }
        binding.actionBarRadioGroup.addView(flatStyleButton.root,
            RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        /*legacyDefaultButton.dialogRadioButton.apply {
            text = getString(R.string.legacy_action_bar)
            isChecked = curStyle == ACTION_BAR_LEGACY
            id = ACTION_BAR_LEGACY
            setOnClickListener {
                curStyle = ACTION_BAR_LEGACY
                //isChecked = curStyle == ACTION_BAR_LEGACY
                //defaultStyleButton.dialogRadioButton.isChecked = curStyle == ACTION_BAR_DEFAULT
                //flatStyleButton.dialogRadioButton.isChecked = curStyle == ACTION_BAR_FLAT
                updateView()
            }
        }
        binding.actionBarRadioGroup.addView(legacyDefaultButton.root,
            RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        */
    }

    private fun updateView() = updateTopBarColors(binding.customizationToolbar, curStyle, primaryColor, bgColor)

    private fun createRadioButton() = RadioButtonBinding.inflate(layoutInflater, null, false)

    private fun setupOptionsMenu() {
        binding.customizationToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.save -> {
                    saveAndExit()
                    true
                }
                else -> false
            }
        }
    }

    private fun saveAndExit() {
        baseConfig.actionBarStyle = curStyle
        finish()
    }
}
