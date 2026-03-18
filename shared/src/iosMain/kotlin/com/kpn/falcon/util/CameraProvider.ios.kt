package com.kpn.falcon.util

// Full implementation via PHPicker + UIImagePickerController wired in Task 12 (iOS parity).
actual class CameraProvider {

    actual suspend fun capturePhoto(): CapturedMedia? {
        // TODO: Wire iOS PHPicker / UIImagePickerController in Task 12
        return null
    }

    actual suspend fun pickFromGallery(allowMultiple: Boolean): List<CapturedMedia> {
        // TODO: Wire iOS PHPickerViewController in Task 12
        return emptyList()
    }

    actual suspend fun captureVideo(): CapturedMedia? {
        // TODO: Wire iOS video capture in Task 12
        return null
    }
}
