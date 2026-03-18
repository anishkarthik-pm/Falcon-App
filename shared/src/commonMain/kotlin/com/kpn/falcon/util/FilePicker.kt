package com.kpn.falcon.util

data class PickedFile(
    val bytes: ByteArray,
    val mimeType: String,
    val fileName: String
)

expect class FilePicker {
    suspend fun pickPdf(): PickedFile?
    suspend fun pickDocument(): PickedFile?
}
