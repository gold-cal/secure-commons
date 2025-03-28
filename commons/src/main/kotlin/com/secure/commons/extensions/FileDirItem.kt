package com.secure.commons.extensions

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.secure.commons.models.FileDirItem

fun FileDirItem.isRecycleBinPath(context: Context): Boolean {
    return path.startsWith(context.recycleBinPath)
}

fun FileDirItem.assembleContentUri(): Uri {
    val uri = when {
        path.isImageFast() -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        path.isVideoFast() -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        else -> MediaStore.Files.getContentUri("external")
    }

    return Uri.withAppendedPath(uri, mediaStoreId.toString())
}
