package com.secure.commons.activities

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import com.secure.commons.R
import com.secure.commons.databinding.ActivityCustomizationBinding
import com.secure.commons.dialogs.*
import com.secure.commons.extensions.*
import com.secure.commons.helpers.*
import com.secure.commons.models.MyTheme
import com.secure.commons.models.RadioItem
import com.secure.commons.views.MyTextView
import kotlin.math.abs

class CustomizationActivity : BaseSimpleActivity() {
    private val THEME_LIGHT = 0
    private val THEME_DARK = 1
    private val THEME_SOLARIZED = 2
    private val THEME_DARK_RED = 3
    private val THEME_BLACK_WHITE = 4
    private val THEME_CUSTOM = 5
    //private val THEME_SHARED = 6
    private val THEME_WHITE = 7
    private val THEME_AUTO = 8
    private val THEME_SYSTEM = 9    // Material You

    private var curTextColor = 0
    private var curBackgroundColor = 0
    private var curPrimaryColor = 0
    private var curAccentColor = 0
    private var curSelectedThemeId = 0
    private var lastSavePromptTS = 0L
    private var hasUnsavedChanges = false
    private var predefinedThemes = LinkedHashMap<Int, MyTheme>()
    private var curPrimaryLineColorPicker: LineColorPickerDialog? = null

    private val binding by viewBinding(ActivityCustomizationBinding::inflate)

    override fun getAppIconIDs() = intent.getIntegerArrayListExtra(APP_ICON_IDS) ?: ArrayList()

    override fun getAppLauncherName() = intent.getStringExtra(APP_LAUNCHER_NAME) ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupOptionsMenu()
        refreshMenuItems()
        initColorVariables()
        setupThemes()

        val textColor = if (baseConfig.isUsingSystemTheme) {
            getProperTextColor()
        } else {
            baseConfig.textColor
        }

