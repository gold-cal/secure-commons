package com.secure.commons.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.secure.commons.extensions.adjustAlpha
import com.secure.commons.extensions.applyColorFilter
import com.secure.commons.helpers.MEDIUM_ALPHA

class MyEditText : AppCompatEditText {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    fun setColors(textColor: Int, accentColor: Int, backgroundColor: Int) {
        background?.mutate()?.applyColorFilter(accentColor)

        // requires android:textCursorDrawable="@null" in xml to color the cursor too
        setTextColor(textColor)
        setHintTextColor(textColor.adjustAlpha(MEDIUM_ALPHA))
        setLinkTextColor(accentColor)
    }
}
