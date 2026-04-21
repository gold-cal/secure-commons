package com.secure.commons.extensions

import android.app.Activity
import android.graphics.Color
import com.secure.commons.R
import com.secure.commons.helpers.DARK_GREY

fun Activity.getThemeId(showTransparentTop: Boolean = false) = when {
    baseConfig.isUsingSystemTheme -> if (isUsingSystemDarkTheme()) R.style.AppTheme_Base_System else R.style.AppTheme_Base_System_Light
    isBlackAndWhiteTheme() -> when {
        showTransparentTop -> R.style.AppTheme_BlackAndWhite_NoActionBar
        baseConfig.primaryColor.getContrastColor() == DARK_GREY -> R.style.AppTheme_BlackAndWhite_DarkTextColor
        else -> R.style.AppTheme_BlackAndWhite
    }
    isWhiteTheme() -> when {
        showTransparentTop -> R.style.AppTheme_White_NoActionBar
        baseConfig.primaryColor.getContrastColor() == Color.WHITE -> R.style.AppTheme_White_LightTextColor
        else -> R.style.AppTheme_White
    }
    isLightBackground() -> R.style.AppTheme_LightStyle
    showTransparentTop -> R.style.AppTheme_Default_Core
    else -> R.style.AppTheme_Default
}