        updateLabelColors(textColor)
    }

    override fun onResume() {
        super.onResume()
        setTheme(getThemeId(getCurrentPrimaryColor()))

        if (!baseConfig.isUsingSystemTheme) {
            updateBackgroundColor(getCurrentBackgroundColor())
            updateStatusbarColor(getCurrentStatusBarColor())
        }

        curPrimaryLineColorPicker?.getSpecificColor()?.apply {
            updateTopBarColors(binding.customizationToolbar, pColor = this)
            setTheme(getThemeId(this))
        }

        setupToolbar(binding.customizationToolbar, NavigationIcon.Cross)
        binding.actionBarRadioGroup.beGone()
    }

    private fun refreshMenuItems() {
        binding.customizationToolbar.menu.findItem(R.id.save).isVisible = hasUnsavedChanges
    }

    private fun setupOptionsMenu() {
        binding.customizationToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.save -> {
                    saveChanges()
                    true
                }
                else -> false
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (hasUnsavedChanges && System.currentTimeMillis() - lastSavePromptTS > SAVE_DISCARD_PROMPT_INTERVAL) {
            promptSaveDiscard()
        } else {
            super.onBackPressed()
        }
    }

    private fun setupThemes() {
        predefinedThemes.apply {
            if (isSPlus()) {
                put(THEME_SYSTEM, getSystemThemeColors())
            }

            put(THEME_AUTO, getAutoThemeColors())
            put(
                THEME_LIGHT,
                MyTheme(
                    getString(R.string.light_theme),
                    R.color.theme_light_text_color,
                    R.color.theme_light_background_color,
                    R.color.color_primary,
                    R.color.color_accent
                )
            )
            put(
                THEME_DARK,
                MyTheme(
                    getString(R.string.dark_theme),
                    R.color.theme_dark_text_color,
                    R.color.theme_dark_background_color,
                    R.color.color_primary,
                    R.color.color_accent
                )
            )
            put(
                THEME_SOLARIZED,
                MyTheme(getString(R.string.solarized),
                    R.color.theme_solarized_text_color,
                    R.color.theme_solarized_background_color,
                    R.color.theme_solarized_primary_color,
                    R.color.md_blue_700_dark)
            )
            put(
                THEME_DARK_RED,
                MyTheme(
                    getString(R.string.dark_red),
                    R.color.theme_dark_text_color,
                    R.color.theme_dark_background_color,
                    R.color.theme_dark_red_primary_color,
                    R.color.md_red_700
                )
            )
            put(THEME_WHITE, MyTheme(getString(R.string.white), R.color.dark_grey,
                android.R.color.white, android.R.color.white, R.color.color_accent))
            put(
                THEME_BLACK_WHITE,
                MyTheme(getString(R.string.black_white), android.R.color.white,
                    android.R.color.black, android.R.color.black, R.color.md_grey_black)
            )
            put(THEME_CUSTOM, MyTheme(getString(R.string.custom), 0, 0, 0, 0))

        }
        setupThemePicker()
        setupColorsPickers()
    }

    private fun setupThemePicker() {
        curSelectedThemeId = getCurrentThemeId()
        binding.customizationTheme.text = getThemeText()
        updateAutoThemeFields()
        binding.customizationThemeHolder.setOnClickListener {
            themePickerClicked()
            /*} else {
                ConfirmationDialog(this, "", R.string.app_icon_color_warning, R.string.ok, 0) {
                    baseConfig.wasAppIconCustomizationWarningShown = true
                    themePickerClicked()
                }
            }*/
        }
    }

    private fun themePickerClicked() {
        val items = arrayListOf<RadioItem>()
        for ((key, value) in predefinedThemes) {
            items.add(RadioItem(key, value.label))
        }

        RadioGroupDialog(this@CustomizationActivity, items, curSelectedThemeId) {
            updateColorTheme(it as Int, true)
            // && it != THEME_SHARED
            if (it != THEME_CUSTOM && it != THEME_AUTO && it != THEME_SYSTEM && !baseConfig.wasCustomThemeSwitchDescriptionShown) {
                baseConfig.wasCustomThemeSwitchDescriptionShown = true
                toast(R.string.changing_color_description, Toast.LENGTH_LONG)
            }

            updateMenuItemColors(binding.customizationToolbar.menu)
            setupToolbar(binding.customizationToolbar, NavigationIcon.Cross,
                primaryColor = curPrimaryColor, bgColor = curBackgroundColor)
            updateStatusbarColor(getCurrentStatusBarColor())
            updateNavigationBarColor(curBackgroundColor)
        }
    }

    private fun updateColorTheme(themeId: Int, useStored: Boolean = false) {
        curSelectedThemeId = themeId
        binding.customizationTheme.text = getThemeText()

        resources.apply {
            if (curSelectedThemeId == THEME_CUSTOM) {
                if (useStored) {
                    curTextColor = baseConfig.customTextColor
                    curBackgroundColor = baseConfig.customBackgroundColor
                    curPrimaryColor = baseConfig.customPrimaryColor
                    curAccentColor = baseConfig.customAccentColor
                    setTheme(getThemeId(curPrimaryColor))
                    updateMenuItemColors(binding.customizationToolbar.menu, curPrimaryColor)
                    setupToolbar(binding.customizationToolbar, NavigationIcon.Cross, primaryColor =  curPrimaryColor)
                    setupColorsPickers()
                } else {
                    baseConfig.customPrimaryColor = curPrimaryColor
                    baseConfig.customAccentColor = curAccentColor
                    baseConfig.customBackgroundColor = curBackgroundColor
                    baseConfig.customTextColor = curTextColor
                }
            } else {
                val theme = predefinedThemes[curSelectedThemeId]!!
                curTextColor = getColor(theme.textColorId, null)
                curBackgroundColor = getColor(theme.backgroundColorId, null)

                if (curSelectedThemeId != THEME_AUTO && curSelectedThemeId != THEME_SYSTEM) {
                    curPrimaryColor = getColor(theme.primaryColorId, null)
                    curAccentColor = getColor(theme.accentColorId, null)
                }

                setTheme(getThemeId(getCurrentPrimaryColor()))
                colorChanged()
                updateMenuItemColors(binding.customizationToolbar.menu)
                setupToolbar(binding.customizationToolbar, NavigationIcon.Cross, primaryColor = curPrimaryColor, bgColor = curBackgroundColor)
                updateNavigationBarColor(curBackgroundColor)
            }
        }

        hasUnsavedChanges = true
        refreshMenuItems()
        updateLabelColors(getCurrentTextColor())
        updateBackgroundColor(getCurrentBackgroundColor())
        updateStatusbarColor(getCurrentStatusBarColor())
        updateAutoThemeFields()
    }

    private fun getAutoThemeColors(): MyTheme {
        val isUsingSystemDarkTheme = isUsingSystemDarkTheme()
        val textColor = if (isUsingSystemDarkTheme) R.color.theme_dark_text_color else R.color.theme_light_text_color
        val backgroundColor = if (isUsingSystemDarkTheme) R.color.theme_dark_background_color else R.color.theme_light_background_color
        return MyTheme(getString(R.string.auto_light_dark_theme), textColor, backgroundColor, R.color.color_primary, R.color.color_accent)
    }

    // doesn't really matter what colors we use here, everything will be taken from the system. Use the default dark theme values here.
    private fun getSystemThemeColors(): MyTheme {
        return MyTheme(
            getMaterialYouString(),
            R.color.theme_dark_text_color,
            R.color.theme_dark_background_color,
            R.color.color_primary,
            R.color.color_accent
        )
    }

    private fun getCurrentThemeId(): Int {
        /*if (baseConfig.isUsingSharedTheme) {
            return THEME_SHARED
        } else */
        if ((baseConfig.isUsingSystemTheme && !hasUnsavedChanges) || curSelectedThemeId == THEME_SYSTEM) {
            return THEME_SYSTEM
        } else if (baseConfig.isUsingAutoTheme || curSelectedThemeId == THEME_AUTO) {
            return THEME_AUTO
        }

        var themeId = THEME_CUSTOM
        resources.apply {
            //&& it.key != THEME_SHARED
            for ((key, value) in predefinedThemes.filter { it.key != THEME_CUSTOM && it.key != THEME_AUTO && it.key != THEME_SYSTEM }) {
                if (curTextColor == getColor(value.textColorId, null) &&
                    curBackgroundColor == getColor(value.backgroundColorId, null) &&
                    curPrimaryColor == getColor(value.primaryColorId, null) &&
                    curAccentColor == getColor(value.accentColorId, null)
                ) {
                    themeId = key
                }
            }
        }

        return themeId
    }

    private fun getThemeText(): String {
        var label = getString(R.string.custom)
        for ((key, value) in predefinedThemes) {
            if (key == curSelectedThemeId) {
                label = value.label
            }
        }
        return label
    }

    private fun updateAutoThemeFields() {
        arrayOf(binding.customizationTextColorHolder, binding.customizationBackgroundColorHolder).forEach {
            it.beVisibleIf(curSelectedThemeId != THEME_AUTO && curSelectedThemeId != THEME_SYSTEM)
        }

        binding.customizationPrimaryColorHolder.beVisibleIf(curSelectedThemeId != THEME_SYSTEM)
    }

    private fun promptSaveDiscard() {
        lastSavePromptTS = System.currentTimeMillis()
        ConfirmationAdvancedDialog(this, "", R.string.save_before_closing, R.string.save, R.string.discard) {
            if (it) {
                saveChanges()
            } else {
                //resetColors()
                finish()
            }
        }
    }

    private fun saveChanges() {
        baseConfig.apply {
            textColor = curTextColor
            backgroundColor = curBackgroundColor
            primaryColor = curPrimaryColor
            accentColor = curAccentColor
            themeType = getThemeText()
        }
        baseConfig.isUsingAutoTheme = curSelectedThemeId == THEME_AUTO
        baseConfig.isUsingSystemTheme = curSelectedThemeId == THEME_SYSTEM

        hasUnsavedChanges = false
        //if (finishAfterSave) {
            finish()
        /*} else {
            refreshMenuItems()
        }*/
    }

    /*private fun resetColors() {
        hasUnsavedChanges = false
        initColorVariables()
        setupColorsPickers()
        updateBackgroundColor()
        updateActionbarColor()
        refreshMenuItems()
        updateLabelColors(getCurrentTextColor())
    }*/

    private fun initColorVariables() {
        curTextColor = baseConfig.textColor
        curBackgroundColor = baseConfig.backgroundColor
        curPrimaryColor = baseConfig.primaryColor
        curAccentColor = baseConfig.accentColor
    }

    private fun setupColorsPickers() {
        val textColor = getCurrentTextColor()
        val backgroundColor = getCurrentBackgroundColor()
        val primaryColor = getCurrentPrimaryColor()
        binding.customizationTextColor.setFillWithStroke(textColor, backgroundColor)
        binding.customizationPrimaryColor.setFillWithStroke(primaryColor, backgroundColor)
        binding.customizationAccentColor.setFillWithStroke(curAccentColor, backgroundColor)
        binding.customizationBackgroundColor.setFillWithStroke(backgroundColor, backgroundColor)
        //binding.customizationAppIconColor.setFillWithStroke(curAppIconColor, backgroundColor)
        //apply_to_all.setTextColor(primaryColor.getContrastColor())

        binding.customizationTextColorHolder.setOnClickListener { pickTextColor() }
        binding.customizationBackgroundColorHolder.setOnClickListener { pickBackgroundColor() }
        binding.customizationPrimaryColorHolder.setOnClickListener { pickPrimaryColor() }
        binding.customizationAccentColorHolder.setOnClickListener { pickAccentColor() }
        binding.customizationAppIconColorHolder.beGone()
    }

    private fun hasColorChanged(old: Int, new: Int) = abs(old - new) > 1

    private fun colorChanged() {
        hasUnsavedChanges = true
        setupColorsPickers()
        refreshMenuItems()
    }

    private fun setCurrentTextColor(color: Int) {
        curTextColor = color
        updateLabelColors(color)
    }

    private fun setCurrentBackgroundColor(color: Int) {
        curBackgroundColor = color
        updateBackgroundColor(color)
        updateStatusbarColor(color)
    }

    private fun setCurrentPrimaryColor(color: Int) {
        curPrimaryColor = color
        setupToolbar(binding.customizationToolbar, NavigationIcon.Cross, primaryColor = color, bgColor = curBackgroundColor)
    }

    /*private fun updateApplyToAllColors(newColor: Int) {
        if (newColor == baseConfig.primaryColor && !baseConfig.isUsingSystemTheme) {
            apply_to_all.setBackgroundResource(R.drawable.button_background_rounded)
        } else {
            val applyBackground = resources.getDrawable(R.drawable.button_background_rounded, theme) as RippleDrawable
            (applyBackground as LayerDrawable).findDrawableByLayerId(R.id.button_background_holder).applyColorFilter(newColor)
            apply_to_all.background = applyBackground
        }
    }*/

    private fun isCurrentWhiteTheme() = curTextColor == DARK_GREY && curPrimaryColor == Color.WHITE && curBackgroundColor == Color.WHITE

    private fun isCurrentBlackAndWhiteTheme() = curTextColor == Color.WHITE && curPrimaryColor == Color.BLACK && curBackgroundColor == Color.BLACK

    private fun pickTextColor() {
        ColorPickerDialog(this, curTextColor) { wasPositivePressed, color ->
            if (wasPositivePressed) {
                if (hasColorChanged(curTextColor, color)) {
                    setCurrentTextColor(color)
                    colorChanged()
                    updateColorTheme(getCurrentThemeId())//getUpdatedTheme())
                }
            }
        }
    }

    private fun pickBackgroundColor() {
        ColorPickerDialog(this, curBackgroundColor) { wasPositivePressed, color ->
            if (wasPositivePressed) {
                if (hasColorChanged(curBackgroundColor, color)) {
                    setCurrentBackgroundColor(color)
                    colorChanged()
                    updateColorTheme(getCurrentThemeId()) //getUpdatedTheme())
                }
            }
        }
    }

    private fun pickPrimaryColor() {
        curPrimaryLineColorPicker = LineColorPickerDialog(this, curPrimaryColor,
            true, toolbar = binding.customizationToolbar) { wasPositivePressed, color ->
            curPrimaryLineColorPicker = null
            if (wasPositivePressed) {
                if (hasColorChanged(curPrimaryColor, color)) {
                    setCurrentPrimaryColor(color)
                    colorChanged()
                    updateColorTheme(getCurrentThemeId()) //getUpdatedTheme())
                    setTheme(getThemeId(color))
                }
                updateMenuItemColors(binding.customizationToolbar.menu, color)
                setupToolbar(binding.customizationToolbar, NavigationIcon.Cross, color)
            } else {
                setTheme(getThemeId(curPrimaryColor))
                updateMenuItemColors(binding.customizationToolbar.menu, curPrimaryColor)
                setupToolbar(binding.customizationToolbar, NavigationIcon.Cross, primaryColor =  curPrimaryColor)
                updateTopBarColors(binding.customizationToolbar, curPrimaryColor)
            }
        }
    }

    private fun pickAccentColor() {
        ColorPickerDialog(this, curAccentColor) { wasPositivePressed, color ->
            if (wasPositivePressed) {
                if (hasColorChanged(curAccentColor, color)) {
                    curAccentColor = color
                    colorChanged()

                    if (isCurrentWhiteTheme() || isCurrentBlackAndWhiteTheme()) {
                        setupToolbar(binding.customizationToolbar, NavigationIcon.Cross, primaryColor = getCurrentStatusBarColor())
                    }
                }
            }
        }
    }

    //private fun getUpdatedTheme() = if (curSelectedThemeId == THEME_SHARED) THEME_SHARED else getCurrentThemeId()

    /*private fun applyToAll() {
        if (isThankYouInstalled()) {
            ConfirmationDialog(this, "", R.string.share_colors_success, R.string.ok, 0) {
                Intent().apply {
                    action = MyContentProvider.SHARED_THEME_ACTIVATED
                    sendBroadcast(this)
                }

                if (!predefinedThemes.containsKey(THEME_SHARED)) {
                    predefinedThemes[THEME_SHARED] = MyTheme(getString(R.string.shared), 0, 0, 0, 0)
                }

                baseConfig.wasSharedThemeEverActivated = true
                apply_to_all_holder.beGone()
                updateColorTheme(THEME_SHARED)
                saveChanges(false)
            }
        } else {
            PurchaseThankYouDialog(this)
        }
    }*/

    private fun updateLabelColors(textColor: Int) {
        arrayListOf<MyTextView>(
            binding.customizationThemeLabel,
            binding.customizationTheme,
            binding.customizationTextColorLabel,
            binding.customizationBackgroundColorLabel,
            binding.customizationPrimaryColorLabel,
            binding.customizationAccentColorLabel
        ).forEach {
            it.setTextColor(textColor)
        }
    }

    private fun getCurrentTextColor() = if (binding.customizationTheme.value == getMaterialYouString()) {
        resources.getColor(R.color.you_neutral_text_color, null)
    } else {
        curTextColor
    }

    private fun getCurrentBackgroundColor() = if (binding.customizationTheme.value == getMaterialYouString()) {
        resources.getColor(R.color.you_background_color, null)
    } else {
        curBackgroundColor
    }

    private fun getCurrentPrimaryColor() = if (binding.customizationTheme.value == getMaterialYouString()) {
        resources.getColor(R.color.you_primary_color, null)
    } else {
        curPrimaryColor
    }

    private fun getCurrentStatusBarColor() = if (binding.customizationTheme.value == getMaterialYouString()) {
        resources.getColor(R.color.you_status_bar_color, null)
    } /*else if (baseConfig.actionBarStyle == ACTION_BAR_LEGACY) {
        curPrimaryColor
    }*/ else curBackgroundColor

    private fun getMaterialYouString() = "${getString(R.string.system_default)} (${getString(R.string.material_you)})"
}
