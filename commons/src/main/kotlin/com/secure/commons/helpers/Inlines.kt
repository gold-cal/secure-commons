package com.secure.commons.helpers

import android.content.Intent
import android.os.Build
import java.io.Serializable

inline fun <reified T : Serializable?> Intent.serializable(key: String): T = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)!!
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as T
}

inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long) = this.map { selector(it) }.sum()

inline fun <T> Iterable<T>.sumByInt(selector: (T) -> Int) = this.map { selector(it) }.sum()
