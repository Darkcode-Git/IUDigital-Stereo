package co.edu.iudigital.radio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

data class Station(val id: Int, val name: String, val genre: String, val streamUrl: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF083C65),
                    secondary = Color(0xFF9A1515)
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF8F9FA)
                ) {
                    IUDigitalRadioApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IUDigitalRadioApp() {
    val context = LocalContext.current

    val stations = remember {
        listOf(
            Station(1, "IU Digital Stereo", "Institucional & Noticias", "http://174.142.111.104:9974/;"),
            Station(2, "Emisora Cultural Antioquia", "Música & Cultura", "http://stream.cultural.co/live"),
            Station(3, "Radio Académica FM", "Conferencias & Podcast", "http://stream.academica.edu/fm"),
            Station(4, "UdeA Radio 100.9 FM", "Universitaria", "http://emisora.udea.edu.co/live")
        )
    }

    var isPlaying by rememberSaveable { mutableStateOf(false) }
    var isMuted by rememberSaveable { mutableStateOf(false) }
    var selectedStationIndex by rememberSaveable { mutableStateOf(0) }
    var userPhoto by remember { mutableStateOf<Bitmap?>(null) }

    val currentStation = stations[selectedStationIndex]

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            userPhoto = bitmap
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    fun triggerHapticFeedback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            vibrator.vibrate(VibrationEffect.createOneShot(50L, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            @Suppress("DEPRECATION")
            vibrator.vibrate(50L)
        }
    }

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
                        if (userPhoto != null) {
                            Image(
                                bitmap = userPhoto!!.asImageBitmap(),
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
                        onClick = {
                            val permissionCheck = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            )
                            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                cameraLauncher.launch(null)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
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
                        text = currentStation.name,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = currentStation.genre,
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledIconButton(
                            onClick = {
                                triggerHapticFeedback()
                                isMuted = !isMuted
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = if (isMuted) Color(0xFF9A1515) else Color.White.copy(alpha = 0.2f)
                            )
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.Radio,
                                contentDescription = "Mute",
                                tint = Color.White
                            )
                        }

                        Button(
                            onClick = {
                                triggerHapticFeedback()
                                isPlaying = !isPlaying
                            },
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

            Text(
                text = "Emisoras Disponibles",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF083C65)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(stations) { station ->
                    val isSelected = station.id == currentStation.id

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                triggerHapticFeedback()
                                selectedStationIndex = stations.indexOf(station)
                                isPlaying = true
                            },
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
    }
}
