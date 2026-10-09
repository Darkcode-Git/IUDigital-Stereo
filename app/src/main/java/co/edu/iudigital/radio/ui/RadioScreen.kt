package co.edu.iudigital.radio.ui

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.iudigital.radio.model.Station
import co.edu.iudigital.radio.ui.state.RadioUiState
import co.edu.iudigital.radio.util.hasCameraPermission
import co.edu.iudigital.radio.util.triggerHapticFeedback

@Composable
fun RadioRoute(viewModel: RadioViewModel) {
    val context = LocalContext.current
    val uiState = viewModel.uiState

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = viewModel::onPhotoCaptured
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    RadioScreen(
        uiState = uiState,
        onCapturePhotoClick = {
            if (hasCameraPermission(context)) {
                cameraLauncher.launch(null)
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        },
        onPlayPauseClick = {
            triggerHapticFeedback(context)
            viewModel.onPlayPauseClick()
        },
        onMuteClick = {
            triggerHapticFeedback(context)
            viewModel.onMuteClick()
        },
        onStationSelected = { index ->
            triggerHapticFeedback(context)
            viewModel.onStationSelected(index)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioScreen(
    uiState: RadioUiState,
    onCapturePhotoClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onMuteClick: () -> Unit,
    onStationSelected: (Int) -> Unit
) {
    val currentStation = uiState.currentStation

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("IU Digital Radio", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF083C65))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileCard(
                userPhotoAvailable = uiState.userPhoto != null,
                userPhotoState = uiState.userPhoto?.asImageBitmap(),
                onCapturePhotoClick = onCapturePhotoClick
            )

            NowPlayingCard(
                stationName = currentStation?.name ?: "Sin emisora",
                stationGenre = currentStation?.genre ?: "Sin género",
                isPlaying = uiState.isPlaying,
                isMuted = uiState.isMuted,
                onPlayPauseClick = onPlayPauseClick,
                onMuteClick = onMuteClick
            )

            Text(
                text = "Emisoras Disponibles",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF083C65)
            )

            StationList(
                stations = uiState.stations,
                currentStationId = currentStation?.id,
                isPlaying = uiState.isPlaying,
                onStationSelected = onStationSelected
            )
        }
    }
}

@Composable
private fun ProfileCard(
    userPhotoAvailable: Boolean,
    userPhotoState: ImageBitmap?,
    onCapturePhotoClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (userPhotoAvailable && userPhotoState != null) {
                    Image(
                        bitmap = userPhotoState,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFF9A1515), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF083C65)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Radio,
                            contentDescription = "Avatar por defecto",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text("Estudiante IU Digital", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Oyente Activo", color = Color.Gray, fontSize = 14.sp)
                }
            }

            IconButton(
                onClick = onCapturePhotoClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF9A1515))
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Tomar Foto",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun NowPlayingCard(
    stationName: String,
    stationGenre: String,
    isPlaying: Boolean,
    isMuted: Boolean,
    onPlayPauseClick: () -> Unit,
    onMuteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF083C65)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SONANDO AHORA",
                color = Color(0xFFFFD700),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stationName,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stationGenre,
                color = Color.LightGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledIconButton(
                    onClick = onMuteClick,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (isMuted) {
                            Color(0xFF9A1515)
                        } else {
                            Color.White.copy(alpha = 0.2f)
                        }
                    )
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.Radio,
                        contentDescription = "Mute",
                        tint = Color.White
                    )
                }

                Button(
                    onClick = onPlayPauseClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9A1515)),
                    shape = CircleShape,
                    modifier = Modifier.size(64.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pausa" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StationList(
    stations: List<Station>,
    currentStationId: Int?,
    isPlaying: Boolean,
    onStationSelected: (Int) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(stations) { index, station ->
            val isSelected = station.id == currentStationId

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStationSelected(index) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
                ),
                border = if (isSelected) ButtonDefaults.outlinedButtonBorder else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = station.name,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color(0xFF083C65) else Color.Black
                        )
                        Text(
                            text = station.genre,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    if (isSelected && isPlaying) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Reproduciendo",
                            tint = Color(0xFF9A1515)
                        )
                    }
                }
            }
        }
    }
}
