package co.edu.iudigital.radio.ui

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import co.edu.iudigital.radio.data.InMemoryStationRepository
import co.edu.iudigital.radio.data.StationRepository
import co.edu.iudigital.radio.ui.state.RadioUiState

class RadioViewModel(
    private val stationRepository: StationRepository
) : ViewModel() {

    constructor() : this(InMemoryStationRepository())

    var uiState by mutableStateOf(
        RadioUiState(
            stations = stationRepository.getStations()
        )
    )
        private set

    fun onPlayPauseClick() {
        uiState = uiState.copy(isPlaying = !uiState.isPlaying)
    }

    fun onMuteClick() {
        uiState = uiState.copy(isMuted = !uiState.isMuted)
    }

    fun onStationSelected(index: Int) {
        if (index !in uiState.stations.indices) return
        uiState = uiState.copy(
            selectedStationIndex = index,
            isPlaying = true
        )
    }

    fun onPhotoCaptured(bitmap: Bitmap?) {
        if (bitmap != null) {
            uiState = uiState.copy(userPhoto = bitmap)
        }
    }
}
