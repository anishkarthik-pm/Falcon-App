package com.kpn.falcon.util

data class CapturedMedia(
    val bytes: ByteArray,
    val mimeType: String,
    val fileName: String,
    val geoTag: GpsCoordinates?,
    val capturedAt: Long
)

expect class CameraProvider {
    suspend fun capturePhoto(): CapturedMedia?
    suspend fun pickFromGallery(allowMultiple: Boolean): List<CapturedMedia>
    suspend fun captureVideo(): CapturedMedia?
}
