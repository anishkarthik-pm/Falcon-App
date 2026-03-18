package com.kpn.falcon.util

// Full implementation via UIDocumentPickerViewController wired in Task 12 (iOS parity).
actual class FilePicker {

    actual suspend fun pickPdf(): PickedFile? {
        // TODO: Wire UIDocumentPickerViewController in Task 12
        return null
    }

    actual suspend fun pickDocument(): PickedFile? {
        // TODO: Wire UIDocumentPickerViewController in Task 12
        return null
    }
}
