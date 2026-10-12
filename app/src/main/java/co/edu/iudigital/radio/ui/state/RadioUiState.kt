package co.edu.iudigital.radio.ui.state

import android.graphics.Bitmap
import co.edu.iudigital.radio.model.Station

data class RadioUiState(
    val stations: List<Station> = emptyList(),
    val selectedStationIndex: Int = 0,
    val isPlaying: Boolean = false,
    val isMuted: Boolean = false,
    val userPhoto: Bitmap? = null
) {
    val currentStation: Station?
        get() = stations.getOrNull(selectedStationIndex)
}
