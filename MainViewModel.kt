package co.edu.iudigital.radio.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.iudigital.radio.data.model.Station
import co.edu.iudigital.radio.data.model.UserProfile
import co.edu.iudigital.radio.data.repository.StationRepositoryImpl
import co.edu.iudigital.radio.domain.usecase.GetStationsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PlayerState(
    val isPlaying: Boolean = false,
    val isMuted: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class MainViewModel : ViewModel() {
    private val repository = StationRepositoryImpl()
    private val getStationsUseCase = GetStationsUseCase(repository)
    
    private val _uiState = MutableStateFlow(PlayerState())
    val uiState: StateFlow<PlayerState> = _uiState.asStateFlow()
    
    private val _stations = MutableStateFlow<List<Station>>(emptyList())
    val stations: StateFlow<List<Station>> = _stations.asStateFlow()
    
    private val _currentStation = MutableStateFlow<Station?>(null)
    val currentStation: StateFlow<Station?> = _currentStation.asStateFlow()
    
    private val _userProfile = MutableStateFlow(UserProfile("Usuario", null))
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()
    
    private val _hasCameraPermission = MutableStateFlow(false)
    val hasCameraPermission: StateFlow<Boolean> = _hasCameraPermission.asStateFlow()
    
    init {
        viewModelScope.launch {
            getStationsUseCase().collect { stationList ->
                _stations.value = stationList
                if (_currentStation.value == null && stationList.isNotEmpty()) {
                    _currentStation.value = stationList.first()
                }
            }
        }
    }
    
    fun togglePlayback() {
        _uiState.update { it.copy(isPlaying = !it.isPlaying) }
        // In a real app, this would control the ExoPlayer
    }
    
    fun toggleMute() {
        _uiState.update { it.copy(isMuted = !it.isMuted) }
    }
    
    fun selectStation(station: Station) {
        _currentStation.value = station
        _uiState.update { it.copy(isPlaying = true) }
    }
    
    fun playNextStation() {
        val currentIndex = _stations.value.indexOfFirst { it.id == _currentStation.value?.id }
        if (currentIndex >= 0 && currentIndex < _stations.value.size - 1) {
            _currentStation.value = _stations.value[currentIndex + 1]
        }
    }
    
    fun playPreviousStation() {
        val currentIndex = _stations.value.indexOfFirst { it.id == _currentStation.value?.id }
        if (currentIndex > 0) {
            _currentStation.value = _stations.value[currentIndex - 1]
        }
    }
    
    fun updateUserName(name: String) {
        _userProfile.update { it.copy(name = name) }
    }
    
    fun updateProfileImage(uri: String) {
        _userProfile.update { it.copy(profileImageUri = uri) }
    }
    
    fun updateCameraPermission(granted: Boolean) {
        _hasCameraPermission.value = granted
    }
}
