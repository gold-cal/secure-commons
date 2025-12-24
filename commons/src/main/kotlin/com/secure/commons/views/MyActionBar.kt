package com.secure.commons.views

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.Toolbar
import com.secure.commons.R
import com.secure.commons.activities.BaseSimpleActivity
import com.secure.commons.databinding.ActionBarSearchBinding
import com.secure.commons.extensions.*
import com.secure.commons.helpers.MEDIUM_ALPHA

class MyActionBar(context: Context, attrs: AttributeSet) : Toolbar(context, attrs) {
    var isSearchOpen = false
    var useArrowIcon = false
    var onSearchOpenListener: (() -> Unit)? = null
    var onSearchClosedListener: (() -> Unit)? = null
    var onSearchTextChangedListener: ((text: String) -> Unit)? = null
    var onNavigateBackClickListener: (() -> Unit)? = null
    val binding = ActionBarSearchBinding.inflate(LayoutInflater.from(context), this, true)

    fun getToolbar() = binding.actionBarToolbar

    fun setupMenu() {
        binding.actionBarSearchIcon.setOnClickListener {
            if (isSearchOpen) {
                closeSearch()
            } else if (useArrowIcon && onNavigateBackClickListener != null) {
                onNavigateBackClickListener!!()
            } else {
                binding.actionBarSearch.requestFocus()
                (context as? Activity)?.showKeyboard(binding.actionBarSearch)
            }
        }

        post {
            binding.actionBarSearch.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    openSearch()
                }
            }
        }

        binding.actionBarSearch.onTextChangeListener { text ->
            onSearchTextChangedListener?.invoke(text)
        }
    }

    fun focusView() {
        binding.actionBarSearch.requestFocus()
    }

    private fun openSearch() {
        isSearchOpen = true
        onSearchOpenListener?.invoke()
        binding.actionBarSearchIcon.setImageResource(R.drawable.ic_arrow_left_vector)
    }

    fun closeSearch() {
        isSearchOpen = false
        onSearchClosedListener?.invoke()
        binding.actionBarSearch.setText("")
        if (!useArrowIcon) {
            binding.actionBarSearchIcon.setImageResource(R.drawable.ic_search_vector)
        }
        (context as? Activity)?.hideKeyboard()
    }

    fun getCurrentQuery() = binding.actionBarSearch.text.toString()

    fun updateHintText(text: String) {
        binding.actionBarSearch.hint = text
    }

    /*fun toggleHideOnScroll(hideOnScroll: Boolean) {
        val params = binding.actionBarHolder.layoutParams as LayoutParams
        if (hideOnScroll) {
            params.scrollFlags = LayoutParams.SCROLL_FLAG_SCROLL or LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        } else {
            params.scrollFlags = params.scrollFlags.removeBit(LayoutParams.SCROLL_FLAG_SCROLL or LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
        }
    }*/

    fun toggleForceArrowBackIcon(useArrowBack: Boolean) {
        this.useArrowIcon = useArrowBack
        val icon = if (useArrowBack) {
            R.drawable.ic_arrow_left_vector
        } else {
            R.drawable.ic_search_vector
        }

        binding.actionBarSearchIcon.setImageResource(icon)
    }

    fun updateColors() {
        val backgroundColor = context.getProperBackgroundColor()
        val actionbarColor = context.getProperActionBarColor()
        val contrastColor = actionbarColor.getContrastColor()

        setBackgroundColor(backgroundColor)
        binding.actionBarSearchIcon.applyColorFilter(contrastColor)
        binding.actionBarHolder.background?.applyColorFilter(actionbarColor)
        binding.actionBarSearch.setTextColor(contrastColor)
        binding.actionBarSearch.setHintTextColor(contrastColor.adjustAlpha(MEDIUM_ALPHA))
        (context as? BaseSimpleActivity)?.updateTopBarColors(binding.actionBarToolbar)
    }
}
