package co.edu.iudigital.radio.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import co.edu.iudigital.radio.data.model.Station
import co.edu.iudigital.radio.ui.components.NowPlayingCard
import co.edu.iudigital.radio.ui.components.PlayerControls
import co.edu.iudigital.radio.ui.components.SpectrumVisualizer
import co.edu.iudigital.radio.ui.components.StationItem
import co.edu.iudigital.radio.ui.viewmodel.MainViewModel
import co.edu.iudigital.radio.utils.PermissionHelper
import co.edu.iudigital.radio.utils.VibrationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val stations by viewModel.stations.collectAsState()
    val selectedStation by viewModel.currentStation.collectAsState()
    val hasCameraPermission by viewModel.hasCameraPermission.collectAsState()
    
    // Permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.updateCameraPermission(isGranted)
    }
    
    // Request camera permission on first launch if needed
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!PermissionHelper.hasCameraPermission(context)) {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            } else {
                viewModel.updateCameraPermission(true)
            }
        } else {
            viewModel.updateCameraPermission(true)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "IU Digital Radio",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Spectrum visualizer
            SpectrumVisualizer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                isPlaying = uiState.isPlaying
            )
            
            // Now playing card
            NowPlayingCard(
                station = selectedStation,
                isPlaying = uiState.isPlaying
            )
            
            // Player controls
            PlayerControls(
                playerState = uiState,
                onPlayPause = { 
                    viewModel.togglePlayback()
                    VibrationHelper.vibrate(context)
                },
                onMute = { 
                    viewModel.toggleMute()
                    VibrationHelper.vibrate(context)
                },
                onNext = { 
                    viewModel.playNextStation()
                    VibrationHelper.vibrate(context)
                },
                onPrevious = { 
                    viewModel.playPreviousStation()
                    VibrationHelper.vibrate(context)
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Stations header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Emisoras",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${stations.size} stations",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            // Stations list
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(stations) { station ->
                    StationItem(
                        station = station,
                        isSelected = station.id == selectedStation?.id,
                        onClick = { 
                            viewModel.selectStation(station)
                            VibrationHelper.vibrate(context)
                        }
                    )
                }
            }
        }
    }
}
