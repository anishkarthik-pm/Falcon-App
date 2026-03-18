package com.kpn.falcon.util

import android.content.Context

// Full implementation wired via Activity result contracts in Task 5 (Add Property Wizard).
// Stub provided here to satisfy expect/actual compilation.
actual class CameraProvider(private val context: Context) {

    actual suspend fun capturePhoto(): CapturedMedia? {
        // TODO: Wire CameraX + ActivityResultLauncher in Task 5
        return null
    }

    actual suspend fun pickFromGallery(allowMultiple: Boolean): List<CapturedMedia> {
        // TODO: Wire Android Photo Picker in Task 5
        return emptyList()
    }

    actual suspend fun captureVideo(): CapturedMedia? {
        // TODO: Wire video capture in Task 5
        return null
    }
}
