package com.kpn.falcon.presentation.screens.wizard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kpn.falcon.data.api.MediaType
import com.kpn.falcon.data.repository.PropertyRepository
import com.kpn.falcon.presentation.components.*
import com.kpn.falcon.presentation.viewmodels.AddPropertyUiState
import com.kpn.falcon.presentation.viewmodels.AddPropertyViewModel
import com.kpn.falcon.util.*
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun Step4MediaContent(
    viewModel: AddPropertyViewModel,
    state: AddPropertyUiState
) {
    val scope = rememberCoroutineScope()
    val cameraProvider = koinInject<CameraProvider>()
    val m = state.media
    val errors = state.validationErrors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        WizardStepHeader(title = Strings.MEDIA_HEADER, fieldProgress = state.stepProgress)

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Exterior Photos
            MediaUploadSlot(
                label = Strings.MEDIA_EXTERIOR,
                uploadedCount = m.exteriorPhotos.size,
                minRequired = KPNConstants.MIN_EXTERIOR_PHOTOS,
                onTap = {
                    scope.launch {
                        val picked = cameraProvider.pickFromGallery(allowMultiple = true)
                        picked.forEach { media ->
                            // In Task 11 this will upload to server; for now add a local URL stub
                            viewModel.addExteriorPhoto("local://${media.fileName}")
                        }
                    }
                }
            )
            if (errors.containsKey("exteriorPhotos")) {
                DeviationFlag(message = errors["exteriorPhotos"]!!)
            }

            // Internal Photos
            MediaUploadSlot(
                label = Strings.MEDIA_INTERNAL,
                uploadedCount = m.internalPhotos.size,
                minRequired = KPNConstants.MIN_INTERNAL_PHOTOS,
                onTap = {
                    scope.launch {
                        val picked = cameraProvider.pickFromGallery(allowMultiple = true)
                        picked.forEach { viewModel.addInternalPhoto("local://${it.fileName}") }
                    }
                }
            )
            if (errors.containsKey("internalPhotos")) {
                DeviationFlag(message = errors["internalPhotos"]!!)
            }

            // Competitor Photos
            MediaUploadSlot(
                label = Strings.MEDIA_COMPETITOR,
                uploadedCount = m.competitorPhotos.size,
                minRequired = KPNConstants.MIN_COMPETITOR_PHOTOS,
                onTap = {
                    scope.launch {
                        val picked = cameraProvider.pickFromGallery(allowMultiple = true)
                        picked.forEach { viewModel.addCompetitorPhoto("local://${it.fileName}") }
                    }
                }
            )
            if (errors.containsKey("competitorPhotos")) {
                DeviationFlag(message = errors["competitorPhotos"]!!)
            }

            // Videos
            MediaUploadSlot(
                label = Strings.MEDIA_VIDEOS,
                uploadedCount = m.videos.size,
                minRequired = KPNConstants.MIN_VIDEOS,
                onTap = {
                    scope.launch {
                        val video = cameraProvider.captureVideo()
                        if (video != null) viewModel.addVideo("local://${video.fileName}")
                    }
                }
            )
            if (errors.containsKey("videos")) {
                DeviationFlag(message = errors["videos"]!!)
            }

            // Documents (no minimum)
            MediaUploadSlot(
                label = Strings.MEDIA_DOCUMENTS,
                uploadedCount = m.documents.size,
                minRequired = 0,
                onTap = {
                    scope.launch {
                        val filePicker = koinInject<FilePicker>()
                        val doc = filePicker.pickDocument()
                        if (doc != null) viewModel.addDocument("local://${doc.fileName}")
                    }
                }
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}
