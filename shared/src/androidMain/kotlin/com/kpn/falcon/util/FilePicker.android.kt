package com.kpn.falcon.util

import android.content.Context

// Full implementation via Intent.ACTION_OPEN_DOCUMENT wired in Task 5.
actual class FilePicker(private val context: Context) {

    actual suspend fun pickPdf(): PickedFile? {
        // TODO: Wire Android file picker with Intent.ACTION_OPEN_DOCUMENT in Task 5
        return null
    }

    actual suspend fun pickDocument(): PickedFile? {
        // TODO: Wire Android document picker in Task 5
        return null
    }
}
