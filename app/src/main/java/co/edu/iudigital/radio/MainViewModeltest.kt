package co.edu.iudigital.radio.viewmodel

import co.edu.iudigital.radio.ui.viewmodel.MainViewModel
import co.edu.iudigital.radio.ui.viewmodel.PlayerState
import junit.framework.Assert.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class MainViewModelTest {
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel()
    }

    @Test
    fun `initial state is correct`() = runBlocking {
        val state = viewModel.uiState.first()
        assertEquals(PlayerState(), state)
    }

    @Test
    fun `togglePlayback changes isPlaying state`() = runBlocking {
        viewModel.togglePlayback()
        val state = viewModel.uiState.first()
        assertTrue(state.isPlaying)
        
        viewModel.togglePlayback()
        val newState = viewModel.uiState.first()
        assertFalse(newState.isPlaying)
    }

    @Test
    fun `toggleMute changes isMuted state`() = runBlocking {
        viewModel.toggleMute()
        val state = viewModel.uiState.first()
        assertTrue(state.isMuted)
        
        viewModel.toggleMute()
        val newState = viewModel.uiState.first()
        assertFalse(newState.isMuted)
    }
}
